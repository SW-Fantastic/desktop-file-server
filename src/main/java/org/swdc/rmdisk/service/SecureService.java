package org.swdc.rmdisk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.password4j.Argon2Function;
import com.password4j.Hash;
import com.password4j.SaltGenerator;
import com.password4j.types.Argon2;
import io.jsonwebtoken.Jwts;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jcifs.smb.NtlmPasswordAuthentication;
import org.slf4j.Logger;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.fx.FXResources;
import org.swdc.ours.common.type.JSONMapper;
import org.swdc.rmdisk.core.AuthType;
import org.swdc.rmdisk.core.CoreSecurityKey;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.LoginedUser;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.service.events.UserStateChangeEvent;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.Duration;
import java.util.*;

@Singleton
public class SecureService implements EventEmitter {

    private static final int MAX_ATTEMPTS = 5;

    @Inject
    private UserManageService userManageService;

    @Inject
    private Logger logger;

    /**
     * 在线用户ID映射表，用于快速查找用户信息。
     */
    private Map<Long,String> onLineUserId = new HashMap<>();


    /**
     * 缓存的NTLM挑战信息，用于验证用户身份。
     */
    private Cache<String, byte[]> ntlmChallenges = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(60 * 5))
            .build();

    /**
     * 在线用户缓存，用于存储当前登录的用户信息。
     */
    private Cache<String,User> onlineUsers = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(6))
            .removalListener((key, value, cause) -> {
                if (value == null || key == null) {
                    return;
                }
                User removed = (User) value;
                onLineUserId.remove(removed.getId());
                emit(new UserStateChangeEvent(removed));
            })
            .build();

    /**
     * 用户密码错误次数缓存，用于限制登录尝试。
     */
    private Cache<String, Long> wrongPasswordAttempts = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private Events events;

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }

    @Override
    public <T extends AbstractEvent> void emit(T event) {
        events.dispatch(event);
    }


    public User requestAuth(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return onlineUsers.getIfPresent(token);
    }


    public User requestAuth(HttpServerRequest request, HttpServerResponse response) throws IOException, NoSuchAlgorithmException {

        wrongPasswordAttempts.cleanUp();
        onlineUsers.cleanUp();
        Cookie sessionKey = request.getCookie("SESSION_KEY");
        Cookie challengeCookie = request.getCookie("NTLM_CHID");
        String remoteAddress = SecureUtils.remoteAddress(request);

        Long count = wrongPasswordAttempts.getIfPresent(remoteAddress);
        if (count != null && count >= MAX_ATTEMPTS) {
            SecureUtils.setupAuthHeader(response,true);
            response.end();
            return null;
        } else if (count == null) {
            count = 0L;
        }

        AuthType type = AuthType.Unknown;
        if (challengeCookie != null) {
            type = AuthType.NTLM;
        } else {
            if(request.getHeader("Authorization") != null) {
                type = SecureUtils.getAuthType(request.getHeader("Authorization"));
            } else if (request.getParam("token") != null) {
                type = AuthType.Bearer;
            }
        }

        if (sessionKey == null) {

            if(type == AuthType.NTLM) {

                byte[] challenge = null;
                Cookie cookieChid = request.getCookie("NTLM_CHID");

                if (cookieChid == null) {
                    challenge = new byte[8];
                    SecureRandom random = new SecureRandom();
                    random.nextBytes(challenge);

                    String challengeId = UUID.randomUUID().toString();
                    ntlmChallenges.put(challengeId,challenge);
                    cookieChid = Cookie.cookie("NTLM_CHID",challengeId);
                    response.addCookie(cookieChid);
                } else {
                    String challengeId = cookieChid.getValue();
                    challenge = ntlmChallenges.getIfPresent(challengeId);
                    if (challenge == null) {
                        SecureUtils.setupAuthHeader(response,true);
                        ntlmChallenges.cleanUp();
                        response.removeCookie("NTLM_CHID");
                        response.end();
                        return null;
                    }
                }

                // NTLM 协议的认证消息，它出现在Header里面必然是进行登录流程的。
                NtlmPasswordAuthentication auth = SecureUtils.processNTMLAuth(
                        request,
                        response,
                        challenge
                );
                if (auth == null) {
                    // 认证尚未完成。
                    response.end();
                    return null;
                } else {
                    // 查找用户，执行认证
                    User user = userManageService.findByName(auth.getUsername());
                    if (user == null) {
                        response.setStatusCode(401);
                        response.putHeader("WWW-Authenticate", "NTLM");
                        response.end();
                        return null;
                    }
                    byte[] clientHash = auth.getUnicodeHash(challenge);
                    String domain = auth.getDomain();
                    boolean passwordIsCorrect = SecureUtils.checkPasswordNtlmV2(
                            domain,
                            clientHash,
                            user.getUsername(),
                            userManageService.decodePassword(user.getPassword()),
                            challenge
                    );
                    if (!passwordIsCorrect) {
                        SecureUtils.setupAuthHeader(response,true);
                        response.end();
                        wrongPasswordAttempts.put(remoteAddress, count + 1);
                        return null;
                    }
                    // 认证完毕，添加Cookie

                    String token = getLoginedUserToken(user);
                    Cookie cookie = null;
                    if (token == null) {
                        token = generateJWT(user);
                        onlineUsers.put(token,user);
                        onLineUserId.put(user.getId(),token);
                    }
                    cookie = Cookie.cookie("SESSION_KEY", token);
                    response.addCookie(cookie);
                    emit(new UserStateChangeEvent(user));
                    wrongPasswordAttempts.invalidate(remoteAddress);
                    return user;
                }
            } else if (type == AuthType.Bearer) {
                String header = request.getHeader("Authorization");
                if (header == null) {
                    header = request.getParam("token");
                } else {
                    header = header.replace("Bearer ", "").trim();
                }
                // Bearer令牌，通常是JWT，解析之。
                User user = onlineUsers.getIfPresent(header);
                if (user == null) {
                    wrongPasswordAttempts.put(remoteAddress, count + 1);
                    response.removeCookie("SESSION_KEY");
                    response.removeCookie("NTLM_CHID");
                    SecureUtils.setupAuthHeader(response,true);
                    response.end();
                    return null;
                }
                wrongPasswordAttempts.invalidate(remoteAddress);
                return user;
            } else {
                if (SecureUtils.isBrowser(request)) {
                    // 浏览器，跳转到登录页面
                    response.setStatusCode(302);
                    response.putHeader("Location", "/@web/");
                    response.end();
                } else {
                    // 尚未支持的认证协议，直接返回
                    SecureUtils.setupAuthHeader(response,true);
                    response.end();
                }
                return null;
            }
        } else {
            // 存在SessionKey，解析JWT。
            User user = onlineUsers.getIfPresent(sessionKey.getValue());
            if (user == null) {
                SecureUtils.setupAuthHeader(response,true);
                response.end();
                return null;
            }
            return user;
        }
    }

    /**
     * 生成JWT令牌, 用作SessionId。
     * @param user 用户对象
     * @return JWT令牌
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String generateJWT(User user) throws IOException, NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();
        LoginedUser loginedUser = new LoginedUser(user);
        String payload = mapper.writeValueAsString(loginedUser);

        KeyGenerator generator = KeyGenerator.getInstance("HmacSHA256");
        generator.init(new SecureRandom(userManageService.decodePassword(user.getPassword())
                .getBytes(StandardCharsets.UTF_8))
        );
        Key key = generator.generateKey();

        return Jwts.builder()
                .subject(payload)
                .signWith(key)
                .compact();
    }

    /**
     * 获取用户登录的Token。
     * @param user 用户对象
     * @return JWT令牌
     */
    public String getLoginedUserToken(User user) {
        for (Map.Entry<String,User> entry: onlineUsers.asMap().entrySet()) {
            User onlineUser = entry.getValue();
            if (onlineUser.getId().equals(user.getId())) {
                return entry.getKey();
            }
        }
        return null;
    }


    /**
     * 用户登录，使用用户名和密码。
     * @param address 远程地址, 用于限制登录尝试次数，抵抗暴力破解。
     * @param userName 用户名，用于查找用户。
     * @param password 密码，用于验证用户。
     * @return JWT令牌，用于后续的会话。
     * @throws Exception 异常，未知错误。
     */
    public String loginWithPassword(String address, String userName, String password) throws Exception {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        wrongPasswordAttempts.cleanUp();
        onlineUsers.cleanUp();
        Long count = wrongPasswordAttempts.getIfPresent(address);
        if (count != null && count > MAX_ATTEMPTS) {
            return null;
        } else if (count == null) {
            count = 0L;
        }

        User user = userManageService.findByName(userName);
        if(user != null && checkPassword(user.getPassword(), password)) {
            String token = getLoginedUserToken(user);
            if (token != null) {
                return token;
            }
            token = generateJWT(user);
            onlineUsers.put(token,user);
            onLineUserId.put(user.getId(),token);
            return token;
        }
        wrongPasswordAttempts.put(address, count + 1);
        return null;
    }

    public boolean checkPassword(String encodedPassword, String userInputPassword) {
        String password = userManageService.decodePassword(encodedPassword);
        return password.equals(userInputPassword);
    }

    public boolean isOnline(Long userId) {
        return onLineUserId.containsKey(userId);
    }

    public void logout(Long userId) {
        onLineUserId.remove(userId);
        onlineUsers.invalidateAll();
        ntlmChallenges.invalidateAll();
        emit(new UserStateChangeEvent(null));
    }

    public void refresh(Long userId) {
        if (onLineUserId.containsKey(userId)) {
            String token = onLineUserId.get(userId);
            User user = userManageService.getUser(userId);
            onlineUsers.put(token,user);
        }
    }

    public void clear() {
        onLineUserId.clear();
        onlineUsers.invalidateAll();
        ntlmChallenges.invalidateAll();
        wrongPasswordAttempts.invalidateAll();
        emit(new UserStateChangeEvent(null));
    }

}
