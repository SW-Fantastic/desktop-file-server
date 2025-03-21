package org.swdc.rmdisk.views.modals;

import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;

@View(
        viewLocation = "views/modals/ClientUserProfileModal.fxml",
        title = "用户信息",
        dialog = true,
        resizeable = false
)
public class ClientUserProfileModal extends AbstractView {

    @Override
    public void show() {
        throw new RuntimeException("Use show(RemoteUser user) instead.");
    }

    public void show(ClientFileProtocol protocol, String token) {
        if(protocol == null || token == null || token.isEmpty()) {
            return;
        }
        ClientUserProfileModalController controller = getController();
        if(controller.update(protocol, token)) {
            super.show();
        }
    }

}
