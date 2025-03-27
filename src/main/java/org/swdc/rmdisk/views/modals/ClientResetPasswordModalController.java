package org.swdc.rmdisk.views.modals;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.views.events.ClientLogoutEvent;

import java.util.ResourceBundle;

public class ClientResetPasswordModalController extends ViewController<ClientResetPasswordModal> {

    @FXML
    private PasswordField txtPasswordOld;

    @FXML
    private PasswordField txtPasswordNew;

    @FXML
    private PasswordField txtPasswordNewConfirm;

    @Inject
    private FXResources resources;

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

        ResourceBundle bundle = resources.getResourceBundle();

        String txtPwdOld = txtPasswordOld.getText();
        String txtPwdNew = txtPasswordNew.getText();
        String txtPwdRep = txtPasswordNewConfirm.getText();

        if (txtPwdOld.isBlank()) {
            Alert alert = modal.alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.CLIENT_DLG_PWD_RST_OLD_PWD_EMPTY),
                    Alert.AlertType.ERROR
            );
            alert.showAndWait();
            return;
        }
        if (txtPwdNew.isBlank()) {
            Alert alert = modal.alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.CLIENT_DLG_PWD_RST_NEW_PWD_EMPTY),
                    Alert.AlertType.ERROR
            );
            alert.showAndWait();
            return;
        }

        if (txtPwdNew.equals(txtPwdOld)) {
            Alert alert = modal.alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.CLIENT_DLG_PWD_RST_OLD_NEW_EQ),
                    Alert.AlertType.ERROR
            );
            alert.showAndWait();
            return;
        }

        if (!txtPwdNew.equals(txtPwdRep)) {
            Alert alert = modal.alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.CLIENT_DLG_PWD_RST_PWD_MISMATCH),
                    Alert.AlertType.ERROR
            );
            alert.showAndWait();
            return;
        }

        try {
            RemoteUser user = protocol.resetPassword(token,txtPwdOld,txtPwdNew);
            if (user != null) {

                modal.emit(new ClientLogoutEvent(bundle.getString(LanguageKeys.CLIENT_DLG_PWD_RST_COMPLETE)));
                modal.hide();

            }
        } catch (Exception e) {

            Alert alert = modal.alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.CLIENT_DLG_PWD_RST_FAILED),
                    Alert.AlertType.ERROR
            );
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
