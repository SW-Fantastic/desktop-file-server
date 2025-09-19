package org.swdc.rmdisk.core.entity;

import org.swdc.data.anno.StatelessIgnore;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "usergroup")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    @OneToOne
    @JoinColumn(name = "template_root_id")
    private TemplateFolder templateRoot;

    @OneToMany(mappedBy = "group",cascade = CascadeType.REMOVE)
    private Set<User> users;

    @Enumerated(EnumType.STRING)
    private State state;

    private boolean registrable;

    public boolean isRegistrable() {
        return registrable;
    }

    public void setRegistrable(boolean registrable) {
        this.registrable = registrable;
    }

    public TemplateFolder getTemplateRoot() {
        return templateRoot;
    }

    public void setTemplateRoot(TemplateFolder templateRoot) {
        this.templateRoot = templateRoot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
