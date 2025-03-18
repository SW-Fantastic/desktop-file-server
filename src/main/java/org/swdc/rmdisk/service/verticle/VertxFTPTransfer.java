package org.swdc.rmdisk.service.verticle;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

public class VertxFTPTransfer {

    private NetServer netServer;

    private NetSocket clientSocket;

    private Vertx vertx;

    public VertxFTPTransfer(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * 主动模式的传输，连接到FTP客户端指定的数据端口。
     * @param addr 客户端地址
     * @param port 客户端数据端口号
     * @return
     */
    public Future<NetSocket> connect(String addr, int port) {

        Promise<NetSocket>  promise = Promise.promise();
        vertx.createNetClient().connect(port, addr, ar -> {
            if (ar.succeeded()) {
                clientSocket = ar.result();
                clientSocket.closeHandler(e -> {
                    // 客户端断开连接，立即关闭临时端口服务。
                    this.clientSocket = null;
                });
                promise.complete(clientSocket);
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();

    }

    /**
     * 被动模式的传输，开启一个临时端口，等待客户端连接。
     * @param port 临时端口号
     * @return
     */
    public Future<NetSocket> listen(int port) {

        Promise<NetSocket> promise = Promise.promise();

        NetServer server = vertx.createNetServer();
        server.connectHandler(socket -> {
            // 处理连接
            if (clientSocket != null) {
                // 已经有客户端连接，拒绝新的连接
                // FTP的临时端口只允许有一个客户端连接。
                socket.close();
                return;
            } else {
                clientSocket = socket;
            }

            socket.closeHandler(e -> {
                // 客户端断开连接，立即关闭临时端口服务。
                this.netServer.close();
                this.clientSocket = null;
                this.netServer = null;
            });

            // 执行端口的数据处理逻辑
            promise.complete(clientSocket);

        });

        server.listen(port).onSuccess(v -> {
            this.netServer = v;
        }).onFailure(promise::fail);

        return promise.future();

    }

}
