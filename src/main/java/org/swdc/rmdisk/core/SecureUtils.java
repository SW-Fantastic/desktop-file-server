package org.swdc.rmdisk.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.util.Base64;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SecureUtils {

    private static ObjectMapper mapper;

    /**
     * 本方法用于判断用户的身份认证类型。
     * @param authHeader 认证的Header。
     * @return 认证类型
     */
    public static AuthType getAuthType(String authHeader) {
        if (authHeader.startsWith("Negotiate") || authHeader.startsWith("NTLM")) {
            return AuthType.NTLM;
        } else if (authHeader.startsWith("Bearer")) {
            return AuthType.Bearer;
        }
        return AuthType.Unknown;
    }



    /**
     * 以NTLMv2的协议格式验证用户密码。
     * @param domain 用户所在的域，这不重要，直接使用NTLM提供的域即可
     * @param ntlmResponse NTLM的相应信息，是用来验证的依据
     * @param userName 用户名
     * @param password 用户密码（明文）
     * @param serverChallenge 服务器通过Type2Message发送的密钥挑战
     * @return 协议认证成功与否
     */
    public static boolean checkPasswordNtlmV2(String domain, byte[] ntlmResponse, String userName, String password, byte[] serverChallenge) {

        // Response的前16位是加密后的信息，稍后对比的就是它们
        // ClientChallenge在Response的第16位开始，持续到Response的结束。
        byte[] clChallenge = new byte[ntlmResponse.length - 16];
        System.arraycopy(ntlmResponse,16,clChallenge,0,ntlmResponse.length - 16);

        // 生成Response。
        byte[] response = NtlmPasswordAuthentication.getLMv2Response(
                domain,userName,password,serverChallenge,clChallenge
        );

        // 按照生成的Response的位数提取Client的Response中的数据
        byte[] clientResponse = new byte[response.length];
        System.arraycopy(ntlmResponse,0,clientResponse,0,response.length);

        // 对比并验证正确性。
        return Arrays.equals(response,clientResponse);

    }

    /**
     * 处理NTLM认证协议
     * @param request http的Request
     * @param response http的Response
     * @param challenge Challenge，8位，服务器随机生成。
     * @return 在客户端提供适当的http-header后返回Ntlm认证对象，否则全部为空。
     * @throws IOException
     */
    public static NtlmPasswordAuthentication processNTMLAuth(HttpServerRequest request, HttpServerResponse response, byte[] challenge) throws IOException {

        String auth = request.getHeader("Authorization");
        if (auth != null) {

            if (getAuthType(auth) == AuthType.NTLM) {

                String data = auth.substring(5);

                byte[] decoded =  Base64.decode(data);
                if (decoded[8] == 1) {

                    Type1Message type1 = new Type1Message(decoded);
                    Type2Message type2 = new Type2Message(type1, challenge, null);
                    String msg = Base64.encode(type2.toByteArray());
                    response.putHeader("WWW-Authenticate", "NTLM " + msg);
                    response.putHeader("Content-Length", "0");
                    response.setStatusCode(401);

                } else if (decoded[8] == 3) {

                    Type3Message type3 = new Type3Message(decoded);
                    byte[] lmResponse = type3.getLMResponse();
                    if (lmResponse == null) {
                        lmResponse = new byte[0];
                    }

                    byte[] ntResponse = type3.getNTResponse();
                    if (ntResponse == null) {
                        ntResponse = new byte[0];
                    }

                    return new NtlmPasswordAuthentication(
                            type3.getDomain(),
                            type3.getUser(),
                            challenge,
                            lmResponse,
                            ntResponse
                    );
                }
            }

        } else {
            setupAuthHeader(response,false);
            return null;
        }

        return null;

    }

    /**
     * 设置未登录时候的HttpHeader。
     * @param response Http响应
     * @param focus 是否清理Cookie
     */
    public static void setupAuthHeader(HttpServerResponse response, boolean focus) {
        response.setStatusCode(401);
        response.putHeader("WWW-Authenticate", "NTLM,Bearer");
        if (focus) {
            response.removeCookie("NTLM_CHID");
            response.removeCookie("SESSION_KEY");
        }
    }


    public static void failed(HttpServerResponse response) {
        if (!response.ended()) {
            response.setStatusCode(500);
            response.setStatusMessage("Internal Error");
            response.end();
        }
    }

    public static boolean isBrowser(HttpServerRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return false;
        }
        List<String> agentsLowerCase = Arrays.asList(
                "msie","chrome","firefox","opera","trident/7","edge"
        );
        for (String agent : agentsLowerCase) {
            if (userAgent.toLowerCase().contains(agent)) {
                return true;
            }
        }
        return false;
    }

    public static String writeString(Object obj) {
        if (mapper == null) {
            createMapper();
        }
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] writeBytes(Object obj) {
        if (mapper == null) {
            createMapper();
        }
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T readString(String json, Class<T> clazz) {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
        try {
            return mapper.readValue(json,clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T jsonObjectAsType(Class<T> type, Object obj) {
        return mapper.convertValue(obj,type);
    }

    private static void createMapper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

}
