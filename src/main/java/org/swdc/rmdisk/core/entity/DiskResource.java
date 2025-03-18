package org.swdc.rmdisk.core.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Base class for all disk resources.
 *
 * there is two types of disk resources:
 * - DiskFolder
 * - DiskFile
 *
 * Each disk resource has a parent folder and an owner.
 */
@MappedSuperclass
public class DiskResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(columnDefinition = "parent_id")
    @ManyToOne
    private DiskFolder parent;

    @JoinColumn(
            columnDefinition = "owner_id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    @ManyToOne
    private User owner;

    private String name;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public User getOwner() {
        return owner;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setParent(DiskFolder parent) {
        this.parent = parent;
    }

    public DiskFolder getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
