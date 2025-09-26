package org.swdc.rmdisk.service.verticle.http.dto;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.swdc.rmdisk.core.SecureUtils;

public class UpdateGroupReq {

    public interface GP_UPDATE {}
    public interface GP_CREATE {}

    @Null(groups = {GP_CREATE.class})
    @NotNull(groups = {GP_UPDATE.class})
    private Long groupId;

    @NotBlank(groups = {GP_CREATE.class, GP_UPDATE.class})
    private String name;

    @NotNull(groups = {GP_CREATE.class, GP_UPDATE.class})
    private Boolean allowRegister;

    @AssertFalse(groups = {GP_CREATE.class})
    private boolean trash;

    public Boolean getAllowRegister() {
        return allowRegister;
    }

    public void setAllowRegister(Boolean allowRegister) {
        this.allowRegister = allowRegister;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isTrash() {
        return trash;
    }

    public void setTrash(boolean trash) {
        this.trash = trash;
    }

    public boolean creatable() {
        return SecureUtils.createValidator().validate(
                this, GP_CREATE.class
        ).isEmpty();
    }

    public boolean updatable() {
        return SecureUtils.createValidator().validate(
                this, GP_UPDATE.class
        ).isEmpty();
    }

}
