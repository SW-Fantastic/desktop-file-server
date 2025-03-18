package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.RmDiskApplicationConfig;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.views.events.ConfigureUpdateEvent;

public class ServerConfigureViewController extends ViewController<ServerConfigureView> {

    @Inject
    private ServerConfigure serverConfigure;

    @Inject
    private RmDiskApplicationConfig config;

    @FXML
    public void saveConfiguration() {

        ServerConfigureView view = getView();

        Alert alert = view.alert("提示","如果网络服务正在运行，修改配置将会停止它们，你确定要继续吗？", Alert.AlertType.CONFIRMATION);
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                try {
                    config.save();
                    serverConfigure.save();
                    view.hide();
                    view.emit(new ConfigureUpdateEvent());
                } catch (Exception e) {
                    Alert alertModal = view.alert("失败", "保存配置失败", Alert.AlertType.ERROR);
                    alertModal.showAndWait();
                }
            }
        });


    }

}
