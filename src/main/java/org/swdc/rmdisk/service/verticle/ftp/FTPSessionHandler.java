package org.swdc.rmdisk.service.verticle.ftp;

import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.ftp.*;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.FTPControl;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.verticle.VertxFTPAbstractHandler;
import org.swdc.rmdisk.service.verticle.VertxFTPTransfer;

public class FTPSessionHandler extends VertxFTPAbstractHandler {

    @Inject
    private SecureService secureService;

    @Inject
    private DiskFileService diskFileService;

    @Inject
    private ServerConfigure serverConfigure;


    @FTPControl("OPTS")
    public void options(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        FTPMsg reply = new FTPMsg(FTPRespCode.COMMAND_OK, "Command executed successfully");
        sender.write(reply.prepare());

    }

    /**
     * NOOP指令，用于保持连接活跃。
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("NOOP")
    public void noop(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for NOOP command
        FTPMsg reply = new FTPMsg(FTPRespCode.COMMAND_OK, "Command executed successfully");
        sender.write(reply.prepare());

    }

    /**
     * ABOR指令，用于中断当前的数据传输。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("ABOR")
    public void abort(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }
        session.clearTransferState();

        Future<NetSocket> dataConnFuture = session.getDataConnection();
        if (dataConnFuture.succeeded()) {
            NetSocket dataConn = dataConnFuture.result();
            dataConn.close();
        }

        FTPMsg reply = new FTPMsg(FTPRespCode.COMMAND_OK, "Command executed successfully");
        sender.write(reply.prepare());

    }

    /**
     * SYST指令，用于获取服务器系统类型。
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("SYST")
    public void systemType(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for SYST command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        String systemType = "UNIX Type: L8";
        FTPMsg resp = new FTPMsg(FTPRespCode.SYSTEM_STATUS, systemType);
        sender.write(resp.prepare());

    }


    /**
     * PASV指令，用于进入被动模式，此指令将会在服务器监听一个临时端口，
     * 用于传输数据。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("PASV")
    public void enterPassiveMode(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for PASV command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (session.getDataConnection() != null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_BUSY, "Cannot open data connection.");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        int maxPort = 65535;
        int minPort = 20000;
        String portRange = serverConfigure.getFtpPortRange();
        if (!portRange.isBlank() && portRange.contains("-")) {

            String[] portRangeArr = portRange.split("-");
            int portA = Integer.parseInt(portRangeArr[0]);
            int portB = Integer.parseInt(portRangeArr[1]);
            maxPort = Integer.max(portA,portB);
            minPort = Integer.min(portA, portB);

        }

        int port = minPort + (int) (Math.random() * ((maxPort - minPort) + 1));
        VertxFTPTransfer transfer = new VertxFTPTransfer(context.getThirdPartyContext());
        session.setDataConnection(transfer.listen(port));

        int pasvPort = port / 256;
        int pasvPortHigh = port % 256;

        String hostAddr = sender.localAddress().hostAddress().replaceAll("[.]", ",");
        if (serverConfigure.getServerAddress() != null) {
            hostAddr = serverConfigure.getServerAddress().replaceAll("[.]", ",");
        }
        FTPMsg resp = new FTPMsg(FTPRespCode.ENTER_PASSIVE_MODE, "Entering Passive Mode (" + hostAddr + "," + pasvPort + "," + pasvPortHigh + ")");
        sender.write(resp.prepare());

    }


    /**
     * EPSV指令，用于进入扩展被动模式。
     * 此指令将会在服务器监听一个临时端口，用于传输数据。
     * 拓展被动模式相比于PASV指令，可以支持IPv6地址。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("EPSV")
    public void enterExtendedPassiveMode(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for EPSV command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() != null && !msg.getArgs().isBlank()) {

            String arg = msg.getArgs();

            if (arg.toUpperCase().equals("ALL")) {
                session.setTransferMode(FTPTransferMode.EPSV);
                FTPMsg resp = new FTPMsg(FTPRespCode.COMMAND_OK, "EPSV ALL command executed.");
                sender.write(resp.prepare());
                return;
            } else if (arg.equals("1")) {
                session.setTransferMode(FTPTransferMode.PASV);
            } else if (arg.equals("2")) {
                session.setTransferMode(FTPTransferMode.EPSV);
            }

        }

        if (session.getDataConnection() != null) {
            FTPMsg resp = new FTPMsg(FTPRespCode.FILE_BUSY, "Cannot open data connection.");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        int maxPort = 65535;
        int minPort = 20000;
        String portRange = serverConfigure.getFtpPortRange();
        if (!portRange.isBlank() && portRange.contains("-")) {

            String[] portRangeArr = portRange.split("-");
            int portA = Integer.parseInt(portRangeArr[0]);
            int portB = Integer.parseInt(portRangeArr[1]);
            maxPort = Integer.max(portA,portB);
            minPort = Integer.min(portA, portB);

        }

        int port = minPort + (int) (Math.random() * ((maxPort - minPort) + 1));

        VertxFTPTransfer transfer = new VertxFTPTransfer(context.getThirdPartyContext());
        session.setDataConnection(transfer.listen(port));

        FTPMsg resp = new FTPMsg(FTPRespCode.ENTER_EPSV_MODE, "Entering Extended Passive Mode (|||" + port + "|)");
        sender.write(resp.prepare());

    }


    /**
     * 设置传输类型，可以是ASCII、二进制或其他。
     * 本服务器默认二进制模式，其他暂不支持。
     *
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("TYPE")
    public void setTransferType(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for TYPE command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: TYPE <type-code>");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        String type = msg.getArgs().toUpperCase();
        if (type.equals("A")) {
            session.setTransferType(FTPTransferType.ASCII);
        } else if (type.equals("I") || type.equals("B")) {
            session.setTransferType(FTPTransferType.BINARY);
        } else if (type.equals("E")) {
            session.setTransferType(FTPTransferType.EBCDIC);
        } else if (type.startsWith("L")) {
            session.setTransferType(FTPTransferType.LOCAL);
        } else {
            FTPMsg resp = new FTPMsg(FTPRespCode.NOT_RECOGNIZED, "Syntax error: TYPE <type-code>");
            sender.write(resp.prepare());
            return;
        }

        FTPMsg resp = new FTPMsg(FTPRespCode.COMMAND_OK, "Transfer type set to " + type);
        sender.write(resp.prepare());

    }

    /**
     * PORT指令，用于建立数据连接。
     * 客户端通过PORT指令告诉服务器其数据连接端口，
     * @param context
     * @param sender
     * @param msg
     */
    @FTPControl("PORT")
    public void connectWithClient(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for PORT command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: PORT <h1>,<h2>,<h3>,<h4>,<p1>,<p2>");
            sender.write(resp.prepare());
            return;
        }

        String[] args = msg.getArgs().split(",");
        if (args.length != 6) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: PORT <h1>,<h2>,<h3>,<h4>,<p1>,<p2>");
            sender.write(resp.prepare());
            return;
        }

        session.clearTransferState();

        int port = Integer.parseInt(args[4]) * 256 + Integer.parseInt(args[5]);
        String host = args[0] + "." + args[1] + "." + args[2] + "." + args[3];

        VertxFTPTransfer transfer = new VertxFTPTransfer(context.getThirdPartyContext());
        transfer.connect(host, port);
        session.setDataConnection(transfer);

        FTPMsg resp = new FTPMsg(FTPRespCode.COMMAND_OK, "PORT command executed.");
        sender.write(resp.prepare());

    }

    @FTPControl("EPRT")
    public void connectWithClientEPRT(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {

        // Implementation for EPRT command
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user == null) {
            // user not authenticated
            FTPMsg resp = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "Login with USER and PASS first.");
            sender.write(resp.prepare());
            return;
        }
        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: EPRT |<addr_type>|<ip>|<port>|");
            sender.write(resp.prepare());
            return;
        }
        session.clearTransferState();

        String[] args = msg.getArgs().split("[|]");
        if (args.length != 4) {
            FTPMsg resp = new FTPMsg(FTPRespCode.SYNTAX_ERROR, "Syntax error: EPRT |<addr_type>|<ip>|<port>|");
            sender.write(resp.prepare());
            return;
        }
        //String ipVersion = args[1];
        String ip = args[2];
        String port = args[3];
        VertxFTPTransfer transfer = new VertxFTPTransfer(context.getThirdPartyContext());
        Future<NetSocket> conn = transfer.connect(ip, Integer.parseInt(port));
        session.setDataConnection(conn);

        FTPMsg resp = new FTPMsg(FTPRespCode.COMMAND_OK, "EPRT command executed.");
        sender.write(resp.prepare());

    }

}
