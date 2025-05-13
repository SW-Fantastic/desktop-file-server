package org.swdc.rmdisk.core;

import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.JsonConfigHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.CheckEditor;
import org.swdc.fx.config.editors.TextEditor;
import org.swdc.rmdisk.views.cells.PortPropertyEditor;
import org.swdc.rmdisk.views.cells.PortRangePropertyEditor;

@ConfigureSource(value = "assets/server-config.json",handler = JsonConfigHandler.class)
public class ServerConfigure extends AbstractConfig {

    @PropEditor(
            editor = PortPropertyEditor.class,
            name = LanguageKeys.UI_SERVER_CONFIG_DAV_SERVER_PORT,
            description = LanguageKeys.UI_SERVER_CONFIG_DAV_SERVER_DESC
    )
    @Property("server-port")
    private String port;

    @PropEditor(
            editor = PortPropertyEditor.class,
            name = LanguageKeys.UI_SERVER_CONFIG_FTP_SERVER_PORT,
            description = LanguageKeys.UI_SERVER_CONFIG_FTP_SERVER_DESC
    )
    @Property("ftp-port")
    private String ftpServerPort;

    @PropEditor(
            editor = TextEditor.class,
            name = LanguageKeys.UI_SERVER_CONFIG_IP,
            description = LanguageKeys.UI_SERVER_CONFIG_IP_DESC
    )
    @Property("server-address")
    private String serverAddress;

    @PropEditor(
            editor = PortRangePropertyEditor.class,
            name = LanguageKeys.UI_SERVER_CONFIG_FTP_RANGE_PORT,
            description = LanguageKeys.UI_SERVER_CONFIG_FTP_RANGE_DESC
    )
    @Property("ftp-port-range")
    private String ftpPortRange;

    @PropEditor(
            editor = CheckEditor.class,
            name = LanguageKeys.UI_SERVER_CONFIG_REC_LOGS,
            description = LanguageKeys.UI_SERVER_CONFIG_REC_LOGS_DESC
    )
    @Property("record-server-logs")
    private Boolean recordLogs;

    public Boolean getRecordLogs() {
        return recordLogs;
    }

    public void setRecordLogs(Boolean recordLogs) {
        this.recordLogs = recordLogs;
    }

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
