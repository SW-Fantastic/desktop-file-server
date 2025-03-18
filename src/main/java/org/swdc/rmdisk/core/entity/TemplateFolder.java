package org.swdc.rmdisk.core.entity;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "template_folder")
public class TemplateFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(columnDefinition = "parent_id")
    private TemplateFolder parent;

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "parent")
    private List<TemplateFolder> children;

    @OneToOne(cascade = CascadeType.DETACH)
    private UserGroup group;

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public List<TemplateFolder> getChildren() {
        return children;
    }

    public TemplateFolder getParent() {
        return parent;
    }

    public void setParent(TemplateFolder parent) {
        this.parent = parent;
    }

    public void setChildren(List<TemplateFolder> children) {
        this.children = children;
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
