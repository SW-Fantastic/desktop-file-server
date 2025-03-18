package org.swdc.rmdisk.core;

import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.JsonConfigHandler;

@ConfigureSource(value = "assets/system.json",handler = JsonConfigHandler.class)
public class ManagedServerConfigure extends AbstractConfig {

    @Property("server-enable-register")
    private Boolean enableRegister;

    @Property("server-default-space")
    private Integer defaultSpaceSize;

    @Property("server-auto-register")
    private Boolean autoRegisterUser;

    public Boolean getAutoRegisterUser() {
        return autoRegisterUser;
    }

    public void setAutoRegisterUser(Boolean autoRegisterUser) {
        this.autoRegisterUser = autoRegisterUser;
    }

    public Boolean getEnableRegister() {
        return enableRegister;
    }

    public void setDefaultSpaceSize(Integer defaultSpaceSize) {
        this.defaultSpaceSize = defaultSpaceSize;
    }

    public Integer getDefaultSpaceSize() {
        return defaultSpaceSize;
    }

    public void setEnableRegister(Boolean enableRegister) {
        this.enableRegister = enableRegister;
    }

}
