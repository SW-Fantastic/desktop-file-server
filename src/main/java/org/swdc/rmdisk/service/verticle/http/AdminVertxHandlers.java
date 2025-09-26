package org.swdc.rmdisk.service.verticle.http;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.data.StatelessHelper;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.service.*;
import org.swdc.rmdisk.service.events.GroupListRefreshEvent;
import org.swdc.rmdisk.service.verticle.VertxHttpAbstractHandler;
import org.swdc.rmdisk.service.verticle.http.dto.PagedReq;
import org.swdc.rmdisk.service.verticle.http.dto.PagedResponse;
import org.swdc.rmdisk.service.verticle.http.dto.UpdateGroupReq;
import org.swdc.rmdisk.service.verticle.http.dto.UpdateUserReq;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AdminVertxHandlers extends VertxHttpAbstractHandler implements EventEmitter {

    @Inject
    private SecureService  secureService;

    @Inject
    private UserManageService userManageService;

    @Inject
    private ActivityService activityService;

    @Inject
    private CommonService commonService;

    @Inject
    private Logger logger;

    private Events eventEmitter;

    @RequestMapping(value = "/admin/groups",method = "GET")
    public void getGroups(HttpServerRequest request, HttpServerResponse response) {
        try {

            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }

            if (current.getPermissions() != Permission.SUPER_ADMIN) {
                response.setStatusCode(403).end();
                return;
            }

            response.putHeader("content-type", "application/json");
            String state = request.getParam("state");
            State targetState = null;
            try {
                targetState = State.valueOf(state);
            } catch (Exception e) {
                targetState = State.NORMAL;
            }

            List<UserGroup> groups = userManageService.getGroupsByState(targetState);
            response.setStatusCode(200).end(SecureUtils.writeString(
                    new Resp(200, groups.stream().map(StatelessHelper::safety).toList())
            ));
        } catch (Exception e) {
            logger.error("Error while getGroups", e);
            response.setStatusCode(200).end(SecureUtils.writeString(
                    new Resp(500, "Internal Server Error")
            ));
        }

    }

    /**
     * 创建一个用户分组
     * @param request
     * @param response
     */
    @RequestMapping(value = "/admin/groups",method = "POST")
    public void addGroup(HttpServerRequest request, HttpServerResponse response) {
        try {

            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }

            if (current.getPermissions() != Permission.SUPER_ADMIN) {
                response.setStatusCode(403).end();
                return;
            }

            request.bodyHandler(buffer -> {
                response.putHeader("content-type", "application/json");
                UpdateGroupReq req = SecureUtils.readString(buffer.toString(), UpdateGroupReq.class);
                if (req == null || !req.creatable()) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(400, "Bad Request")
                    ));
                    return;
                }

                UserGroup group = userManageService.addGroup(req.getName());
                if (group != null) {

                    activityService.createActivity(
                            current.getId(),
                            SecureUtils.remoteAddress(request),
                            ActivityType.CREATE,
                            UserGroup.class,
                            group.getId(),
                            null,
                            null,
                            null
                    );

                    if (req.getAllowRegister() != null) {
                        userManageService.updateRegisterPermission(group.getId(), req.getAllowRegister());
                    }
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(200, StatelessHelper.safety(group))
                    ));
                    emit(new GroupListRefreshEvent(group.getId()));
                } else {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(400, "Failed to add group")
                    ));
                }

            });

        } catch (Exception e) {
            logger.error("Error while addGroup", e);
            response.setStatusCode(200).end(SecureUtils.writeString(
                    new Resp(500, "Internal Server Error")
            ));
        }
    }


    /**
     * 更新或者删除一个用户分组。
     * @param request the HTTP request.
     * @param response the HTTP response.
     */
    @RequestMapping(value = "/admin/groups",method = "PUT")
    public void updateGroup(HttpServerRequest request, HttpServerResponse response) {
        try {

            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }

            if (current.getPermissions() != Permission.SUPER_ADMIN) {
                response.setStatusCode(403).end();
                return;
            }

            request.bodyHandler(buffer -> {
                response.putHeader("content-type", "application/json");
                UpdateGroupReq req = SecureUtils.readString(buffer.toString(), UpdateGroupReq.class);
                if (req == null || !req.updatable()) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(400, "Bad Request")
                    ));
                    return;
                }

                if (req.isTrash()) {
                    if (current.getGroup().getId().equals(req.getGroupId())) {
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(400, "Failed to delete group")
                        ));
                        return;
                    }
                    boolean result = userManageService.transGroup(req.getGroupId());
                    if (result) {
                        activityService.createActivity(
                                current.getId(),
                                SecureUtils.remoteAddress(request),
                                ActivityType.DELETE,
                                UserGroup.class,
                                req.getGroupId(),
                                null,
                                null,
                                null
                        );
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(200, "Ok")
                        ));
                        emit(new GroupListRefreshEvent(req.getGroupId()));
                    } else {
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(400, "Failed to delete group")
                        ));
                    }
                } else {

                    UserGroup group = userManageService.getGroup(req.getGroupId());
                    boolean result = userManageService.renameGroup(req.getGroupId(), req.getName());
                    if (result) {
                        activityService.createActivity(
                                current.getId(),
                                SecureUtils.remoteAddress(request),
                                ActivityType.UPDATE,
                                UserGroup.class,
                                req.getGroupId(),
                                "groupName",
                                group.getGroupName(),
                                req.getName()
                        );
                    }
                    if (req.getAllowRegister() != null) {
                        result = result && userManageService.updateRegisterPermission(req.getGroupId(), req.getAllowRegister());
                    }
                    if (result) {
                        activityService.createActivity(
                                current.getId(),
                                SecureUtils.remoteAddress(request),
                                ActivityType.UPDATE,
                                UserGroup.class,
                                req.getGroupId(),
                                "registerable",
                                group.isRegistrable() ? "TRUE" : "FALSE",
                                req.getAllowRegister() ? "TRUE" : "FALSE"
                        );
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(200, "Ok")
                        ));
                        emit(new GroupListRefreshEvent(req.getGroupId()));
                    } else {
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(400, "Failed to update the group")
                        ));
                    }
                }

            });
        } catch (Exception e) {
            logger.error("Error while updateGroup", e);
            response.setStatusCode(200).end(SecureUtils.writeString(
                    new Resp(500, "Failed to update the group")
            ));
        }
    }

    @RequestMapping(value = "/admin/groups/users",method = "POST")
    public void getUsersOfGroup(HttpServerRequest request, HttpServerResponse response) {
        try {
            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }
            if (current.getPermissions() != Permission.SUPER_ADMIN) {
                response.setStatusCode(403).end();
                return;
            }
            request.bodyHandler(buffer -> {
                response.putHeader("content-type", "application/json");
                PagedReq req = SecureUtils.readString(buffer.toString(), PagedReq.class);
                if (req == null || req.getId() == null) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(400, "Bad Request")
                    ));
                    return;
                }
                UserGroup group = userManageService.getGroup(req.getId());
                if (group == null) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                        new Resp(404, "Group not found")
                    ));
                    return;
                }

                LocalDateTime start = null;
                LocalDateTime end = null;
                if (req.getStartDate() != null) {
                    start = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                            req.getStartDate()), ZoneId.of("UTC")
                    );
                }

                if (req.getEndDate() != null) {
                    end = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                            req.getEndDate()), ZoneId.of("UTC")
                    );
                }

                List<User> users = userManageService.getUserFiltered(
                        group,start,end,req.getKeyword(),req.getPage(),req.getSize()
                );

                int totals = userManageService.countUserFiltered(
                        group,start,end,req.getKeyword()
                );

                PagedResponse<User> resp = new PagedResponse<>();
                resp.setPage(req.getPage());
                resp.setSize(req.getSize());
                resp.setContent(users.stream()
                        .map(StatelessHelper::safety)
                        .collect(Collectors.toList()));
                resp.setTotalElements(totals);
                response.setStatusCode(200).end(SecureUtils.writeString(
                        new Resp(200, resp)
                ));

            }).exceptionHandler(e -> {

                logger.error("Error while getUsersOfGroup", e);
                if (! response.ended()) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(500, "Internal Server Error")
                    ));
                }

            });
        } catch (Exception e) {
            logger.error("Error while getUsersOfGroup", e);
            response.setStatusCode(200).end(SecureUtils.writeString(
                    new Resp(500, "Internal Server Error")
            ));
        }
    }


    @RequestMapping(value = "admin/groups/users/update", method = "PUT")
    public void updateUser(HttpServerRequest request, HttpServerResponse response) {
        try {
            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }
            if (current.getPermissions() != Permission.SUPER_ADMIN) {
                response.setStatusCode(403).end();
                return;
            }
            request.bodyHandler(buffer -> {

                response.putHeader("content-type", "application/json");
                UpdateUserReq req = SecureUtils.readString(buffer.toString(), UpdateUserReq.class);
                if (req == null || !req.updatable()) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(400, "Bad Request")
                    ));
                    return;
                }

                if (req.isTrash()) {

                    User user = userManageService.getUser(req.getId());
                    if (user == null) {
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(404, "User not found")
                        ));
                        return;
                    }

                    if (user.getState() == State.TRASHED && req.isPurge()) {
                        if(userManageService.purgeUser(user)) {
                            activityService.createActivity(
                                    current.getId(),
                                    SecureUtils.remoteAddress(request),
                                    ActivityType.DELETE,
                                    User.class,
                                    req.getId(),
                                    null,
                                    null,
                                    null);
                            response.setStatusCode(200).end(SecureUtils.writeString(
                                    new Resp(200, "Ok")
                            ));
                        } else {
                            response.setStatusCode(200).end(SecureUtils.writeString(
                                    new Resp(400, "Failed to delete the user.")
                            ));
                        }
                        return;
                    }

                    if(userManageService.deleteUser(user)) {
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(200, "Ok")
                        ));
                    } else {
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(400, "Failed to delete the user.")
                        ));
                    }

                } else {
                    User update = new User();
                    update.setId(req.getId());
                    update.setTotalSize(req.getTotalSize());
                    update.setUsername(req.getUsername());
                    update.setNickname(req.getNickname());
                    update.setPassword(req.getPassword());
                    if (req.getGroupId() != null) {
                        UserGroup group = userManageService.getGroup(req.getGroupId());
                        if (group == null) {
                            response.setStatusCode(200).end(SecureUtils.writeString(
                                    new Resp(404, "Group not found")
                            ));
                            return;
                        }
                        update.setGroup(group);
                    }
                    if (req.getAvatarBase64() != null) {
                        byte[] avatar = Base64.getDecoder().decode(req.getAvatarBase64());
                        update.setAvatar(avatar);
                    }
                    User updated = userManageService.saveUser(update);
                    if (updated != null) {
                        activityService.createActivity(
                                current.getId(),
                                SecureUtils.remoteAddress(request),
                                ActivityType.UPDATE,
                                User.class,
                                req.getId(),
                                null,
                                null,
                                null);
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(200, "Ok")
                        ));
                    } else {
                        response.setStatusCode(200).end(SecureUtils.writeString(
                                new Resp(400, "Failed to update the user.")
                        ));
                    }
                }

            }).exceptionHandler(e -> {
                if (! response.ended()) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(500, "Internal Server Error")
                    ));
                }
            });
        } catch (Exception e) {
            logger.error("Error while updateUser", e);
        }

    }


    @RequestMapping(value = "/admin/groups/users/create",method = "POST")
    public void createUser(HttpServerRequest request, HttpServerResponse response) {
        try {
            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }
            if (current.getPermissions() != Permission.SUPER_ADMIN) {
                response.setStatusCode(403).end();
                return;
            }
            request.bodyHandler(buffer -> {

                response.putHeader("content-type", "application/json");
                UpdateUserReq req = SecureUtils.readString(buffer.toString(), UpdateUserReq.class);
                if (req == null || !req.creatable()) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(400, "Bad Request")
                    ));
                    return;
                }

                UserGroup group = userManageService.getGroup(req.getGroupId());
                if (group == null) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(404, "Group not found")
                    ));
                    return;
                }

                User user = new User();
                user.setUsername(req.getUsername());
                user.setPassword(req.getPassword());
                user.setTotalSize(req.getTotalSize());
                user.setNickname(req.getNickname());
                user.setGroup(group);
                if (req.getAvatarBase64() != null) {
                    byte[] avatar = Base64.getDecoder().decode(req.getAvatarBase64());
                    user.setAvatar(avatar);
                }
                user.setPermissions(Permission.USER);
                user.setUsedSize(0L);
                User created = userManageService.saveUser(user);
                if (created != null) {
                    activityService.createActivity(
                            current.getId(),
                            SecureUtils.remoteAddress(request),
                            ActivityType.CREATE,
                            User.class,
                            created.getId(),
                            null,
                            null,
                            null
                    );
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(200, "Ok")
                    ));
                } else {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(400, "Failed to create the user")
                    ));
                }

            }).exceptionHandler(e -> {
                if (! response.ended()) {
                    response.setStatusCode(200).end(SecureUtils.writeString(
                            new Resp(500, "Internal Server Error")
                    ));
                }
            });

        } catch (Exception e) {
            logger.error("Error while createUser", e);
            response.setStatusCode(200).end(SecureUtils.writeString(
                    new Resp(500, "Internal Server Error")
            ));
        }
    }


    @RequestMapping(value = "/admin/default-avatar",method = "GET")
    public void getDefaultAvatar(HttpServerRequest request, HttpServerResponse response) {
        try {
            User current = secureService.requestAuth(request, response);
            if (current == null) {
                return;
            }
            if (current.getPermissions() != Permission.SUPER_ADMIN) {
                response.setStatusCode(403).end();
                return;
            }
            response.putHeader("content-type", "application/json");
            byte[] avatar = commonService.getDefaultAvatar();
            response.end(SecureUtils.writeString(
                    new Resp(200, avatar)
            ));
        } catch (Exception e) {
            logger.error("Error while getDefaultAvatar", e);
            response.setStatusCode(200).end(SecureUtils.writeString(
                    new Resp(500, "Internal Server Error")
            ));
        }
    }

    @Override
    public <T extends AbstractEvent> void emit(T t) {
        eventEmitter.dispatch(t);
    }

    @Override
    public void setEvents(Events events) {
        this.eventEmitter = events;
    }
}
