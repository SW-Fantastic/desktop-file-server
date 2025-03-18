package org.swdc.rmdisk.core.dav.multiple;


import org.swdc.rmdisk.core.xmlns.XmlProperty;

@XmlProperty("D:response")
public class DResponse {

    @XmlProperty("D:href")
    private String href;

    @XmlProperty("D:propstat")
    private DPropertyStatus propertyStatus;

    public DResponse() {
    }

    public DResponse(String href, DPropertyStatus propertyStatus) {
        this.href = href;
        this.propertyStatus = propertyStatus;
    }

    public DPropertyStatus getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(DPropertyStatus propertyStatus) {
        this.propertyStatus = propertyStatus;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
