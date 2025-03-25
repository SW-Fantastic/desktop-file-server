package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.service.FTPVertxService;
import org.swdc.rmdisk.service.HttpVertxService;
import org.swdc.rmdisk.views.events.ConfigureUpdateEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 这里的功能主要是用来控制云服务器的启动和停止，以及提供一个各个功能的Tab视图。
 * 各个功能模块处于Tab中，通过Tab来切换和使用不同的功能。
 */
@Singleton
public class DiskServerController extends ViewController<DiskServerView> {

    /**
     * 端口号
     * 用于 http 服务器启动的端口号，值由配置文件指定
     */
    @FXML
    private TextField portField;

    /**
     * FTP端口号，用于FTP服务器启动的端口号，值由配置文件指定。
     */
    @FXML
    private TextField portFTPField;

    /**
     * Tab容器，用于存放各个功能模块的Tab视图。
     */
    @FXML
    private TabPane tabContainer;

    /**
     * 用户管理Tab视图
     */
    @Inject
    private ServerUserTabView serverUserTabView;

    /**
     * 注册管理Tab视图
     */
    @Inject
    private ServerRegisterTabView serverRegisterTabView;

    /**
     * 服务器的设置对象
     */
    @Inject
    private ServerConfigure config;

    @Inject
    private ServerConfigureView serverConfigureView;

    /**
     * WebDAV服务的HTTP服务对象，用于启动和停止WebDAV服务。
     */
    @Inject
    private HttpVertxService httpVertxService;

    /**
     * FTP服务的FTP服务对象，用于启动和停止FTP服务。
     */
    @Inject
    private FTPVertxService ftpVertxService;

    /**
     * 客户端登录视图，用于显示和操作客户端的登录界面。
     */
    @Inject
    private ClientLoginView clientLoginView;

    @Inject
    private FXResources resources;

    /**
     * 当视图准备就绪时调用此方法，本方法用于初始化视图。
     *
     * @param url 资源文件的URL
     * @param resourceBundle 资源包
     */
    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        portField.setText(config.getPort().toString());
        portFTPField.setText(config.getFtpServerPort().toString());

        ObservableList<Tab> tabs = tabContainer.getTabs();
        tabs.add(new Tab(resourceBundle.getString(LanguageKeys.SERVER_USER_MGR), serverUserTabView.getView()));
        tabs.add(new Tab(resourceBundle.getString(LanguageKeys.SERVER_USER_REG), serverRegisterTabView.getView()));
    }

    @EventListener(type = ConfigureUpdateEvent.class)
    public void updatePorts(ConfigureUpdateEvent event) {

        if (httpVertxService.isStarted()) {
            this.serverDAVStartStop();
        }

        if (ftpVertxService.isStarted()) {
            this.serverFTPStartStop();
        }

        Platform.runLater(() -> {
            portField.setText(config.getPort().toString());
            portFTPField.setText(config.getFtpServerPort().toString());
        });

    }

    @FXML
    public void onLoginRequest() {
        clientLoginView.show();
    }


    @FXML
    public void serverFTPStartStop() {

        ResourceBundle bundle = resources.getResourceBundle();

        try {
            Integer port = Integer.parseInt(portFTPField.getText());
            config.setFtpServerPort(port.toString());
            config.save();
        } catch (Exception e) {
            portFTPField.setText(config.getFtpServerPort().toString());
        }

        DiskServerView serverView = getView();
        if (ftpVertxService.isStarted()) {
            ftpVertxService.stopService(result -> {
                if (!result){
                    Alert alert = serverView.alert(
                            bundle.getString(LanguageKeys.ERROR),
                            bundle.getString(LanguageKeys.ERR_STOP_FTP_SERVER),
                            Alert.AlertType.ERROR
                    );
                    alert.showAndWait();
                } else {
                    serverView.switchFTPServerState(false);
                    portFTPField.setDisable(false);
                }
            });
        } else {
            ftpVertxService.startService(result -> {
                if (!result) {
                    Alert alert = getView().alert(
                            bundle.getString(LanguageKeys.ERROR),
                            bundle.getString(LanguageKeys.ERR_START_FTP_SERVER),
                            Alert.AlertType.ERROR
                    );
                    alert.showAndWait();
                    portFTPField.setDisable(false);
                } else {
                    serverView.switchFTPServerState(true);
                    portFTPField.setDisable(true);
                }
            });
        }
    }

    /**
     * 启动或停止WebDAV服务
     */
    @FXML
    public void serverDAVStartStop(){

        ResourceBundle bundle = resources.getResourceBundle();

        try {
            Integer port = Integer.parseInt(portField.getText());
            config.setPort(port.toString());
            config.save();
        } catch (Exception e) {
            portField.setText(config.getPort().toString());
        }

        DiskServerView serverView = getView();
        if (httpVertxService.isStarted()) {
            httpVertxService.stopService(result -> {
                if (!result){
                    Alert alert = serverView.alert(
                            bundle.getString(LanguageKeys.ERROR),
                            bundle.getString(LanguageKeys.ERR_STOP_HTTP_SERVER),
                            Alert.AlertType.ERROR
                    );
                    alert.showAndWait();
                } else {
                    serverView.switchServerState(false);
                    portField.setDisable(false);
                }
            });
        } else {
            httpVertxService.startService(result -> {
                if (!result){
                    Alert alert = getView().alert(
                            bundle.getString(LanguageKeys.ERROR),
                            bundle.getString(LanguageKeys.ERR_START_HTTP_SERVER),
                            Alert.AlertType.ERROR
                    );
                    alert.showAndWait();
                    portField.setDisable(false);
                } else {
                    serverView.switchServerState(true);
                    portField.setDisable(true);
                }
            });
        }

    }

    @FXML
    public void showServerConfig() {
        serverConfigureView.show();
    }

}
