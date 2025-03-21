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
import org.swdc.rmdisk.service.UserManageService;
import org.swdc.rmdisk.service.verticle.VertxFTPAbstractHandler;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FTPFolderHandler extends VertxFTPAbstractHandler {

    @Inject
    private SecureService secureService;

    @Inject
    private UserManageService userManageService;

    @Inject
    private DiskFileService diskFileService;

    /**
     * CWD - Change Working Directory
     * 切换当前的工作目录。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("CWD")
    public void changeDirectory(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for CWD command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: CWD <pathname>");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        String path = msg.getArgs();

        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(), path);
        DiskFolder resource = diskFileService.getFolderByPath(user,accessor.asDirPath());

        if (resource == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "Directory not found: " + accessor.asDirPath());
            sender.write(resp.prepare());
            return;
        }

        session.setCurrentDirectory(accessor.asDirPath());
        FTPMsg resp = new FTPMsg(FTPRespCode.REQUESTED_FILE_ACTION_OK, "\"" + accessor.asDirPath() +  "\" is current directory.");
        sender.write(resp.prepare());
    }

    /**
     * PWD - Print Working Directory
     * 返回当前的工作目录。
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("PWD")
    public void getCurrentDirectory(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for PWD command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        String path = session.getCurrentDirectory() ;
        FTPMsg resp = new FTPMsg(FTPRespCode.PATH_NAME_CREATED, "\"" + path + "\" is current directory.");
        sender.write(resp.prepare());

    }

    /**
     * RNFR - Rename From
     * 指定要重命名的文件或目录。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("RNFR")
    public void renameFrom(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for RNFR command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: RNFR <pathname>");
            sender.write(resp.prepare());
            return;
        }

        String oldFileName = msg.getArgs();
        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(), oldFileName);

        DiskFolder oldFolder = diskFileService.getFolderByPath(user,accessor.getPath());
        DiskFile oldFile = diskFileService.getFileByPath(user,accessor.getPath());

        if (oldFolder == null && oldFile == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "File or directory not found: " + oldFileName);
            sender.write(resp.prepare());
            return;
        }

        session.setRenameResource(accessor.getPath());
        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_ACTION_PENDING, "File or directory \"" + oldFileName + "\" ready for rename.");
        sender.write(resp.prepare());

    }

    /**
     * RNTO - Rename To
     * 将指定文件或目录重命名。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("RNTO")
    public void renameTo(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for RNTO command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (session.getRenameResource() == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.BAD_SEQUENCE, "Use RNFR first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: RNTO <pathname>");
            sender.write(resp.prepare());
            return;
        }

        String newFileName = msg.getArgs();

        PathAccessor absOldTarget = new PathAccessor(session.getCurrentDirectory(), session.getRenameResource());

        DiskFolder oldFolder = diskFileService.getFolderByPath(user,absOldTarget.getPath());
        if (oldFolder != null) {
            // 被改名的是文件夹
            DiskFolder parentFolder = oldFolder.getParent();
            DiskFolder newFolder = diskFileService.getFolderByParent(parentFolder, newFileName);
            if (newFolder != null) {
                // 指定名称的文件夹已存在，不能改名
                FTPMsg resp = new FTPMsg(FTPRespCode.FILE_NAME_NOT_ALLOWED, "Directory already exists: " + newFileName);
                sender.write(resp.prepare());
                return;
            }
            diskFileService.renameFolder(oldFolder, newFileName);
            session.setRenameResource(null);

            FTPMsg resp = new FTPMsg(FTPRespCode.REQUESTED_FILE_ACTION_OK, "Directory \"" + session.getRenameResource() + "\" renamed to \"" + newFileName + "\".");
            sender.write(resp.prepare());

            return;
        }

        DiskFile oldFile = diskFileService.getFileByPath(user,absOldTarget.getPath());;
        if (oldFile != null) {
            // 被改名的是文件
            DiskFolder parentFolder = oldFile.getParent();
            DiskFile newFile = diskFileService.getFileByParent(parentFolder, newFileName);
            if (newFile != null) {
                // 指定名称的文件已存在，不能改名
                FTPMsg resp = new FTPMsg(FTPRespCode.FILE_NAME_NOT_ALLOWED, "File already exists: " + newFileName);
                sender.write(resp.prepare());
                return;
            }

            diskFileService.renameFile(oldFile, newFileName);
            session.setRenameResource(null);

            FTPMsg resp = new FTPMsg(FTPRespCode.REQUESTED_FILE_ACTION_OK, "File \"" + session.getRenameResource() + "\" renamed to \"" + newFileName + "\".");
            sender.write(resp.prepare());
            return;
        }
        // 未找到改名资源，改名失败
        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "File or directory not found: " + session.getRenameResource());
        sender.write(resp.prepare());
    }

    /**
     * MKD - Make Directory
     * 创建新目录。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("MKD")
    public void makeDirectory(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for MKD command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: MKD <pathname>");
            sender.write(resp.prepare());
            return;
        }

        String folderName = msg.getArgs();

        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(), folderName);
        PathAccessor parent = accessor.getParent();

        // 检查父目录是否存在
        DiskFolder parentFolder = diskFileService.getFolderByPath(user, parent.getPath());
        if (parentFolder == null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "Parent directory not found: " + parent.getPath());
            sender.write(resp.prepare());
            return;
        }

        // 检查同名目录是否已存在
        DiskFolder existFolder = diskFileService.getFolderByParent(parentFolder, folderName);
        if (existFolder != null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_NAME_NOT_ALLOWED, "Directory already exists: " + folderName);
            sender.write(resp.prepare());
            return;
        }

        // 创建新目录
        DiskFolder folder = diskFileService.createFolder(parentFolder, folderName);
        if (folder != null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.PATH_NAME_CREATED, "Directory created: \"" + folderName + "\"");
            sender.write(resp.prepare());
        } else {
            FTPMsg resp = new FTPMsg(FTPRespCode.ACTION_LOCAL_ERROR, "Can not create directory: \"" + folderName + "\"");
            sender.write(resp.prepare());
        }

    }

    /**
     * LIST - List files and directories
     * 列出当前目录下的文件和文件夹。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("LIST")
    public void list(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

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

        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(), path);
        DiskFolder folder = diskFileService.getFolderByPath(user, accessor.getPath());
        if (folder == null) {
            FTPMsg response = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "Directory not found: " + session.getCurrentDirectory());
            sender.write(response.prepare());
            return;
        }

        Future<NetSocket> future = session.getDataConnection();
        if (future == null) {
            FTPMsg response = new FTPMsg(FTPRespCode.CAN_NOT_OPEN_DATA_CONN, "Can not open data connection.");
            sender.write(response.prepare());
            return;
        }

        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_STATE_OK_OPEN_DATA_CONN, "File status okay; about to open data connection.");
        sender.write(resp.prepare());

        future.onSuccess(dataSender -> {
            // handle connection

            List<DiskFolder> folders = diskFileService.getChildren(folder);
            List<DiskFile> files = diskFileService.getFilesByParent(folder);

            List<? extends DiskResource> resources = List.of(folders, files).stream()
                    .flatMap(List::stream).toList();

            for (DiskResource resource: resources) {
                FTPResource ftpResource = new FTPResource();
                ftpResource.setDirectory(resource instanceof DiskFolder);
                ftpResource.setName(resource.getName());
                ftpResource.setGroup(user.getGroup().getGroupName());
                ftpResource.setName(resource.getName());
                ftpResource.setOwner(user.getUsername());
                ftpResource.setSize(resource instanceof DiskFile ? ((DiskFile) resource).getFileSize() : null);
                if (folder.getUpdatedOn() == null) {
                    ftpResource.setLastModified(folder.getCreatedOn()
                            .atZone(ZoneId.of("UTC"))
                            .format(DateTimeFormatter
                                    .ofPattern("MMM dd HH:mm", Locale.ENGLISH)
                            )
                    );
                } else {
                    ftpResource.setLastModified(folder.getUpdatedOn()
                            .atZone(ZoneId.of("UTC"))
                            .format(DateTimeFormatter
                                    .ofPattern("MMM dd HH:mm", Locale.ENGLISH)
                            )
                    );
                }
                dataSender.write(ftpResource.asFTPFileListEntry());
            }

            FTPMsg ctrl = new FTPMsg(FTPRespCode.DATA_CONN_CLOSING, "Transfer complete.");
            sender.write(ctrl.prepare());
            dataSender.close();

            session.setDataConnection(null);

        });

    }



    /**
     * NLST指令，返回文件名列表。
     * 这是简化版本的List。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("NLST")
    public void nameList(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

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
        String absPath = new PathAccessor(session.getCurrentDirectory(), path).getPath();
        DiskFolder folder = diskFileService.getFolderByPath(user, absPath);
        if (folder == null) {
            FTPMsg response = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "Directory not found: " + session.getCurrentDirectory());
            sender.write(response.prepare());
            return;
        }

        Future<NetSocket> future = session.getDataConnection();
        if (future == null) {
            FTPMsg response = new FTPMsg(FTPRespCode.CAN_NOT_OPEN_DATA_CONN, "Can not open data connection.");
            sender.write(response.prepare());
            return;
        }

        session.clearTransferState();

        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_STATE_OK_OPEN_DATA_CONN, "Opening data connection.");
        sender.write(resp.prepare());

        future.onSuccess(dataSender -> {
            // handle connection

            List<DiskFolder> folders = diskFileService.getChildren(folder);
            List<DiskFile> files = diskFileService.getFilesByParent(folder);

            List<? extends DiskResource> resources = List.of(folders, files).stream()
                    .flatMap(List::stream).toList();

            for (DiskResource resource: resources) {
                dataSender.write(resource.getName() + "\r\n");
            }

            FTPMsg ctrl = new FTPMsg(FTPRespCode.DATA_CONN_CLOSING, "Transfer complete.");
            sender.write(ctrl.prepare());
            dataSender.close();

            session.setDataConnection(null);
        });


    }


    /**
     * RMD - Remove directory
     * 删除一个目录。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("RMD")
    public void delete(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for DELE command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        String fileName = "";
        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            fileName = msg.getArgs();
        } else {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: DELE <pathname>");
            sender.write(resp.prepare());
        }

        PathAccessor accessor = new PathAccessor(session.getCurrentDirectory(), fileName);
        DiskFolder existFolder = diskFileService.getFolderByPath(user, accessor.getPath());

        if (existFolder != null) {
            List<DiskFolder> subFolders = new ArrayList<>();
            diskFileService.collectionResources(existFolder,subFolders);
            for (DiskFolder diskFolder: subFolders) {
                List<DiskFile> files = diskFileService.getFilesByParent(diskFolder);
                if (files != null) {
                    for (DiskFile file: files) {
                        diskFileService.trashFile(file.getId());
                    }
                }
                diskFileService.trashFolder(diskFolder.getId());
            }

            FTPMsg resp = new FTPMsg(FTPRespCode.REQUESTED_FILE_ACTION_OK, "Folder deleted: \"" + fileName + "\"");
            sender.write(resp.prepare());
            return;
        }

        FTPMsg resp = new FTPMsg(FTPRespCode.FILE_UNAVAILABLE, "Folder not found: \"" + fileName + "\"");
        sender.write(resp.prepare());

    }


}
