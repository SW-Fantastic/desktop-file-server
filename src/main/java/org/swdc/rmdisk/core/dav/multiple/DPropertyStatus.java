package org.swdc.rmdisk.core.dav.multiple;

import org.swdc.rmdisk.core.xmlns.XmlProperty;

public class DPropertyStatus {

    @XmlProperty("D:status")
    private String status;

    @XmlProperty("D:prop")
    private DProperties properties;

    public DPropertyStatus() {
    }

    public DPropertyStatus(String status, DProperties properties) {
        this.status = status;
        this.properties = properties;
    }

    public DProperties getProperties() {
        return properties;
    }

    public void setProperties(DProperties properties) {
        this.properties = properties;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
