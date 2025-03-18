package org.swdc.rmdisk.core.dav.locks;

import org.swdc.rmdisk.core.xmlns.XmlProperty;

public class DLockTarget {

    @XmlProperty("D:href")
    private String href;

    public DLockTarget() {
    }

    public DLockTarget(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
