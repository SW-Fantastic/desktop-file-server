package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.dav.ResponseStatus;
import org.swdc.rmdisk.core.dav.multiple.*;
import org.swdc.rmdisk.core.entity.DiskFile;
import org.swdc.rmdisk.core.entity.DiskFolder;
import org.swdc.rmdisk.core.entity.DiskResource;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.xmlns.*;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.SecureService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 本Handler处理文件和文件夹的元数据，
 * 包括文件的创建日期，文件大小等，对于文件系统来说这就是文件的属性。
 */
@Singleton
public class VertxDAVPropFindHandler implements Handler<RoutingContext> {

    @Inject
    private Logger logger;

    @Inject
    private SecureService secureService;

    @Inject
    private DiskFileService diskFileService;


    @Override
    public void handle(RoutingContext ctx) {

        HttpServerRequest request = ctx.request();
        HttpServerResponse response = ctx.response();

        try {

            User currentUser = secureService.requestAuth(request,response);
            if (currentUser == null) {
                // 用户尚未登录。
                return;
            }

            DMultiStatus multipleStatus = new DMultiStatus();
            List<DResponse> responses = new ArrayList<>();

            // Path可能已经通过URLEncoder编码过，首先解码处理之
            String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");

            DiskFolder folder = diskFileService
                    .getFolderByPath(currentUser,path);

            ZoneOffset offset = OffsetDateTime.now()
                    .getOffset();

            if (folder == null) {

                // 目录不存在，此时有两种情况，其一是正在访问文件，其二是正在访问目录（而目录不存在）
                // 按路径查找文件
                DiskFile file = diskFileService.getFileByPath(currentUser,path);
                if (file == null) {
                    // 文件不存在
                    response.setStatusCode(404);
                    response.end();
                    return;
                } else {

                    // 文件存在，返回文件的属性信息
                    DProperties dProperties = new DProperties();
                    dProperties.setDisplayName(file.getName());
                    dProperties.setContentType(file.getMimeType());
                    dProperties.setResourceType(null);
                    dProperties.setContentLength(file.getFileSize() == null ? 0 : file.getFileSize());
                    dProperties.setCreationDate(file.getCreatedOn()
                            .atZone(offset)
                            .withZoneSameInstant(ZoneId.of("GMT"))
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    );
                    if (file.getUpdatedOn() == null) {
                        dProperties.setLastModified(file.getCreatedOn()
                                .atZone(offset)
                                .withZoneSameInstant(ZoneId.of("GMT"))
                                .format(DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH))
                        );
                    } else {
                        dProperties.setLastModified(file.getUpdatedOn()
                                .atZone(offset)
                                .withZoneSameInstant(ZoneId.of("GMT"))
                                .format(DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH))
                        );
                    }

                    DPropertyStatus status = new DPropertyStatus(ResponseStatus.OK, dProperties);
                    responses.add(new DResponse(path, status));

                }

            } else {

                // 目录存在，返回目录的属性信息
                DProperties dProperties = new DProperties();
                if (folder.getParent() == null) {
                    dProperties.setDisplayName(currentUser.getUsername() + " StarCloud");
                } else {
                    dProperties.setDisplayName(path);
                }
                dProperties.setResourceType(DResourceType.COLLECTION);

                DPropertyStatus dstatRoot = new DPropertyStatus(ResponseStatus.OK, dProperties);
                responses.add(new DResponse(path, dstatRoot));

                if (request.headers().contains("Depth") && request.headers().get("Depth").equals("1")) {
                    // 深度为1，即只返回当前目录下的文件和文件夹信息
                    List<DiskResource> resources = new ArrayList<>();
                    resources.addAll(diskFileService.getChildren(folder));
                    resources.addAll(diskFileService.getFilesByParent(folder));

                    for (DiskResource item : resources) {

                        DProperties properties = new DProperties();
                        properties.setDisplayName(item.getName());
                        properties.setResourceType(item instanceof DiskFolder ? DResourceType.COLLECTION : null);
                        properties.setCreationDate(item.getCreatedOn()
                                .atZone(ZoneId.systemDefault())
                                .withZoneSameInstant(ZoneId.of("GMT"))
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        );

                        if (item instanceof DiskFile) {
                            DiskFile file = (DiskFile) item;
                            if (file.getFileSize() != null) {
                                properties.setContentLength(file.getFileSize());
                            }
                            properties.setContentType(file.getMimeType());
                        }
                        if (item.getUpdatedOn() == null) {
                            properties.setLastModified(item.getCreatedOn()
                                    .atZone(offset)
                                    .withZoneSameInstant(ZoneId.of("GMT"))
                                    .format(DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH))
                            );
                        } else {
                            properties.setLastModified(item.getUpdatedOn()
                                    .atZone(offset)
                                    .withZoneSameInstant(ZoneId.of("GMT"))
                                    .format(DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH))
                            );
                        }

                        if (path.endsWith("/")) {
                            path = path.substring(0, path.length() - 1);
                        }
                        DPropertyStatus dstat = new DPropertyStatus(ResponseStatus.OK,properties);
                        DResponse dResp = new DResponse(path + "/" + item.getName(), dstat);
                        responses.add(dResp);

                    }

                }
            }

            multipleStatus.setResponses(responses);
            response.end(XMLMapper.writeObjectAsString(multipleStatus));

        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("failed to process request, unknown exception : ", e);
        }
    }
}
