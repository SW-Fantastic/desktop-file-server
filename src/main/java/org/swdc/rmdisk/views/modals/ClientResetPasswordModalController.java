package org.swdc.rmdisk.views.modals;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.views.ClientMainController;
import org.swdc.rmdisk.views.ClientMainView;
import org.swdc.rmdisk.views.events.ClientLogoutEvent;

public class ClientResetPasswordModalController extends ViewController<ClientResetPasswordModal> {

    @FXML
    private PasswordField txtPasswordOld;

    @FXML
    private PasswordField txtPasswordNew;

    @FXML
    private PasswordField txtPasswordNewConfirm;

    private ClientFileProtocol protocol = null;

    private String token = null;

    public boolean update(ClientFileProtocol protocol, String token) {

        if (protocol == null || token == null || token.isEmpty()) {
            return false;
        }

        this.protocol = protocol;
        this.token = token;

        clearFields();

        return true;
    }

    private void clearFields() {
        txtPasswordNew.setText("");
        txtPasswordNewConfirm.setText("");
        txtPasswordOld.setText("");
    }

    @FXML
    public void onResetPassword() {

        var modal = getView();

        String txtPwdOld = txtPasswordOld.getText();
        String txtPwdNew = txtPasswordNew.getText();
        String txtPwdRep = txtPasswordNewConfirm.getText();

        if (txtPwdOld.isBlank()) {
            Alert alert = modal.alert("提示", "旧密码不能为空！", Alert.AlertType.ERROR);
            alert.showAndWait();
            return;
        }
        if (txtPwdNew.isBlank()) {
            Alert alert = modal.alert("提示","新密码不能为空！", Alert.AlertType.ERROR);
            alert.showAndWait();
            return;
        }

        if (txtPwdNew.equals(txtPwdOld)) {
            Alert alert = modal.alert("提示","新密码不应与旧密码一致", Alert.AlertType.ERROR);
            alert.showAndWait();
            return;
        }

        if (!txtPwdNew.equals(txtPwdRep)) {
            Alert alert = modal.alert("提示","您输入的新密码和重复密码不一致。", Alert.AlertType.ERROR);
            alert.showAndWait();
            return;
        }

        try {
            RemoteUser user = protocol.resetPassword(token,txtPwdOld,txtPwdNew);
            if (user != null) {

                modal.emit(new ClientLogoutEvent("密码已经重置，你需要重新登录。"));
                modal.hide();

            }
        } catch (Exception e) {

            Alert alert = modal.alert("提示","未知错误，修改失败", Alert.AlertType.ERROR);
            alert.showAndWait();

        }

    }

    @FXML
    public void onCancel() {

        ClientResetPasswordModal modal = getView();
        clearFields();
        modal.hide();

    }

}
