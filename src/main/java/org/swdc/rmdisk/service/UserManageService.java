package org.swdc.rmdisk.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.rmdisk.core.ManagedServerConfigure;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.core.repo.UserGroupRepo;
import org.swdc.rmdisk.core.repo.UserRegisterRequestRepo;
import org.swdc.rmdisk.core.repo.UserRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class UserManageService {

    @Inject
    private Logger logger;

    @Inject
    private DiskFileService fileService;

    @Inject
    private UserGroupRepo groupRepo;

    @Inject
    private UserRepo userRepo;

    @Inject
    private UserRegisterRequestRepo requestRepo;

    @Inject
    private CommonService commonService;

    @Inject
    private ManagedServerConfigure serverConfigure;


    @Transactional
    public List<UserGroup> getGroupsByState(State state){
        List<UserGroup> groups = groupRepo.findGroupByState(state);
        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }
        return groups.stream()
                .map(StatelessHelper::stateless)
                .toList();
    }

    @Transactional
    public List<UserRegisterRequest> getRegisterRequestByState(CommonState state){
        List<UserRegisterRequest> requests = requestRepo.findByState(state);
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(StatelessHelper::stateless)
                .toList();
    }

    @Transactional
    public boolean updateRegisterRequestState(Long requestId, CommonState state) {
        UserRegisterRequest request = requestRepo.getOne(requestId);
        if (request == null || !request.getState().equals(CommonState.PENDING)) {
            return false;
        }
        request.setState(state);
        requestRepo.save(request);
        return true;
    }


    @Transactional
    public boolean updateRegisterPermission(Long groupId, boolean state) {
        UserGroup group = groupRepo.getOne(groupId);
        if(group == null) {
            return false;
        }
        group.setRegistrable(state);
        groupRepo.save(group);
        return true;
    }


    @Transactional
    public User acceptUserRegByDefault(Long requestId) {
        if (requestId == null || requestId <= 0) {
            return null;
        }

        UserRegisterRequest request = requestRepo.getOne(requestId);
        if (request == null || request.getState() != CommonState.PENDING) {
            return null;
        }

        request.setState(CommonState.ACCEPTED);
        requestRepo.save(request);

        int defaultSize = serverConfigure.getDefaultSpaceSize();
        User user = new User();;
        user.setUsername(request.getName());
        user.setTotalSize(defaultSize * 1000 * 1000 * 1000L);
        user.setNickname(request.getNickname());
        user.setState(State.NORMAL);
        user.setGroup(request.getGroup());
        user.setPassword(request.getPassword());
        user.setCreatedOn(LocalDateTime.now());

        return saveUser(user);

    }

    /**
     * 检查用户是否存在
     * @param name 用户名
     * @return 用户是否存在
     */
    @Transactional
    public boolean checkUserExist(String name) {
        User user = userRepo.findByUserName(name);
        if (user != null) {
            return true;
        }

        UserRegisterRequest request = requestRepo.findByName(name);
        if (request != null) {
            return true;
        }

        return false;
    }


    /**
     * 处理注册请求
     * @param request 用户的注册请求
     * @return 是否成功
     */
    @Transactional
    public boolean requestUserRegister(UserRegisterRequest request) {
        UserRegisterRequest reg = new UserRegisterRequest();
        if (request.getId() != null) {
            return false;
        }
        if (request.getName() == null || request.getName().isBlank()) {
            return false;
        }
        if (request.getNickname() == null || request.getNickname().isBlank()) {
            return false;
        }
        if (request.getGroup() == null || request.getGroup().getId() == null) {
            return false;
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return false;
        }

        if (checkUserExist(request.getName())) {
            return false;
        }

        UserGroup group = groupRepo.getOne(request.getGroup().getId());
        if (group == null || group.getState() != State.NORMAL) {
            return false;
        }

        reg.setGroup(group);
        reg.setNickname(request.getNickname());
        reg.setName(request.getName());
        reg.setPassword(request.getPassword());
        reg.setCreatedOn(LocalDateTime.now());
        reg.setState(CommonState.PENDING);

        reg = requestRepo.save(reg);
        logger.info("Register request has been created. id: {}", reg.getId());
        if (serverConfigure.getAutoRegisterUser()) {
            acceptUserRegByDefault(reg.getId());
            logger.info("User has been registered automatically. id: {}", reg.getId());
        }

        return true;
    }

    @Transactional
    public List<UserGroup> getRegistrableGroups() {
        List<UserGroup> groups = groupRepo.findRegistrable();
        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }
        return groups.stream()
                .map(StatelessHelper::stateless)
                .toList();
    }

    @Transactional
    public UserGroup addGroup(String groupName) {
        if (groupName == null || groupName.isBlank()) {
            return null;
        }
        UserGroup group = groupRepo.findByName(groupName);
        if (group != null) {
            return StatelessHelper.stateless(group);
        }
        group = new UserGroup();
        group.setGroupName(groupName);
        group.setState(State.NORMAL);
        group = groupRepo.save(group);
        return StatelessHelper.stateless(group);
    }

    @Transactional
    public boolean transGroup(Long groupId) {
        if (groupId == null || groupId < 0) {
            return false;
        }
        UserGroup group = groupRepo.getOne(groupId);
        if (group == null || group.getState() == State.TRASHED) {
            return true;
        }
        group.setState(State.TRASHED);
        groupRepo.save(group);
        List<User> users = userRepo.findByGroup(groupId);
        for (User user : users) {
            deleteUser(user);
        }
        return true;
    }


    @Transactional
    public boolean renameGroup(Long groupId, String name) {
        if (groupId == null || name == null || name.isBlank()) {
            return false;
        }
        UserGroup group = groupRepo.getOne(groupId);
        if (group == null) {
            return false;
        }
        if (group.getGroupName().equals(name)) {
            return true;
        }
        group.setGroupName(name);
        groupRepo.save(group);
        return true;
    }

    @Transactional
    public User findByName(String userName) {
        User user = userRepo.findByUserName(userName);
        if (user == null) {
            return null;
        }
        return StatelessHelper
                .stateless(user);
    }


    @Transactional
    public List<User> getUserFiltered(UserGroup group, LocalDate start, LocalDate end, String keyword, int page, int size) {
        if (group == null || group.getId() == null) {
            return Collections.emptyList();
        }
        List<User> users = userRepo.searchByFilters(keyword,start,end,group.getId(),page,size);
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }


    @Transactional
    public int countUserFiltered(UserGroup group, LocalDate start, LocalDate end, String keyword) {
        if (group == null || group.getId() == null) {
            return 0;
        }
        Long count = userRepo.countByFilters(keyword,start,end,group.getId());
        if (count == null) {
            return 0;
        }
        return count.intValue();
    }

    @Transactional
    public List<UserRegisterRequest> getRegisterRequestsFiltered(LocalDate start, LocalDate end, String keyword, CommonState state, int page, int size) {
        List<UserRegisterRequest> requests = requestRepo.searchByFilters(keyword,start,end,state,page,size);
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }

    @Transactional
    public int countRegisterRequestFiltered(LocalDate start, LocalDate end, String keyword, CommonState state) {
        Long count = requestRepo.countByFilters(keyword,start,end,state);
        if (count == null) {
            return 0;
        }
        return count.intValue();
    }


    @Transactional
    public User saveUser( User user) {
        if (user == null) {
            return null;
        }

        if (user.getId() == null) {
            // new user
            if (user.getUsername() == null || user.getUsername().isBlank()) {
                return null;
            }
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                return null;
            }
            if (user.getTotalSize() == null || user.getTotalSize() <= 0) {
                return null;
            }
            if (user.getNickname() == null || user.getNickname().isBlank()) {
                return null;
            }

            if (user.getGroup() == null) {
                return null;
            }

            User exist = userRepo.findByUserName(user.getUsername());
            if (exist != null) {
                return null;
            }

            user.setGroup(groupRepo.getOne(user.getGroup().getId()));
            user.setUsedSize(0L);
            user.setCreatedOn(LocalDateTime.now());
            user.setState(State.NORMAL);
            if(user.getAvatar() == null || user.getAvatar().isBlank()) {
                byte[] defaultAvatar = commonService.getDefaultAvatar();
                if (defaultAvatar != null) {
                    user.setAvatar(Base64.getEncoder().encodeToString(defaultAvatar));
                }
            }

            user = userRepo.save(user);
            if(fileService.createFolderStructure(user)) {
                logger.info("User registered : " + user.getUsername() + " with id : " + user.getId());
                return StatelessHelper.stateless(user);
            }
            logger.error("Failed to create folder structure with user : " + user.getId());
            userRepo.remove(user);
            return null;
        } else {

            // exist user
            User exist = userRepo.getOne(user.getId());
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                if (!exist.getPassword().equals(user.getPassword())) {
                    exist.setPassword(user.getPassword());
                }
            }

            if (user.getTotalSize() != null && user.getTotalSize() > 0) {
                if (!exist.getTotalSize().equals(user.getTotalSize())) {
                    exist.setTotalSize(user.getTotalSize());
                }
            }

            if (user.getUsername() != null && !user.getUsername().isBlank()) {
                if (!checkUserExist(user.getUsername())) {
                    exist.setUsername(user.getUsername());
                }
            }

            if (user.getGroup() != null && user.getGroup().getId() != null) {
                UserGroup newGroup = groupRepo.getOne(user.getGroup().getId());
                exist.setGroup(newGroup);
            }

            if (user.getAvatar() != null && !user.getAvatar().isBlank()) {
                exist.setAvatar(user.getAvatar());
            }

            if (user.getNickname() != null && !user.getNickname().isBlank()) {
                exist.setNickname(user.getNickname());
            }

            exist = userRepo.save(exist);

            return StatelessHelper.stateless(exist);
        }
    }

    @Transactional
    public User getUser(Long id) {
        User user = userRepo.getOne(id);
        if (user == null) {
            return null;
        }
        return StatelessHelper.stateless(user);
    }

    @Transactional
    public User updateUser(Long id, User update) {
        if (id == null || id < 0 || update == null) {
            return null;
        }
        User user = userRepo.getOne(id);
        if (user == null) {
            return null;
        }

        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            user.setPassword(update.getPassword());
        }

        if (update.getAvatar() != null && !update.getAvatar().isBlank()) {
            user.setAvatar(update.getAvatar());
        }

        if (update.getNickname() != null && !update.getNickname().isBlank()) {
            user.setNickname(update.getNickname());
        }

        user = userRepo.save(user);
        return StatelessHelper.stateless(user);
    }

    /**
     * 删除用户
     *
     * @param user 用户对象
     * @return 删除成功返回true，否则返回false
     * @throws RuntimeException 如果用户为空或用户ID为空，将抛出运行时异常
     */
    @Transactional
    public boolean deleteUser(User user) {

        if (user == null || user.getId() == null) {
            return false;
        }
        user = userRepo.getOne(user.getId());
        user.setState(State.TRASHED);
        userRepo.save(user);

        return true;

    }

    /**
     * 删除用户及其所有文件和文件夹
     *
     * @param user 要删除的用户
     * @return 如果删除成功则返回true，否则返回false
     * @throws RuntimeException 如果用户为空或用户ID为空，将抛出运行时异常
     */
    @Transactional
    public boolean purgeUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        DiskFolder rootFolder = fileService.getRoot(user);
        if(rootFolder != null) {
            List<DiskFolder> subFolders = new ArrayList<>();
            fileService.collectionResources(rootFolder,subFolders);
            for (DiskFolder folder : subFolders) {
                List<DiskFile> files = fileService.getFilesByParent(folder);
                if (files != null && !files.isEmpty()) {
                    for (DiskFile file : files) {
                        fileService.trashFile(file.getId());
                    }
                }
            }
            fileService.trashFolder(rootFolder.getId());
        }
        userRepo.remove(user);
        return true;
    }

}
