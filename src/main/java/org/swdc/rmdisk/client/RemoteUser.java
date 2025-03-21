package org.swdc.rmdisk.client;

public class RemoteUser {

    private String username;

    private String avatar;

    private String nickname;

    private Long usedSize;

    private Long totalSize;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public Long getUsedSize() {
        return usedSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public void setUsedSize(Long usedSize) {
        this.usedSize = usedSize;
    }
}
