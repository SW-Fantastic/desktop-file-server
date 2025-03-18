package org.swdc.rmdisk.core.dav;

/**
 * 处理基于Web端的Ajax登录请求。
 */
public class BarerLoginRequest {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "BarerLoginRequest{" + "username=" + username + ", password=" + password + '}';
    }

}
