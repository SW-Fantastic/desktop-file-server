package org.swdc.rmdisk.core.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DiskFolder entity.
 */
@Entity
@Table(name = "disk_folder")
public class DiskFolder extends DiskResource {

    @OneToMany(mappedBy = "parent",cascade = CascadeType.REMOVE)
    private List<DiskFolder> children;

    @OneToMany(
            targetEntity = DiskFile.class,
            mappedBy = "parent",
            cascade = CascadeType.REMOVE
    )
    private List<DiskFile> files;

    public List<DiskFile> getFiles() {
        return files;
    }

    public void setFiles(List<DiskFile> files) {
        this.files = files;
    }

    public List<DiskFolder> getChildren() {
        return children;
    }

    public void setChildren(List<DiskFolder> children) {
        this.children = children;
    }


}
