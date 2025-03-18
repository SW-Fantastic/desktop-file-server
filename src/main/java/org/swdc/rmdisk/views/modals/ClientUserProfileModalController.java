package org.swdc.rmdisk.views.modals;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.Prototype;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.views.events.ClientUserRefreshEvent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.ConnectException;
import java.util.Base64;

@Prototype
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

            if (avatar.contains("://")) {
                // TODO: load avatar from URL
            } else {
                byte[] avatarData = Base64.getDecoder().decode(avatar);
                Image image = new Image(new ByteArrayInputStream(avatarData));
                this.avatar.setImage(image);
            }

            Rectangle clip = new Rectangle(0,0,this.avatar.getFitWidth(),this.avatar.getFitHeight());
            clip.setArcHeight(16);
            clip.setArcWidth(16);
            this.avatar.setClip(clip);
            return true;
        } catch (ConnectException e) {
            return false;
        }

    }

    @FXML
    private void updateAvatar() {

        ClientUserProfileModal modal = getView();

        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("图片", "*.png", "*.jpg","*.jpeg"));
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
                Alert alert = view.alert("失败", "头像更换失败，未知错误", Alert.AlertType.ERROR);
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
            Alert alert = getView().alert("失败", "昵称更新失败，未知错误", Alert.AlertType.ERROR);
            alert.showAndWait();
        }
    }

    @FXML
    private void close() {
        getView().hide();
    }

}
