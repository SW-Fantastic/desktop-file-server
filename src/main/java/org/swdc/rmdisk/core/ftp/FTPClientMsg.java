package org.swdc.rmdisk.core.ftp;

public class FTPClientMsg {

    private String action;

    private String args;

    public FTPClientMsg(String action, String args) {
        this.action = action;
        this.args = args;
    }

    public String getArgs() {
        return args;
    }

    public String getAction() {
        return action;
    }
}
