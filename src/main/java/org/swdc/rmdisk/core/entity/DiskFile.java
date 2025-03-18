package org.swdc.rmdisk.core.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Disk file entity.
 */
@Entity
@Table(name = "disk_file")
public class DiskFile extends DiskResource {

    private String uuid;

    private String mimeType;

    private Long fileSize;

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


}
