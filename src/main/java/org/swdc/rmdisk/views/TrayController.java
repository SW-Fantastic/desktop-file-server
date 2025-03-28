package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.swdc.fx.view.ViewController;

public class TrayController extends ViewController<TrayView> {

    @Inject
    private ClientLoginView loginView;

    @Inject
    private DiskServerView serverView;

    @Inject
    private DiskStartView startView;

    @FXML
    public void onExit() {
        Platform.exit();
    }

    @FXML
    public void onAbout() {

    }

    @FXML
    public void onServerView() {
        getView().hide();
        if (startView.getStage().isShowing()) {
            startView.hide();
        }
        serverView.show();
    }

    @FXML
    public void onDiskView() {
        getView().hide();
        if (startView.getStage().isShowing()) {
            startView.hide();
        }
        loginView.show();
    }

}
