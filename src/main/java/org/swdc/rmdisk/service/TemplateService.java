package org.swdc.rmdisk.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.rmdisk.core.entity.TemplateFolder;
import org.swdc.rmdisk.core.entity.UserGroup;
import org.swdc.rmdisk.core.repo.TemplateFolderRepo;
import org.swdc.rmdisk.core.repo.UserGroupRepo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class TemplateService {

    @Inject
    private TemplateFolderRepo folderRepo;

    @Inject
    private UserGroupRepo groupRepo;

    @Transactional
    public TemplateFolder getRoot(Long userGroupId) {
        if (userGroupId == null) {
            return null;
        }
        UserGroup group = groupRepo.getOne(userGroupId);
        if (group == null) {
            return null;
        }

        TemplateFolder folder = folderRepo.findRootByGroup(userGroupId);
        if (folder == null) {
            folder = new TemplateFolder();
            folder.setName(group.getGroupName());
            folder.setGroup(group);
            folder.setParent(null);
            folder = folderRepo.save(folder);
        }
        return StatelessHelper.stateless(folder);
    }

    @Transactional
    public TemplateFolder addTemplateFolder(Long parentId, String name) {
        if (parentId == null) {
            return null;
        }
        TemplateFolder folder = folderRepo.findTemplateFolderByName(parentId,name);
        if (folder == null) {
            TemplateFolder parent = folderRepo.getOne(parentId);
            TemplateFolder target = new TemplateFolder();
            target.setGroup(parent.getGroup());
            target.setName(name);
            target.setParent(parent);
            target = folderRepo.save(target);
            return StatelessHelper.stateless(target);
        }
        return StatelessHelper.stateless(folder);
    }

    @Transactional
    public boolean trashTemplateFolder(Long folderId) {

        if (folderId == null || folderId < 0) {
            return false;
        }
        TemplateFolder folder = folderRepo.getOne(folderId);
        if (folder == null || folder.getParent() == null) {
            return false;
        }
        folderRepo.remove(folder);
        return true;

    }

    @Transactional
    public TemplateFolder renameTemplateFolder(Long folderId, String name) {

        if (folderId == null || name == null || name.isBlank()) {
            return null;
        }

        TemplateFolder folder = folderRepo.getOne(folderId);
        if (folder == null) {
            return null;
        }
        folder.setName(name);
        folder = folderRepo.save(folder);

        return StatelessHelper
                .stateless(folder);
    }

    @Transactional
    public List<TemplateFolder> getTemplateFolders(Long parentId) {
        if (parentId == null) {
            return Collections.emptyList();
        }
        TemplateFolder folder = folderRepo.getOne(parentId);
        if (folder == null) {
            return Collections.emptyList();
        }
        List<TemplateFolder> folders = folder.getChildren();
        if (folders == null || folders.isEmpty()) {
            return Collections.emptyList();
        }
        return folders.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }

}
