package org.swdc.rmdisk.service.verticle.http;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.swdc.rmdisk.core.ManagedServerConfigure;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.entity.UserGroup;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;
import org.swdc.rmdisk.service.CommonService;
import org.swdc.rmdisk.service.RequestMapping;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.UserManageService;
import org.swdc.rmdisk.service.verticle.VertxHttpAbstractHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
public class UsersVertxHandlers extends VertxHttpAbstractHandler {

    @Inject
    private UserManageService manageService;

    @Inject
    private CommonService commonService;

    @Inject
    private ManagedServerConfigure serverConfigure;

    @Inject
    private SecureService secureService;

    /**
     * 获取注册功能是否开启
     * @param request
     * @param response
     */
    @RequestMapping(value = "/register", method = "GET")
    public void registerEnabled(HttpServerRequest request, HttpServerResponse response) {

        response.putHeader("content-type", "application/json");
        response.end(SecureUtils.writeString(
                new Resp(200,serverConfigure.getEnableRegister())
        ));

    }

    /**
     * 获取默认头像
     * @param request
     * @param response
     */
    @RequestMapping(value = "/default-avatar", method = "GET")
    public void getDefaultAvatar(HttpServerRequest request, HttpServerResponse response) {

        byte[] avatar = commonService.getDefaultAvatar();
        response.putHeader("content-type", "image/png");
        response.end(Buffer.buffer(avatar));

    }

    @RequestMapping(value = "/user-info", method = "GET")
    public void getUserInfo(HttpServerRequest request, HttpServerResponse response) {

        try {
            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }

            current = manageService.getUser(current.getId());
            current.setPassword(null);
            response.putHeader("content-type", "application/json");
            response.end(SecureUtils.writeString(
                new Resp(200, current)
            ));
        } catch (Exception e) {
            response.setStatusCode(500).end();
        }

    }

    @RequestMapping(value = "/user-info", method = "POST")
    public void updateUserInfo(HttpServerRequest request, HttpServerResponse response) {

        try {
            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }

            request.bodyHandler(buffer -> {

                String body = buffer.toString();
                User updateInfo = SecureUtils.readString(body, User.class);
                if (updateInfo == null || updateInfo.getPassword() != null) {
                    response.setStatusCode(400).end(SecureUtils.writeString(
                            new Resp(400, "Bad request")
                    ));
                    return;
                }

                User updated = manageService.updateUser(
                        current.getId(), updateInfo
                );

                if (updated == null) {
                    response.setStatusCode(400).end(SecureUtils.writeString(
                            new Resp(400, "Bad request")
                    ));
                    return;
                }

                secureService.refresh(updated.getId());
                updated.setPassword(null);

                response.putHeader("content-type", "application/json");
                response.end(SecureUtils.writeString(
                        new Resp(200, updated)
                ));

            });

        } catch (Exception e) {
            response.setStatusCode(500).end();
        }

    }

    @RequestMapping(value = "/reset-pwd", method = "POST")
    public void resetPassword(HttpServerRequest request, HttpServerResponse response) {

        try {
            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }

            request.bodyHandler(buffer -> {

                String body = buffer.toString();
                ClientResetPassword updateInfo = SecureUtils.readString(body, ClientResetPassword.class);
                if (updateInfo == null || updateInfo.password() == null || updateInfo.newPassword() == null) {
                    response.setStatusCode(400).end(SecureUtils.writeString(
                            new Resp(400, "Bad request")
                    ));
                    return;
                }

                String oldPassword = updateInfo.password();
                String newPassword = updateInfo.newPassword();
                if (oldPassword.isBlank() || newPassword.isBlank() || !current.getPassword().equals(oldPassword)) {
                    response.setStatusCode(400).end(SecureUtils.writeString(
                            new Resp(400, "Bad request")
                    ));
                    return;
                }

                User update = new User();
                update.setPassword(newPassword);

                update = manageService.updateUser(current.getId(),update);
                update.setPassword(null);

                secureService.logout(current.getId());
                response.putHeader("content-type", "application/json");
                response.end(SecureUtils.writeString(
                        new Resp(200, update)
                ));
            });

        } catch (Exception e) {
            response.setStatusCode(500).end();
        }

    }

    /**
     * 获取所有可注册的群组
     * @param request
     * @param response
     */

    @RequestMapping(value = "/groups", method = "GET")
    public void getGroups(HttpServerRequest request, HttpServerResponse response) {

        response.putHeader("content-type", "application/json");
        List<UserGroup> groups = manageService.getRegistrableGroups();
        response.end(SecureUtils.writeString(
                new Resp(200,groups)
        ));

    }


    /**
     * 处理用户的注册请求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/register", method = "POST")
    public void register(HttpServerRequest request, HttpServerResponse response) {

        request.bodyHandler(buffer -> {

            String body = buffer.toString();
            UserRegisterRequest userRegRequest = SecureUtils.readString(
                    body, UserRegisterRequest.class
            );

            if (userRegRequest == null) {
                response.setStatusCode(500).end(SecureUtils.writeString(
                        new Resp(500, "Internal Server Error")
                ));
                return;
            }

            if (manageService.checkUserExist(userRegRequest.getName())) {
                response.setStatusCode(200).end(SecureUtils.writeString(
                        new Resp(400, "exist")
                ));
                return;
            }

            if (manageService.requestUserRegister(userRegRequest)) {

                User user = manageService.findByName(userRegRequest.getName());
                if (user != null) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(200, "ok")
                    ));
                } else {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(200, "pending")
                    ));
                }
            } else {
                response.setStatusCode(200).end(SecureUtils.writeString(
                        new Resp(400, "failed")
                ));
            }

        });

    }


}
