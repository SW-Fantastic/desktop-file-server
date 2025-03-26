package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.RmDiskApplicationConfig;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.views.events.ConfigureUpdateEvent;

import java.util.ResourceBundle;

public class ServerConfigureViewController extends ViewController<ServerConfigureView> {

    @Inject
    private ServerConfigure serverConfigure;

    @Inject
    private RmDiskApplicationConfig config;

    @Inject
    private FXResources resources;

    @FXML
    public void saveConfiguration() {

        ServerConfigureView view = getView();

        ResourceBundle bundle = resources.getResourceBundle();

        Alert alert = view.alert(
                bundle.getString(LanguageKeys.WARN),
                bundle.getString(LanguageKeys.SERVER_CONFIG_DLG_SAVE_CONFIG),
                Alert.AlertType.CONFIRMATION
        );
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                try {
                    config.save();
                    serverConfigure.save();
                    view.hide();
                    view.emit(new ConfigureUpdateEvent());
                } catch (Exception e) {
                    Alert alertModal = view.alert(
                            bundle.getString(LanguageKeys.ERROR),
                            bundle.getString(LanguageKeys.SERVER_CONFIG_DLG_SAVE_FAIL),
                            Alert.AlertType.ERROR
                    );
                    alertModal.showAndWait();
                }
            }
        });


    }

}
