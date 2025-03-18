package org.swdc.rmdisk.core;

import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.JsonConfigHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.TextEditor;
import org.swdc.rmdisk.views.cells.PortPropertyEditor;
import org.swdc.rmdisk.views.cells.PortRangePropertyEditor;

@ConfigureSource(value = "assets/server-config.json",handler = JsonConfigHandler.class)
public class ServerConfigure extends AbstractConfig {

    @PropEditor(
            editor = PortPropertyEditor.class,
            name = "WebDAV端口",
            description = "将会在此端口启动WebDAV服务。"
    )
    @Property("server-port")
    private String port;

    @PropEditor(
            editor = PortPropertyEditor.class,
            name = "FTP端口",
            description = "将会在此端口启动FTP服务。"
    )
    @Property("ftp-port")
    private String ftpServerPort;

    @PropEditor(
            editor = TextEditor.class,
            name = "远程地址",
            description = "服务器公开提供服务的IP地址。"
    )
    @Property("server-address")
    private String serverAddress;

    @PropEditor(
            editor = PortRangePropertyEditor.class,
            name = "FTP端口范围",
            description = "此范围的端口将会用于FTP的被动模式。"
    )
    @Property("ftp-port-range")
    private String ftpPortRange;

    public String getFtpServerPort() {
        return ftpServerPort;
    }

    public void setFtpServerPort(String ftpServerPort) {
        this.ftpServerPort = ftpServerPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getFtpPortRange() {
        return ftpPortRange;
    }

    public void setFtpPortRange(String ftpPortRange) {
        this.ftpPortRange = ftpPortRange;
    }
}
