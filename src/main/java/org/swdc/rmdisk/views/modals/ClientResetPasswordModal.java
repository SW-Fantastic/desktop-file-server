package org.swdc.rmdisk.views.modals;

import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;

@View(viewLocation = "views/modals/ClientResetPasswordModal.fxml",title = "修改密码",dialog = true)
public class ClientResetPasswordModal extends AbstractView {

    @Override
    public void show() {
        throw new RuntimeException("use show(ClientFileProtocol protocol, String token) method instead");
    }

    public void show(ClientFileProtocol protocol, String token) {
        ClientResetPasswordModalController controller = getController();
        if (controller.update(protocol, token)) {
            super.show();
        }
    }

}
