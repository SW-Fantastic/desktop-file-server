package org.swdc.rmdisk.core.ftp;

public enum FTPTransferMode {

    /**
     * 服务器将会连接至客户端指定的端口，打开数据连接。
     */
    PORT,

    /**
     * 服务器将会打开一个端口，并通知客户端连接至该端口。
     */
    PASV,

    /**
     * 服务器将会打开一个端口，并通知客户端连接至该端口，
     * 该端口将会兼容IPv4和IPv6。
     */
    EPSV;

}
