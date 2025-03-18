package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.client.ClientDescriptor;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Singleton
public class ClientLoginController extends ViewController<ClientLoginView> {

    @Inject
    private List<ClientDescriptor> descriptors;

    @Inject
    private Logger logger;

    @FXML
    private ComboBox<ClientDescriptor> cbxProtocol;

    @FXML
    private TextField txtHost;

    @FXML
    private TextField txtUserName;

    @FXML
    private PasswordField txtPassword;


    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        cbxProtocol.getItems().addAll(descriptors);
        cbxProtocol.getSelectionModel().select(0);

    }

    @FXML
    private void register() {
        if (txtHost.getText().isEmpty()) {
            return;
        }
        ClientDescriptor descriptor = cbxProtocol.getSelectionModel().getSelectedItem();
        ClientFileProtocol protocol = descriptor.createClient(txtHost.getText());
        if(!protocol.registerable()) {
            Alert alert = getView().alert("提示", "不支持注册操作", Alert.AlertType.WARNING);
            alert.showAndWait();
            return;
        }
    }

    @FXML
    private void login() {
        if (txtHost.getText().isEmpty() || txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            return;
        }
        ClientDescriptor descriptor = cbxProtocol.getSelectionModel().getSelectedItem();
        ClientFileProtocol protocol = descriptor.createClient(txtHost.getText());
        String token = protocol.login(txtUserName.getText(), txtPassword.getText());
        if (token == null || token.isEmpty()) {
            Alert alert = getView().alert("提示", "登录失败，请检查用户名和密码是否正确！", Alert.AlertType.ERROR);
            alert.showAndWait();
            return;
        }
        ClientLoginView loginView = getView();
        ClientMainView mainView = loginView.getView(ClientMainView.class);
        mainView.show(token, protocol);

        loginView.hide();
    }

}
