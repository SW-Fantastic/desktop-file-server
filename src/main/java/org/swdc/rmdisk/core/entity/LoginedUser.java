package org.swdc.rmdisk.core.entity;

public class LoginedUser {

    private Long id;

    private String userName;

    public LoginedUser(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

}
