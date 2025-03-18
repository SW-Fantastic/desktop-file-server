package org.swdc.rmdisk.core.dav.locks;

import org.swdc.rmdisk.core.xmlns.Namespace;
import org.swdc.rmdisk.core.xmlns.XmlFactory;
import org.swdc.rmdisk.core.xmlns.XmlProperty;

@XmlProperty(value= "D:lockinfo", namespace = @Namespace(prefix = "D", uri="DAV:"))
public class DLockInfo {

    @XmlProperty("D:lockscope")
    @XmlFactory(DLockScopes.LockScopeFactory.class)
    private DLockScopes lockScope;

    @XmlProperty("D:locktype")
    @XmlFactory(DLockTypes.LockTypeFactory.class)
    private DLockTypes lockType;

    @XmlProperty("D:owner")
    private DLockTarget owner;

    @XmlProperty("D:lockroot")
    private DLockTarget lockRoot;

    @XmlProperty("D:locktoken")
    private DLockTarget lockToken;

    @XmlProperty("D:depth")
    private String depth;

    @XmlProperty("D:timeout")
    private DLockTimeout timeout;

    public DLockTimeout getTimeout() {
        return timeout;
    }

    public void setTimeout(DLockTimeout timeout) {
        this.timeout = timeout;
    }

    public DLockTarget getLockRoot() {
        return lockRoot;
    }

    public void setLockRoot(DLockTarget lockRoot) {
        this.lockRoot = lockRoot;
    }

    public DLockTarget getLockToken() {
        return lockToken;
    }

    public void setLockToken(DLockTarget lockToken) {
        this.lockToken = lockToken;
    }

    public DLockTarget getOwner() {
        return owner;
    }

    public void setOwner(DLockTarget owner) {
        this.owner = owner;
    }

    public DLockScopes getLockScope() {
        if (lockScope instanceof DLockScopes.LockScopeShared) {
            return DLockScopes.SHARED;
        } else if (lockScope instanceof DLockScopes.LockScopeExclusive) {
            return DLockScopes.EXCLUSIVE;
        }
        return DLockScopes.SHARED;
    }

    public void setLockScope(DLockScopes lockScope) {
        this.lockScope = lockScope;
    }

    public DLockTypes getLockType() {
        if (lockType instanceof DLockTypes.WriteLockType) {
            return DLockTypes.WRITE_LOCK;
        }
        return null;
    }

    public void setLockType(DLockTypes lockType) {
        this.lockType = lockType;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDepth() {
        return depth;
    }
}
