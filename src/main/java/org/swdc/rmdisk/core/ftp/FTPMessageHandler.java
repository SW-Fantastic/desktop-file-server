package org.swdc.rmdisk.core.ftp;

import io.vertx.core.net.NetSocket;

public interface FTPMessageHandler {

    void handle(NetSocket sender, FTPClientMsg msg);

}
