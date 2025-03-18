package org.swdc.rmdisk.views;

import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(
        viewLocation = "views/main/ClientLoginView.fxml",
        title = "登录到繁星云",
        resizeable = false
)
public class ClientLoginView extends AbstractView {

    @Override
    public void show() {
        ClientMainView mainView = getView(ClientMainView.class);
        if (mainView.isLoggedIn()) {
            mainView.show();
        } else {
            super.show();
        }
    }

}
