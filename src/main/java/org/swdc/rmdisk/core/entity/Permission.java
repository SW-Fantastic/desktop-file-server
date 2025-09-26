package org.swdc.rmdisk.core.entity;

import org.swdc.rmdisk.core.LanguageKeys;

public enum Permission {

    SUPER_ADMIN(LanguageKeys.PERMISSION_SUPER_ADMIN),
    USER(LanguageKeys.PERMISSION_USER);

    private String key;
    Permission(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
