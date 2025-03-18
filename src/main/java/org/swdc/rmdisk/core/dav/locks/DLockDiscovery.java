package org.swdc.rmdisk.core.dav.locks;

import org.swdc.rmdisk.core.xmlns.Namespace;
import org.swdc.rmdisk.core.xmlns.XmlProperty;

import java.util.List;

@XmlProperty(value = "D:lockdiscovery",namespace = @Namespace(prefix = "D",uri = "DAV:" ))
public class DLockDiscovery {

    @XmlProperty(value = "D:activelock")
    private List<DLockInfo> activeLocks;

    public DLockDiscovery() {
    }

    public DLockDiscovery(List<DLockInfo> activeLocks) {
        this.activeLocks = activeLocks;
    }

    public List<DLockInfo> getActiveLocks() {
        return activeLocks;
    }

    public void setActiveLocks(List<DLockInfo> activeLocks) {
        this.activeLocks = activeLocks;
    }
    
}
