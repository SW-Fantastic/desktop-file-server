package org.swdc.rmdisk.core.dav.locks;

import org.swdc.rmdisk.core.xmlns.XmlProperty;

public class DLockTimeout {

    @XmlProperty("D:timeout")
    private String lockTimeout;

    public DLockTimeout() {
    }

    public DLockTimeout(Long seconds) {
        this.lockTimeout = "Seconds-" + seconds;
    }

    public void setLockTimeout(String lockTimeout) {
        this.lockTimeout = lockTimeout;
    }

    public String getLockTimeout() {
        return lockTimeout;
    }

}
