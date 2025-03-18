package org.swdc.rmdisk.client.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.ours.common.helper.ProgressListener;
import org.swdc.ours.common.network.*;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.core.dav.BarerLoginRequest;
import org.swdc.rmdisk.core.dav.multiple.DMultiStatus;
import org.swdc.rmdisk.core.dav.multiple.DProperties;
import org.swdc.rmdisk.core.dav.multiple.DPropertyStatus;
import org.swdc.rmdisk.core.dav.multiple.DResponse;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.xmlns.XMLMapper;
import org.swdc.rmdisk.service.verticle.http.Resp;

import java.io.File;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class StarWebDAVClientProtocol implements ClientFileProtocol {

    private StarWebDAVClient client;

    private volatile String token;

    private Logger logger = LoggerFactory.getLogger(StarWebDAVClientProtocol.class);

    private String baseUrl;

    public StarWebDAVClientProtocol(String url) {
        client = Network.create(StarWebDAVClient.class, url, map -> {
            if (token != null) {
                map.put("Authorization", "Bearer " + token);
            }
        }, new ApacheRequester());
        this.baseUrl = url;
    }

    @Override
    public boolean registerable() {
        try {
            Resp resp = client.registerSupport();
            return Boolean.parseBoolean(resp.getData().toString());
        } catch (Exception e) {
            logger.error("Error while registering support", e);
            return false;
        }
    }

    @Override
    public String login(String username, String password) {
        BarerLoginRequest loginRequest = new BarerLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        try {
            return client.login(loginRequest);
        } catch (Exception e) {
            logger.error("Error while logging in", e);
            return null;
        }
    }

    @Override
    public RemoteUser updateUserInfo(String tokenOrSessionId, RemoteUser user) throws ConnectException {
        try {
            this.token = tokenOrSessionId;
            Resp resp = client.updateUserInfo(user);
            if (resp == null || resp.getCode() != 200) {
                return null;
            }
            return resp.getData(RemoteUser.class);
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            if (e instanceof NetworkException) {
                NetworkException ex = (NetworkException) e;
                if (ex.getCode() == 401) {
                    throw new ConnectException("Unauthorized");
                }
            }
            logger.error("Error while getting user info", e);
            return null;
        }
    }

    @Override
    public RemoteUser getUserInfo(String tokenOrSessionId) throws ConnectException {
        this.token = tokenOrSessionId;
        try {

            Resp resp = client.getUserInfo();
            User user = resp.getData(User.class);

            RemoteUser remoteUser = new RemoteUser();
            remoteUser.setUsername(user.getUsername());
            remoteUser.setNickname(user.getNickname());
            remoteUser.setAvatar(user.getAvatar());
            remoteUser.setTotalSize(user.getTotalSize());
            remoteUser.setUsedSize(user.getUsedSize());

            return remoteUser;
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            if (e instanceof NetworkException) {
                NetworkException ex = (NetworkException) e;
                if (ex.getCode() == 401) {
                    throw new ConnectException("Unauthorized");
                }
            }
            logger.error("Error while getting user info", e);
            return null;
        }
    }

    @Override
    public boolean logout(String tokenOrSessionId) {
        return true;
    }

    @Override
    public RemoteResource getRootFolder(String tokenOrSessionId) throws ConnectException {
        return getResource(tokenOrSessionId, "/");
    }

    @Override
    public void loadResourceContent(String tokenOrSessionId, String path, ByteHandler reader, Consumer<Void> then) throws ConnectException {
        try {

            RemoteResource resource = getResource(tokenOrSessionId,path);
            if (resource == null || resource.isFolder()) {
                return;
            }

            client.getResourceContent(URLEncoder.encode(path,StandardCharsets.UTF_8))
                    .byteHandler(reader)
                    .then(then)
                    .send();

        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            logger.error("Error while loading resource content", e);
        }
    }

    @Override
    public void uploadResource(String tokenOrSessionId, String path, File target, ProgressListener progressListener, Consumer<Void> then) throws ConnectException {
        try {

            if (tokenOrSessionId == null || tokenOrSessionId.isBlank()) {
                return;
            }

            if (path == null || path.isBlank()) {
                return;
            }

            if (target == null || !target.exists()) {
                return;
            }

            if (!path.endsWith(target.getName())) {
                if (!path.endsWith("/")) {
                    path += "/";
                }
                path += target.getName();
            }

            String parent = path.substring(0, path.lastIndexOf("/"));
            RemoteResource resource = getResource(tokenOrSessionId, parent);
            if (resource == null || !resource.isFolder()) {
                return;
            }

            this.token = tokenOrSessionId;
            client.uploadResource(URLEncoder.encode(path,StandardCharsets.UTF_8), target)
                    .progressHandler(progressListener)
                    .then(then)
                    .send();

        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            logger.error("Error while uploading resource", e);
        }
    }

    @Override
    public RemoteResource getResource(String tokenOrSessionId, String path) throws ConnectException {
        try {
            this.token = tokenOrSessionId;
            String xml = client.getResource(URLEncoder.encode(path,StandardCharsets.UTF_8));
            if (xml == null || xml.isBlank()) {
                return null;
            }
            DMultiStatus status = XMLMapper.readObjectFromString(DMultiStatus.class, xml);
            if (status == null) {
                return null;
            }

            DResponse response = status.getResponses().get(0);
            DPropertyStatus prop = response.getPropertyStatus();
            DProperties properties = prop.getProperties();

            RemoteResource resource = new RemoteResource();
            if (properties.getCreationDate() != null) {
                resource.setCreatedOn(LocalDateTime.parse(
                        properties.getCreationDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME
                ));
            }
            resource.setFolder(properties.getResourceType() != null);
            resource.setName(properties.getDisplayName());
            resource.setPath(response.getHref());
            resource.setContentSize(properties.getContentLength());
            return resource;
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            logger.error("Error while getting root folder", e);
            return null;
        }
    }

    @Override
    public List<RemoteResource> getFolderContent(String tokenOrSessionId, String folderPath) throws ConnectException {
        try {
            this.token = tokenOrSessionId;
            String xml = client.getChildFolders(URLEncoder.encode(folderPath,StandardCharsets.UTF_8));
            if (xml == null || xml.isBlank()) {
                return Collections.emptyList();
            }
            DMultiStatus status = XMLMapper.readObjectFromString(DMultiStatus.class, xml);
            if (status == null) {
                return null;
            }
            List<RemoteResource> folders = new ArrayList<>();
            for (DResponse response : status.getResponses()) {
                DPropertyStatus prop = response.getPropertyStatus();
                DProperties properties = prop.getProperties();
                String path = response.getHref();

                RemoteResource resource = new RemoteResource();
                if (properties.getCreationDate() != null) {
                    resource.setCreatedOn(LocalDateTime.parse(
                            properties.getCreationDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    ));
                }

                resource.setFolder(properties.getResourceType() != null);
                resource.setName(properties.getDisplayName());
                resource.setPath(path);
                resource.setContentSize(properties.getContentLength());
                folders.add(resource);
            }
            return folders;
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            logger.error("Error while getting folders", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean renameResource(String tokenOrSessionId, String folderPath, String newName) throws ConnectException {
        if (folderPath.endsWith("/")) {
            folderPath = folderPath.substring(0, folderPath.length() - 1);
        }
        String parentPath = folderPath.substring(0, folderPath.lastIndexOf("/"));
        String newPath = parentPath + "/" + newName;
        try {
            client.moveResource(
                    URLEncoder.encode(folderPath, StandardCharsets.UTF_8),
                    URLEncoder.encode(newPath, StandardCharsets.UTF_8)
            );
            return true;
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            logger.error("Error while getting folders", e);
        }
        return false;
    }

    @Override
    public RemoteResource createFolder(String tokenOrSessionId, String folderPath, String newName) throws ConnectException {
        try {
            this.token = tokenOrSessionId;
            if (folderPath.endsWith("/")) {
                folderPath = folderPath.substring(0, folderPath.length() - 1);
            }

            String createdPath = folderPath + "/" + newName;
            String target = URLEncoder.encode(createdPath, StandardCharsets.UTF_8);
            client.createFolder(target);

            return getResource(tokenOrSessionId, createdPath);
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            logger.error("Error while creating folder", e);
        }
        return null;
    }

    @Override
    public boolean trashResource(String tokenOrSessionId, String folderPath) throws ConnectException {
        try {
            this.token = tokenOrSessionId;
            client.deleteResource(URLEncoder.encode(folderPath, StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectException) {
                throw (ConnectException) e.getCause();
            }
            logger.error("Error while deleting folder", e);
        }
        return false;
    }


    @Override
    public String getUrl(String path) {
        String baseUrl = this.baseUrl;
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return baseUrl + path;
    }
}
