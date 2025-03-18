package org.swdc.rmdisk.core.ftp;

public class FTPMsg {

    private FTPRespCode code;

    protected String message;

    public FTPMsg(FTPRespCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code.getCode();
    }

    public String getMessage() {
        return message;
    }

    public String prepare() {
        return String.format("%s %s\n", getCode(), message);
    }

}
