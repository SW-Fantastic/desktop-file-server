package org.swdc.rmdisk.service.verticle.http.dto;


import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.swdc.rmdisk.core.SecureUtils;

public class UpdateUserReq {

    public interface GP_UPDATE {}

    public interface GP_CREATE {}

    @Null(groups = {GP_CREATE.class})
    @NotNull(groups = {GP_UPDATE.class})
    private Long id;

    @NotBlank(groups = {GP_UPDATE.class, GP_CREATE.class})
    private Long groupId;

    @NotBlank(groups = {GP_CREATE.class})
    private String username;

    @NotBlank(groups = {GP_CREATE.class})
    private String nickname;

    @NotBlank(groups = {GP_CREATE.class})
    private String password;

    @NotNull(groups = {GP_CREATE.class})
    private Long totalSize;

    @NotBlank(groups = {GP_CREATE.class})
    private String avatarBase64;

    @AssertFalse(groups = {GP_CREATE.class})
    private boolean trash;

    @AssertFalse(groups = {GP_CREATE.class})
    private boolean purge;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isTrash() {
        return trash;
    }

    public void setTrash(boolean trash) {
        this.trash = trash;
    }

    public boolean isPurge() {
        return purge;
    }

    public void setPurge(boolean purge) {
        this.purge = purge;
    }

    public boolean creatable() {
        return SecureUtils.createValidator().validate(
                UpdateGroupReq.class, GP_CREATE.class
        ).isEmpty();
    }

    public boolean updatable() {
        return SecureUtils.createValidator().validate(
                UpdateGroupReq.class, GP_UPDATE.class
        ).isEmpty();
    }
}
