package org.swdc.rmdisk.core.entity;

import org.swdc.rmdisk.core.dav.locks.DLockInfo;
import org.swdc.rmdisk.core.dav.locks.DLockScopes;
import org.swdc.rmdisk.core.dav.locks.DLockTypes;

import java.util.UUID;

public class LockedResource {

    private Class resourceType;

    private DLockTypes lockType;

    private DLockScopes lockShare;

    private Long resourceId;

    private Long ownerId;

    private String cacheId;
    public LockedResource(User requester, DLockInfo request, DiskResource resource) {

        this.lockShare = request.getLockScope();
        this.lockType = request.getLockType();
        this.ownerId = resource.getOwner().getId();
        this.resourceType = resource.getClass();
        this.resourceId = resource.getId();
        this.cacheId = UUID.randomUUID().toString();

    }

    public LockedResource(LockedResource resource) {

        this.lockShare = resource.getLockScope();
        this.lockType = resource.getLockType();
        this.ownerId = resource.getOwnerId();
        this.resourceType = resource.getResourceType();
        this.resourceId = resource.getResourceId();
        this.cacheId = UUID.randomUUID().toString();

    }

    public Class getResourceType() {
        return resourceType;
    }

    public DLockTypes getLockType() {
        return lockType;
    }

    public DLockScopes getLockScope() {
        return lockShare;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getCacheId() {
        return cacheId;
    }

}
