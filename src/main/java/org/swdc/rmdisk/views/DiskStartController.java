package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.fxml.FXML;
import org.swdc.fx.view.ViewController;

@Singleton
public class DiskStartController extends ViewController<DiskStartView> {

    @Inject
    private DiskServerView diskServerView;

    @Inject
    private ClientLoginView clientLoginView;

    @FXML
    public void showServerView() {
        getView().hide();
        diskServerView.show();
    }

    @FXML
    public void showLoginView() {
        getView().hide();
        clientLoginView.show();
    }

}
