package org.swdc.rmdisk.views.modals;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.views.events.ClientLogoutEvent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.ConnectException;
import java.util.Base64;
import java.util.ResourceBundle;

public class ClientUserProfileModalController extends ViewController<ClientUserProfileModal> {

    @FXML
    private TextField nickNameField;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField groupNameField;

    @FXML
    private TextField totalSpaceField;

    @FXML
    private ImageView avatar;

    @Inject
    private FXResources resources;

    @Inject
    private ClientResetPasswordModal resetPasswordModal;

    private ClientFileProtocol protocol;

    private String token;

    private RemoteUser user;

    public boolean update(ClientFileProtocol protocol, String token) {

        try {

            this.protocol = protocol;
            this.token = token;
            this.user = protocol.getUserInfo(token);

            nickNameField.setText(user.getNickname());
            userIdField.setText(user.getUsername());
            totalSpaceField.setText(user.getTotalSize() / (1000.0 * 1000.0) + "MB" );
            String avatar = user.getAvatar();

            byte[] avatarData = Base64.getDecoder().decode(avatar);
            Image image = new Image(new ByteArrayInputStream(avatarData));
            this.avatar.setImage(image);

            Rectangle clip = new Rectangle(0,0,this.avatar.getFitWidth(),this.avatar.getFitHeight());
            clip.setArcHeight(16);
            clip.setArcWidth(16);
            this.avatar.setClip(clip);
            return true;
        } catch (ConnectException e) {
            return false;
        }

    }

    @EventListener(type = ClientLogoutEvent.class)
    public void onLogout(ClientLogoutEvent event) {
        Platform.runLater(() -> {
            getView().hide();
        });
    }

    @FXML
    private void updateAvatar() {

        ResourceBundle bundle = resources.getResourceBundle();
        ClientUserProfileModal modal = getView();

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(bundle.getString(LanguageKeys.CLIENT_IMAGE),
                        "*.png", "*.jpg","*.jpeg"
                )
        );
        File avatarFile = chooser.showOpenDialog(modal.getStage());
        if (avatarFile == null) {
            return;
        }
        EditAvatarView view = modal.getView(EditAvatarView.class);
        byte[] data = view.showModal(avatarFile);
        if (data != null) {
            try {
                RemoteUser user = new RemoteUser();
                user.setAvatar(Base64.getEncoder().encodeToString(data));
                user = protocol.updateUserInfo(token,user);
                if (user != null) {
                    this.update(protocol,token);
                }
            } catch (Exception e) {
                Alert alert = view.alert(
                        bundle.getString(LanguageKeys.ERROR),
                        bundle.getString(LanguageKeys.CLIENT_DLG_UPDATE_INFO_FAILED),
                        Alert.AlertType.ERROR
                );
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void updateNickName() {
        try {

            String text = nickNameField.getText();
            if (text.isBlank() || text.equals(user.getNickname())) {
                nickNameField.setText(user.getNickname());
                return;
            }

            RemoteUser user = new RemoteUser();
            user.setNickname(text);
            user = protocol.updateUserInfo(token, user);
            if (user != null) {
                update(protocol, token);
            }

        } catch (ConnectException e) {

            ResourceBundle bundle = resources.getResourceBundle();

            Alert alert = getView().alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.CLIENT_DLG_UPDATE_INFO_FAILED),
                    Alert.AlertType.ERROR
            );
            alert.showAndWait();
        }
    }

    @FXML
    public void resetPassword() {
        resetPasswordModal.show(protocol, token);
    }

    @FXML
    private void close() {
        getView().hide();
    }

}
