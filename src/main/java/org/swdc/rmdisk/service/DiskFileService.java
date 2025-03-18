package org.swdc.rmdisk.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.fx.FXResources;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.core.repo.DiskFileRepo;
import org.swdc.rmdisk.core.repo.DiskFolderRepo;
import org.swdc.rmdisk.core.repo.UserRepo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class DiskFileService implements EventEmitter {

    @Inject
    private Logger logger;

    @Inject
    private DiskFolderRepo folderRepo;

    @Inject
    private DiskFileRepo fileRepo;

    @Inject
    private TemplateService templateService;

    @Inject
    private UserRepo userRepo;

    @Inject
    private FXResources resource;

    private Events events;

    @Override
    public <T extends AbstractEvent> void emit(T t) {
        events.dispatch(t);
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }

    public DiskFolder getRoot(User user) {

        if (user == null){
            return null;
        }
        DiskFolder folder = folderRepo.getUserRootFolder(user.getId());
        if (folder == null) {
            if(createFolderStructure(user)) {
                folder = folderRepo.getUserRootFolder(user.getId());
                if (folder == null){
                    return null;
                }
            } else {
                return null;
            }
        }
        return StatelessHelper.stateless(folder);
    }

    @Transactional
    public DiskFolder moveFolderInto(DiskFolder source, DiskFolder target) {

        if (source == null || source.getId() == null || source.getId() < 0) {
            return null;
        }
        if (target == null || target.getId() == null || target.getId() < 0) {
            return null;
        }

        source = folderRepo.getOne(source.getId());
        target = folderRepo.getOne(target.getId());

        DiskFolder exist = folderRepo.getByParentAndName(target.getId(),source.getName());
        if (exist != null) {
            return null;
        }

        source.setParent(target);

        source = folderRepo.save(source);
        return StatelessHelper
                .stateless(source);
    }

    @Transactional
    public DiskFile moveFileInto(DiskFile source, DiskFolder target) {

        if (source == null || source.getId() == null || source.getId() < 0) {
            return null;
        }
        if (target == null || target.getId() == null || target.getId() < 0) {
            return null;
        }

        source = fileRepo.getOne(source.getId());
        target = folderRepo.getOne(target.getId());

        DiskFile exist = fileRepo.findByFolderAndName(target.getId(),source.getName());
        if (exist != null) {
            return null;
        }
        source.setParent(target);
        source = fileRepo.save(source);
        return StatelessHelper
                .stateless(source);
    }

    @Transactional
    public List<DiskFolder> getChildren(DiskFolder parent) {
        if (parent == null) {
            return Collections.emptyList();
        }
        if (parent.getId() < 0) {
            return Collections.emptyList();
        }
        List<DiskFolder> folders = folderRepo.getByParent(parent.getId());
        if (folders == null) {
            return Collections.emptyList();
        }
        return folders.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiskFile createFile(DiskFolder parent, String name) {
        if (parent == null || parent.getId() == null || parent.getId() < 0) {
            return null;
        }
        if (name == null || name.isBlank()) {
            return null;
        }
        parent = folderRepo.getOne(parent.getId());
        DiskFile target = fileRepo.findByFolderAndName(parent.getId(),name);
        if (target != null) {
            return StatelessHelper
                    .stateless(target);
        }
        target = new DiskFile();
        target.setName(name);
        target.setParent(parent);
        target.setOwner(parent.getOwner());
        target.setUuid(UUID.randomUUID().toString());
        target.setCreatedOn(LocalDateTime.now());

        Tika tika = new Tika();
        String mime = tika.detect(name);
        target.setMimeType(mime);

        target = fileRepo.save(target);

        return StatelessHelper
                .stateless(target);
    }

    @Transactional
    public DiskFolder createFolder(DiskFolder parent, String name) {
        if (parent == null || parent.getId() == null || parent.getId() < 0) {
            return null;
        }
        if (name == null || name.isBlank()) {
            return null;
        }

        DiskFolder parentFolder = folderRepo.getOne(parent.getId());

        DiskFolder exist = folderRepo.getByParentAndName(parentFolder.getId(),name);
        if (exist != null) {
            return StatelessHelper
                    .stateless(exist);
        }

        DiskFolder folder = new DiskFolder();
        folder.setParent(parentFolder);
        folder.setName(name);
        folder.setOwner(parent.getOwner());
        folder.setCreatedOn(LocalDateTime.now());
        folder = folderRepo.save(folder);
        logger.info("Create folder with parent : [ id = " + parent.getId() + " ] " + folder.getName());
        return StatelessHelper
                .stateless(folder);
    }

    @Transactional
    public DiskFile renameFile(DiskFile file, String newName) {
        if (file == null || file.getId() == null || file.getId() < 0) {
            return null;
        }
        if (newName == null || newName.isBlank()) {
            return null;
        }
        file = fileRepo.getOne(file.getId());
        DiskFile exist = fileRepo.findByFolderAndName(file.getParent().getId(),newName);
        if (exist != null) {
            return null;
        }
        file.setName(newName);
        file.setUpdatedOn(LocalDateTime.now());
        file = fileRepo.save(file);
        return StatelessHelper.stateless(file);
    }

    @Transactional
    public DiskFolder renameFolder(DiskFolder folder, String newName) {
        if (folder == null || folder.getId() == null || folder.getId() < 0) {
            return null;
        }
        if (newName == null || newName.isBlank()) {
            return null;
        }
        folder = folderRepo.getOne(folder.getId());
        DiskFolder exist = folderRepo.getByParentAndName(folder.getId(),newName);
        if (exist != null) {
            return null;
        }
        folder.setName(newName);
        folder = folderRepo.save(folder);
        return StatelessHelper
                .stateless(folder);
    }

    @Transactional
    public boolean trashFile(Long fileId) {
        if (fileId == null || fileId < 0) {
            return false;
        }
        try {

            DiskFile file = fileRepo.getOne(fileId);
            User owner = file.getOwner();
            File target = getSystemFile(file);
            fileRepo.remove(file);

            if (target != null) {
                long length = target.length();
                if(target.delete()) {
                    owner.setUsedSize(owner.getUsedSize() - length);
                    userRepo.save(owner);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Transactional
    public void collectionResources(DiskFolder folder, List<DiskFolder> folders) {

        if (folder == null || folder.getId() == null || folder.getId() < 0 || folders == null) {
            return;
        }

        List<DiskFolder> diskFolders = folderRepo.getByParent(folder.getId());
        if (diskFolders != null && !diskFolders.isEmpty()) {
            for (DiskFolder sub: diskFolders) {
                collectionResources(sub,folders);
            }
        }

        folders.add(StatelessHelper.stateless(folder));

    }

    @Transactional
    public boolean trashFolder(Long folderId) {

        if (folderId == null || folderId < 0) {
            return false;
        }

        DiskFolder folder = folderRepo.getOne(folderId);
        List<DiskFile> files = fileRepo.getFilesByParent(folder.getId());
        if (files != null &&  !files.isEmpty()) {
            return false;
        }

        List<DiskFolder> subFolders = folderRepo.getByParent(folder.getId());
        if (subFolders != null && !subFolders.isEmpty()) {
            return false;
        }

        folderRepo.remove(folder);
        return true;
    }


    @Transactional
    public DiskFolder getFolderByPath(User user, String path) {
        if (user == null || user.getId() < 0 || path == null || path.isBlank()) {
            return null;
        }

        DiskFolder folder = folderRepo.getUserRootFolder(user.getId());
        String[] parts = path.split("/");
        for (int pIndex = 1; pIndex < parts.length; pIndex ++) {
            String part = parts[pIndex];
            folder = folderRepo.getByParentAndName(folder.getId(),part);
            if (folder == null) {
                return null;
            }
        }

        return StatelessHelper.stateless(folder);
    }

    @Transactional
    public DiskFile getFileByPath(User user, String path) {
        if (user == null || user.getId() < 0 || path == null || path.isBlank()) {
            return null;
        }

        String[] parts = path.split("/");
        if (parts.length < 2) {
            return null;
        }
        DiskFolder folder = folderRepo.getUserRootFolder(user.getId());
        for (int pIndex = 1; pIndex < parts.length - 1; pIndex ++) {
            String part = parts[pIndex];
            folder = folderRepo.getByParentAndName(folder.getId(),part);
            if (folder == null) {
                return null;
            }
        }
        String name = parts[parts.length - 1];
        DiskFile file = fileRepo.findByFolderAndName(folder.getId(),name);
        if (file == null) {
            return null;
        }
        return StatelessHelper.stateless(file);
    }

    @Transactional
    public DiskFile getFileByParent(DiskFolder parent, String name) {
        if (parent == null || parent.getId() == null || parent.getId() < 0) {
            return null;
        }
        if (name == null || name.isBlank()) {
            return null;
        }
        DiskFile file = fileRepo.findByFolderAndName(parent.getId(),name);
        if (file == null) {
            return null;
        }
        return StatelessHelper.stateless(file);
    }

    @Transactional
    public DiskFolder getFolderByParent(DiskFolder parent, String name) {
        if (parent == null || parent.getId() == null || parent.getId() < 0) {
            return null;
        }
        if (name == null || name.isBlank()) {
            return null;
        }
        DiskFolder folder = folderRepo.getByParentAndName(parent.getId(),name);
        if (folder == null) {
            return null;
        }
        return StatelessHelper.stateless(folder);
    }


    @Transactional
    public List<DiskFile> getFilesByParent(DiskFolder parent) {
        if (parent == null || parent.getId() == null || parent.getId() < 0) {
            return Collections.emptyList();
        }
        List<DiskFile> files = fileRepo.getFilesByParent(parent.getId());
        if (files == null) {
            return Collections.emptyList();
        }
        return files.stream().map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }

    public File getSystemFile(DiskFile input) {
        String file = resource.getAssetsFolder().getAbsolutePath() + File.separator + "blocks";
        File target = new File(file);
        if (!target.exists() && !target.mkdirs()) {
            return null;
        }
        File theFile = new File(target.getAbsolutePath() + File.separator + input.getUuid());
        if (!theFile.exists()) {
            return null;
        }
        return theFile;
    }

    /**
     * 获取输出流
     *
     * 该方法根据传入的DiskFile对象，获取一个指向临时文件目录的输出流。
     *
     * @param output DiskFile对象，用于指定输出流的目标文件
     * @return 指向临时文件目录的输出流，如果文件创建失败或找不到文件则返回null
     */
    public OutputStream getOutputStream(DiskFile output) {
        String file = resource.getAssetsFolder().getAbsolutePath() + File.separator + "tmp";
        File target = new File(file);
        if (!target.exists() && !target.mkdirs()) {
            return null;
        }
        File outputFile = new File(target.getAbsolutePath() + File.separator + output.getUuid());
        if (!outputFile.exists()) {
            try {
                if(!outputFile.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                logger.error("failed to create file", e);
                return null;
            }
        }
        try {
            return new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 交换文件
     *
     * 该方法用于将临时文件移动到指定的目标目录，并替换同名文件。
     *
     * @param target 要交换的目标文件对象
     * @return 如果文件交换成功，则返回true；否则返回false。
     */
    public boolean swapFile(DiskFile target) {
        return partialSwapFile(target, 0L);
    }

    /**
     * 部分交换文件, 用于从特定的位置写入文件内容，以此实现断点续传。
     * @param target DiskFile对象，表示要交换的目标文件。该对象的uuid字段将被用于定位临时文件和目标文件的路径。
     * @param targetOffset long类型，表示从目标文件的哪个位置开始写入。如果为0L，则从头覆盖；否则，将从指定偏移量处继续写入。
     * @return 如果文件交换成功，则返回true；否则返回false。
     */
    public boolean partialSwapFile(DiskFile target, long targetOffset) {
        // 获取临时文件的目录
        String file = resource.getAssetsFolder().getAbsolutePath() + File.separator + "tmp";
        File tempFolder = new File(file);
        if (!tempFolder.exists() && !tempFolder.mkdirs()) {
            return false;
        }
        // 获取上传完毕的临时文件
        File tempFile = new File(tempFolder.getAbsolutePath() + File.separator + target.getUuid());
        if (!tempFile.exists()) {
            return false;
        }

        // 获取被替换文件的目录
        String fileReplace = resource.getAssetsFolder().getAbsolutePath() + File.separator + "blocks";
        File fileRepo = new File(fileReplace);
        if (!fileRepo.exists() && !fileRepo.mkdirs()) {
            return false;
        }

        File targetFile = new File(fileRepo.getAbsolutePath() + File.separator + target.getUuid());

        if (targetOffset == 0L) {
            // 获取被替换的目标文件
            try {
                Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                return false;
            }

        } else {

            try {
                if (!targetFile.exists()) {
                    return false;
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile.getAbsolutePath(), "rw");
                randomAccessFile.seek(targetOffset);
                byte[] buffer = new byte[1024];
                int bytesRead;
                FileInputStream tempIs = new FileInputStream(tempFile);
                while ((bytesRead = tempIs.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, bytesRead);
                }
                tempIs.close();
                randomAccessFile.close();
                return true;
            } catch (IOException e) {
                return false;
            }


        }

    }


    public void clearFailedResource(DiskFile output){
        String file = resource.getAssetsFolder().getAbsolutePath() + File.separator + "tmp";
        File target = new File(file);
        if (!target.exists() && !target.mkdirs()) {
            return;
        }
        File outputFile = new File(target.getAbsolutePath() + File.separator + output.getUuid());
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    @Transactional
    public boolean updateFileInfo(DiskFile theFile) {

        String file = resource.getAssetsFolder().getAbsolutePath() + File.separator + "blocks";
        File target = new File(file);
        if (!target.exists() && !target.mkdirs()) {
            return false;
        }
        File outputFile = new File(target.getAbsolutePath() + File.separator + theFile.getUuid());
        theFile = fileRepo.getOne(theFile.getId());
        long length = 0;
        if (theFile.getFileSize() != null) {
            length = theFile.getFileSize();
        }

        User owner = theFile.getOwner();
        owner.setUsedSize((owner.getUsedSize() - length) + outputFile.length());
        theFile.setFileSize(outputFile.length());
        theFile.setUpdatedOn(LocalDateTime.now());

        fileRepo.save(theFile);
        userRepo.save(owner);
        return true;
    }

    @Transactional
    public boolean createFolderStructure(User user) {

        if (user == null || user.getId() == null) {
            return false;
        }

        user = userRepo.getOne(user.getId());
        UserGroup userGroup = user.getGroup();


        TemplateFolder templateFolder = templateService.getRoot(userGroup.getId());

        DiskFolder userRootDir = folderRepo.getUserRootFolder(user.getId());
        if (userRootDir == null) {
            userRootDir = new DiskFolder();
            userRootDir.setOwner(user);
            userRootDir.setName("UserRoot");
            userRootDir.setParent(null);
            userRootDir.setCreatedOn(LocalDateTime.now());
            userRootDir = folderRepo.save(userRootDir);
        }

        reverseCreateFolder(templateFolder,userRootDir);
        return true;
    }

    private void reverseCreateFolder(TemplateFolder template, DiskFolder parent) {
        List<TemplateFolder> child = templateService.getTemplateFolders(template.getId());
        for (TemplateFolder item : child) {
            DiskFolder folder = folderRepo.getByParentAndName(parent.getId(),item.getName());
            if (folder == null){
                folder = new DiskFolder();
                folder.setParent(parent);
                folder.setName(item.getName());
                folder.setOwner(parent.getOwner());
                folder.setCreatedOn(LocalDateTime.now());
                folder = folderRepo.save(folder);
                logger.info("Generated folder with parent : [ id = " + parent.getId() + " ] " + folder.getName());
            }
            reverseCreateFolder(item,folder);
        }
    }



}
