package org.swdc.rmdisk.core.ftp;

public class FTPSession {

    private Long userId;

    /**
     * 当前Session的用户名
     */
    private String userName;

    /**
     * 当前Session的传输模式，默认为PORT
     */
    private FTPTransferMode transferMode = FTPTransferMode.PORT;

    /**
     * 当前Session的传输类型，默认为二进制
     */
    private FTPTransferType transferType = FTPTransferType.BINARY;

    /**
     * 当前Session的当前目录，默认为根目录
     */
    private volatile String currentDirectory;

    /**
     * 当前Session的会话标识，默认为空字符串
     * 登录后是一个JWT格式的Token
     */
    private volatile  String sessionToken;

    /**
     * 当前Session的改名资源，默认为空字符串
     */
    private volatile String renameResource;

    /**
     * 当前Session的数据连接，默认为空对象
     */
    private volatile Object dataConnection;

    /**
     * 当前Session的数据偏移量，默认为0L，用于断点续传，
     * 在传输文件时，如果设置了偏移量，则会从指定的位置开始传输数据。
     */
    private volatile long transferOffset;

    public <T> T getDataConnection() {
        T result = (T)dataConnection;
        dataConnection = null;
        return result;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDataConnection(Object dataConnection) {
        this.dataConnection = dataConnection;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public FTPTransferType getTransferType() {
        return transferType;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public String getUserName() {
        return userName;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }


    public void setTransferType(FTPTransferType transferType) {
        this.transferType = transferType;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public FTPTransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(FTPTransferMode transferMode) {
        this.transferMode = transferMode;
    }

    public String getRenameResource() {
        return renameResource;
    }

    public void setRenameResource(String renameResource) {
        this.renameResource = renameResource;
    }

    public long getTransferOffset() {
        return transferOffset;
    }

    public void setTransferOffset(long transferOffset) {
        this.transferOffset = transferOffset;
    }

    public void clearTransferState() {
        this.renameResource = null;
        this.transferOffset = 0L;
    }

}

