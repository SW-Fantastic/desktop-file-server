package org.swdc.rmdisk.service.verticle.http;

import org.swdc.rmdisk.core.SecureUtils;

public record Resp(int code, Object data) {

    public <T> T data(Class<T> clazz) {
        return SecureUtils.jsonObjectAsType(clazz, data);
    }

}
