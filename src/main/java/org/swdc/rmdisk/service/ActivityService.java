package org.swdc.rmdisk.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.core.repo.ActivityRepo;
import org.swdc.rmdisk.core.repo.UserRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Singleton
public class ActivityService {

    @Inject
    private UserRepo userRepo;

    @Inject
    private ActivityRepo activityRepo;

    @Inject
    private ServerConfigure config;

    @Transactional
    public Activity createActivity(Long userId, String address, ActivityType activityType, Class resource, Long resourceId,String property, String oldValue, String newValue) {

        if (!config.getRecordLogs()) {
            return null;
        }

        if (userId == null || userId < 0 || address == null || address.isBlank() || resource == null || resourceId == null) {
            return null;
        }

        User user = userRepo.getOne(userId);
        if (user == null) {
            return null;
        }

        Activity activity = new Activity();
        activity.setAddress(address);
        activity.setOperation(activityType);
        activity.setResource(resource.getSimpleName());
        activity.setDescription("ID:" + resourceId);
        activity.setCreatedOn(LocalDateTime.now());

        if (property != null && (oldValue != null || newValue != null)) {
            activity.setOldValue(oldValue);
            activity.setNewValue(newValue);
            activity.setProperty(property);
        }


        activity.setUser(user);
        activity = activityRepo.save(activity);

        return StatelessHelper
                .stateless(activity);

    }

    @Transactional
    public Activity createActivity(Long userId, String address, ActivityType type, String description) {

        if (!config.getRecordLogs()) {
            return null;
        }

        if (userId == 0 || userId < 0 || type == null || description == null || description.isBlank()) {
            return null;
        }

        Activity activity = new Activity();
        activity.setCreatedOn(LocalDateTime.now());
        activity.setDescription(description);
        activity.setAddress(address);
        activity.setUser(userRepo.getOne(userId));

        activity = activityRepo.save(activity);

        return StatelessHelper.stateless(activity);

    }

    @Transactional
    public void createUploadActivity(User current, String host,Long oldSize, Long newSize, DiskResource resource) {

        String oldSizeStr = oldSize != null ? (oldSize / (1000.0 * 1000.0)) + " MB" : "0 MB";
        String newSizeStr = newSize != null ? (newSize / (1000.0 * 1000.0)) + " MB" : "0 MB";

        createActivity(
                current.getId(),
                host,
                ActivityType.UPLOAD,
                resource.getClass(),
                resource.getId(),
                "size",
                oldSizeStr,
                newSizeStr
        );

    }

    @Transactional
    public void createAddResourceActivity(User current, DiskResource newRes, String host) {

        createActivity(
                current.getId(),
                host,
                ActivityType.CREATE,
                newRes.getClass(),
                newRes.getId(),
                "id",
                "null",
                newRes.getId() + ""
        );

    }

    @Transactional
    public void createMoveActivity(User current, DiskFolder oldParent, DiskFolder newParent, String host, DiskResource fileOrFolder) {
        createActivity(
                current.getId(),
                host,ActivityType.UPDATE,
                fileOrFolder.getClass(),
                fileOrFolder.getId(),
                "parent",
                oldParent.getId() + "/" + oldParent.getName(),
                newParent.getId() + "/" + newParent.getName()
        );
    }

    @Transactional
    public void createRenameActivity(User current, String oldName, String newName,String host, DiskResource fileOrFolder) {
        createActivity(
                current.getId(),
                host,ActivityType.UPDATE,
                fileOrFolder.getClass(),
                fileOrFolder.getId(),
                "name",
                oldName,
                newName
        );
    }

    @Transactional
    public void createDeleteActivity(User current, DiskResource target,String host) {
        createActivity(
                current.getId(),
                host,
                ActivityType.DELETE,
                target.getClass(),
                target.getId(),
                "id",
                target.getId() + "",
                "null"
        );
    }

    @Transactional
    public int countLogFiltered(LocalDateTime startDate, LocalDateTime endDate, String keyword, ActivityType type) {
        Long count = activityRepo.countByFilters(keyword,startDate,endDate,type);
        if (count == null) {
            return 0;
        }
        return count.intValue();
    }

    @Transactional
    public List<Activity> getLogsFiltered(LocalDateTime startDate,LocalDateTime endDate, String keyword, ActivityType type, int page, int size) {
        List<Activity> activities = activityRepo.searchByFilters(keyword,startDate,endDate,type,page,size);
        if (activities == null) {
            return Collections.emptyList();
        }
        return activities.stream()
                .map(StatelessHelper::stateless)
                .toList();
    }

    @Transactional
    public void removeLogs(List<Activity> activities) {
        for (Activity activity: activities) {
            activityRepo.remove(activity);
        }
    }

    @Transactional
    public void clearByUser(Long userId) {
        activityRepo.deleteByUserId(userId);
    }

}
