package org.swdc.rmdisk.service.verticle.ftp;

import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import org.swdc.rmdisk.core.entity.DiskFile;
import org.swdc.rmdisk.core.entity.DiskFolder;
import org.swdc.rmdisk.core.entity.DiskResource;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.ftp.*;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.FTPControl;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.verticle.VertxFTPAbstractHandler;

import java.io.File;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FTPFilesHandler extends VertxFTPAbstractHandler {

    @Inject
    private SecureService secureService;

    @Inject
    private DiskFileService diskFileService;

    /**
     * FTP的size指令，获取文件大小。
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("SIZE")
    public void size(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        FTPSession session = getFTPSession(context, sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error.");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        String fileName = msg.getArgs();
        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(),fileName);
        DiskFile file = diskFileService.getFileByPath(user,accessor.getPath());
        if (file == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "File not found.");
            sender.write(resp.prepare());
            return;
        }

        FTPMsg resp = new FTPMsg(FTPRespCode.REQUESTED_FILE_ACTION_OK, "" + file.getFileSize());
        sender.write(resp.prepare());

    }

    /**
     * FTP的retr指令，传输文件。
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("RETR")
    public void transferFile(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        FTPSession session = getFTPSession(context, sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error.");
            sender.write(resp.prepare());
            return;
        }

        String fileName = msg.getArgs();
        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(),fileName);
        DiskFile file = diskFileService.getFileByPath(user, accessor.getPath());

        if (file == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "File not found.");
            sender.write(resp.prepare());
            return;
        }

        Future<NetSocket> future = session.getDataConnection();
        if (future == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.CAN_NOT_OPEN_DATA_CONN, "Can not open data connection.");
            sender.write(resp.prepare());
            return;
        }

        future.onSuccess(dataSender -> {

            File targetFile = diskFileService.getSystemFile(file);
            dataSender.sendFile(targetFile.getAbsolutePath(),session.getTransferOffset()).onComplete(v -> {

                FTPMsg resp = new FTPMsg(FTPRespCode.DATA_CONN_CLOSING, "File transfer successful.");
                sender.write(resp.prepare());
                dataSender.end();
                session.setDataConnection(null);

            }).onFailure(err -> {

                logger.error("Error while sending file", err);
                FTPMsg resp = new FTPMsg(FTPRespCode.ABORTED_TRANSFER, "File transfer aborted.");
                sender.write(resp.prepare());
                dataSender.end();
                session.setDataConnection(null);

            });

        });

        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_STATE_OK_OPEN_DATA_CONN, "File status ok, opening data connection.");
        sender.write(resp.prepare());
    }

    @FTPControl("STOR")
    public void storeFile(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error.");
            sender.write(resp.prepare());
            return;
        }

        String fileName = msg.getArgs();
        Future<NetSocket> future = session.getDataConnection();
        if (future == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.CAN_NOT_OPEN_DATA_CONN, "Can not open data connection.");
            sender.write(resp.prepare());
            return;
        }

        PathAccessor filePath = new PathAccessor(session.getCurrentDirectory(),fileName);

        DiskFolder folder = diskFileService.getFolderByPath(user, filePath.getParentPath());
        DiskFile file = diskFileService.getFileByParent(folder, fileName);

        boolean isNewFile = file == null;
        if (isNewFile) {
            file =  diskFileService.createFile(folder,fileName);
        }

        final DiskFile targetFile = file;
        future.onSuccess(dataSender -> {
            OutputStream os = diskFileService.getOutputStream(targetFile);
            dataSender.handler(buffer -> {
               try {

                   byte[] buf = buffer.getBytes();
                   os.write(buf,0,buf.length);

               } catch (Exception e) {
                   logger.error("Error while writing file", e);
               }
            }).endHandler(v -> {
                try {
                    os.close();
                    if(!diskFileService.partialSwapFile(targetFile,session.getTransferOffset()) || !diskFileService.updateFileInfo(targetFile)) {
                        FTPMsg resp = new FTPMsg(FTPRespCode.ACTION_LOCAL_ERROR, "Error while writing file.");
                        sender.write(resp.prepare());
                    } else {
                        FTPMsg resp = new FTPMsg(FTPRespCode.DATA_CONN_CLOSING, "File transfer successful.");
                        sender.write(resp.prepare());
                    }
                    dataSender.close();
                    session.setDataConnection(null);
                } catch (Exception e) {
                    logger.error("Error while closing file", e);
                }
            });
        });

        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_STATE_OK_OPEN_DATA_CONN, "File status ok, opening data connection.");
        sender.write(resp.prepare());
    }

    @FTPControl("DELE")
    public void deleteFile(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {
        // Implement the logic to handle file deletion here.
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error.");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        String fileName = msg.getArgs();
        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(),fileName);
        DiskFile file = diskFileService.getFileByPath(user, accessor.getPath());
        if (file == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "File not found.");
            sender.write(resp.prepare());
            return;
        }

        if(diskFileService.trashFile(file.getId())) {
            FTPMsg resp = new FTPMsg(FTPRespCode.REQUESTED_FILE_ACTION_OK, "File deleted successfully.");
            sender.write(resp.prepare());
        } else {
            FTPMsg resp = new FTPMsg(FTPRespCode.ACTION_LOCAL_ERROR, "Error while deleting file.");
            sender.write(resp.prepare());
        }

    }

    @FTPControl("REST")
    public void restartFileTransfer(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {
        // Implement the logic to handle restarting file transfer here.
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error.");
            sender.write(resp.prepare());
            return;
        }

        try {
            long offset = Long.parseLong(msg.getArgs());
            session.setTransferOffset(offset);
            FTPMsg resp = new FTPMsg(FTPRespCode.REQUESTED_FILE_ACTION_OK, "Restarting file transfer at specified offset.");
            sender.write(resp.prepare());
        } catch (NumberFormatException e) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Invalid argument for REST command");
            sender.write(resp.prepare());
        }
    }

    /**
     * 获取指定文件或者目录的属性信息。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("MLST")
    public void machineListProperties(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        String path = session.getCurrentDirectory();
        if (msg.getArgs() != null && !msg.getArgs().isBlank()) {
            path = msg.getArgs();
        }
        PathAccessor absPathAcc = new PathAccessor(session.getCurrentDirectory(), path);
        DiskResource resource = diskFileService.getFolderByPath(user, absPathAcc.getPath());

        if (resource == null) {
            resource = diskFileService.getFileByPath(user, absPathAcc.getPath());
        }

        if (resource == null) {
            FTPMsg response = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "File not found: " + session.getCurrentDirectory());
            sender.write(response.prepare());
            return;
        }

        FTPResource ftpResource = new FTPResource();
        ftpResource.setDirectory(resource instanceof DiskFolder);
        ftpResource.setName(resource.getName());
        ftpResource.setGroup(user.getGroup().getGroupName());
        ftpResource.setName(resource.getName());
        ftpResource.setOwner(user.getUsername());
        ftpResource.setSize(resource instanceof DiskFile ? ((DiskFile) resource).getFileSize() : null);
        if (resource.getUpdatedOn() == null) {
            ftpResource.setLastModified(resource.getCreatedOn()
                    .atZone(ZoneId.of("UTC"))
                    .format(DateTimeFormatter
                            .ofPattern("yyyyMMMddHHmmss", Locale.ENGLISH)
                    )
            );
        } else {
            ftpResource.setLastModified(resource.getUpdatedOn()
                    .atZone(ZoneId.of("UTC"))
                    .format(DateTimeFormatter
                            .ofPattern("yyyyMMMddHHmmss", Locale.ENGLISH)
                    )
            );
        }

        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_STATUS, ftpResource.asFTPMListEntry(absPathAcc.getParentPath()));
        sender.write(resp.prepare());

    }

    /**
     * 在指定文件的指定位置追加数据，如果没有指定追加的位置，则添加到文件末尾。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("APPE")
    public void appendFile(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {
        // Implement the logic to handle file appending here.
        FTPSession session = getFTPSession(context, sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        String fileName = msg.getArgs();
        Future<NetSocket> future = session.getDataConnection();
        if (future == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.CAN_NOT_OPEN_DATA_CONN, "Can not open data connection.");
            sender.write(resp.prepare());
            return;
        }

        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(),fileName);
        DiskFile file = diskFileService.getFileByPath(user, accessor.getPath());
        if (file == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "File not found.");
            sender.write(resp.prepare());
            return;
        }

        final DiskFile targetFile = file;
        future.onSuccess(dataSender -> {
            OutputStream os = diskFileService.getOutputStream(targetFile);
            dataSender.handler(buffer -> {
                try {

                    byte[] buf = buffer.getBytes();
                    os.write(buf,0,buf.length);

                } catch (Exception e) {
                    logger.error("Error while writing file", e);
                }
            }).endHandler(v -> {
                try {
                    os.close();
                    if(!diskFileService.partialSwapFile(targetFile,targetFile.getFileSize()) || !diskFileService.updateFileInfo(targetFile)) {
                        FTPMsg resp = new FTPMsg(FTPRespCode.ACTION_LOCAL_ERROR, "Error while writing file.");
                        sender.write(resp.prepare());
                    } else {
                        FTPMsg resp = new FTPMsg(FTPRespCode.DATA_CONN_CLOSING, "File transfer successful.");
                        sender.write(resp.prepare());
                    }
                    dataSender.close();
                    session.setDataConnection(null);
                } catch (Exception e) {
                    logger.error("Error while closing file", e);
                }
            });
        });
    }

}
