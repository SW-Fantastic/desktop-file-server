package org.swdc.rmdisk.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.core.repo.DiskFileRepo;
import org.swdc.rmdisk.core.repo.DiskFolderRepo;
import org.swdc.rmdisk.core.dav.locks.DLockInfo;
import org.swdc.rmdisk.core.dav.locks.DLockScopes;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DiskLockService {

    @Inject
    private DiskFolderRepo folderRepo;

    @Inject
    private DiskFileRepo fileRepo;


    private Cache<String, LockedResource> lockedResourceCache = Caffeine
            .newBuilder()
            .expireAfterWrite(Duration.ofDays(1))
            .build();

    public boolean writable(User requester, String lockId, DiskResource resource) {

        lockedResourceCache.cleanUp();
        if (lockId != null) {
            LockedResource lock = lockedResourceCache.getIfPresent(lockId);
            if (lock != null && lock.getOwnerId().equals(requester.getId())) {
                return true;
            }
        }

        return canExclusiveLock(requester,resource);

    }

    public boolean canSharedLock(User requester, DiskResource resource) {

        if (requester == null || resource == null) {
            return false;
        }

        List<Long> parentIds = new ArrayList<>();
        DiskFolder folder = null;
        if (resource instanceof DiskFolder) {
            folder = (DiskFolder) resource;
            folder = folderRepo.getOne(folder.getId());
            folder = folder.getParent();
        } else {
            DiskFile file = (DiskFile) resource;
            file = fileRepo.getOne(file.getId());
            folder = file.getParent();
        }

        while (folder != null) {
            parentIds.add(folder.getId());
            folder = folder.getParent();
        }

        // 遍历Lock
        for(LockedResource locked : lockedResourceCache.asMap().values()) {
            if (locked.getResourceType() == DiskFolder.class) {
                if (parentIds.contains(locked.getResourceId())) {
                    // 有Parent目录被锁定了。
                    if (DLockScopes.EXCLUSIVE == locked.getLockType()) {
                        // 这是一个独占锁
                        if(!locked.getOwnerId().equals(requester.getId())) {
                            return false;
                        }
                    }
                } else if (locked.getResourceId().equals(resource.getId())) {
                    if (resource instanceof DiskFolder) {
                        // 目标资源存在锁。
                        if (locked.getLockScope() == DLockScopes.EXCLUSIVE) {
                            if(!locked.getOwnerId().equals(requester.getId())) {
                                return false;
                            }
                        }
                    }
                }
            } else {
                if (locked.getResourceId().equals(resource.getId())) {
                    // 目标资源存在锁。
                    if (locked.getLockScope() == DLockScopes.EXCLUSIVE) {
                        if(!locked.getOwnerId().equals(requester.getId())) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;

    }

    public boolean canExclusiveLock(User requester, DiskResource resource) {

        if (requester == null || resource == null) {
            return false;
        }

        List<Long> parentIds = new ArrayList<>();
        DiskFolder folder = null;
        if (resource instanceof DiskFolder) {
            folder = (DiskFolder) resource;
            folder = folderRepo.getOne(folder.getId());
            folder = folder.getParent();
        } else {
            DiskFile file = (DiskFile) resource;
            file = fileRepo.getOne(file.getId());
            folder = file.getParent();
        }

        while (folder != null) {
            parentIds.add(folder.getId());
            folder = folder.getParent();
        }

        for(LockedResource locked : lockedResourceCache.asMap().values()) {
            if (locked.getResourceType() == DiskFolder.class) {
                if (parentIds.contains(locked.getResourceId())) {
                    // 有Parent目录被锁定了。
                    if(!locked.getOwnerId().equals(requester.getId())) {
                        return false;
                    }
                } else if (locked.getResourceId().equals(resource.getId())) {
                    if (resource instanceof DiskFolder) {
                        // 目标资源存在锁。
                        if(!locked.getOwnerId().equals(requester.getId())) {
                            return false;
                        }
                    }
                }
            } else {
                if (locked.getResourceId().equals(resource.getId())) {
                    // 目标资源存在锁。
                    if(!locked.getOwnerId().equals(requester.getId())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public LockedResource refreshLock(User requester,String oldToken) {

        if (requester == null ||  oldToken == null || oldToken.isBlank()) {
            return null;
        }

        LockedResource lockedResource = lockedResourceCache.getIfPresent(oldToken);
        if (lockedResource != null && lockedResource.getOwnerId().equals(requester.getId())) {
            lockedResourceCache.invalidate(oldToken);
            lockedResource = new LockedResource(lockedResource);
            lockedResourceCache.put(lockedResource.getCacheId(),lockedResource);
            return lockedResource;
        }

        return null;
    }

    public boolean unlock(User requester, String lockToken) {

        if (requester == null ||  lockToken == null || lockToken.isBlank()) {
            return false;
        }

        LockedResource lockedResource = lockedResourceCache.getIfPresent(lockToken);
        if (lockedResource != null && lockedResource.getOwnerId().equals(requester.getId())) {
            lockedResourceCache.invalidate(lockToken);
        }

        return true;
    }

    public LockedResource tryLock(User requester, DLockInfo request, DiskResource resource) {

        if (requester == null || request == null || resource == null) {
            return null;
        }

        if (request.getLockScope() == DLockScopes.EXCLUSIVE) {
            boolean exclusive = canExclusiveLock(requester,resource);
            if(exclusive) {
                String existKey = null;
                for (LockedResource lockedResource: lockedResourceCache.asMap().values()) {
                    if(
                            lockedResource.getOwnerId().equals(requester.getId()) &&
                                    lockedResource.getResourceType() == resource.getClass() &&
                                    lockedResource.getResourceId().equals(resource.getId()) &&
                                    lockedResource.getLockScope() ==  DLockScopes.EXCLUSIVE
                    ) {
                        existKey = lockedResource.getCacheId();
                        break;
                    }
                }
                if (existKey != null) {
                    lockedResourceCache.invalidate(existKey);
                }
                LockedResource lockedResource = new LockedResource(requester,request,resource);
                lockedResourceCache.put(lockedResource.getCacheId(),lockedResource);
                return lockedResource;
            } else {
                return null;
            }
        } else {
            boolean sharedLock = canSharedLock(requester,resource);
            if (sharedLock) {

                String existKey = null;
                for (LockedResource lockedResource: lockedResourceCache.asMap().values()) {
                    if(
                            lockedResource.getOwnerId().equals(requester.getId()) &&
                                    lockedResource.getResourceType() == resource.getClass() &&
                                    lockedResource.getResourceId().equals(resource.getId())
                    ) {
                        existKey = lockedResource.getCacheId();
                        break;
                    }
                }
                if (existKey != null) {
                    lockedResourceCache.invalidate(existKey);
                }
                LockedResource lockedResource = new LockedResource(requester,request,resource);
                lockedResourceCache.put(lockedResource.getCacheId(),lockedResource);
                return lockedResource;
            } else {
                return null;
            }
        }
    }

}
