package org.swdc.rmdisk.core.ftp;

public enum FTPTransferType {

    /**
     * 以ASCII模式传输数据。
     */
    ASCII,
    /**
     * 以二进制模式传输数据。
     */
    BINARY,
    /**
     * 以EBCDIC模式传输数据。
     */
    EBCDIC,
    /**
     * 以本地模式传输数据。
     */
    LOCAL;

}
