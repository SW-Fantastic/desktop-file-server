package org.swdc.rmdisk.views;

import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.core.LanguageKeys;

@View(
        viewLocation = "views/main/ClientLoginView.fxml",
        title = LanguageKeys.UI_CLIENT_LOGIN_TITLE,
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
