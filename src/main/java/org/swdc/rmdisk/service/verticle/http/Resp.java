package org.swdc.rmdisk.service.verticle.http;

import org.swdc.rmdisk.core.SecureUtils;

public class Resp {

    private int code;

    private Object data;

    public Resp(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public Resp() {

    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public <T> T getData(Class<T> clazz) {
        return SecureUtils.jsonObjectAsType(clazz, data);
    }
}
