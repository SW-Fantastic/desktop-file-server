package org.swdc.rmdisk.views.modals;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.core.ManagedServerConfigure;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.core.entity.State;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.entity.UserGroup;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;
import org.swdc.rmdisk.service.CommonService;
import org.swdc.rmdisk.service.UserManageService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;


@View(
        viewLocation = "views/modals/EditUser.fxml",
        dialog = true,
        multiple = true,
        resizeable = false,
        title = "编辑用户"
)
public class EditUserView extends AbstractView {

    private boolean canceled = false;

    private User editingUser;

    private byte[] avatar;

    @Inject
    private Logger logger;

    @Inject
    private EditAvatarView editAvatarView;

    @Inject
    private FXResources resources;

    @Inject
    private ManagedServerConfigure serverConfigure;

    @Inject
    private CommonService commonService;

    @Inject
    private UserManageService userManageService;

    @PostConstruct
    public void init() {

        Stage stage = getStage();
        stage.setOnCloseRequest(e -> {
            canceled = true;
        });

        Slider slider = findById("sliderSpace");
        slider.valueProperty().addListener(this::updateSliderValue);

        TextField sliderField = findById("txtSpace");
        sliderField.setEditable(false);

        Button cancelButton = findById("btnCancel");
        cancelButton.setOnAction(this::onCancel);

        Button submitButton = findById("btnSubmit");
        submitButton.setOnAction(this::onSubmit);

        Button avatarButton = findById("btnAvatar");
        avatarButton.setOnAction(this::onSelectAvatar);

        ComboBox<UserGroup> groupComboBox = findById("cbxGroup");
        groupComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::onGroupSelected);
        groupComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserGroup object) {
                return object.getGroupName();
            }

            @Override
            public UserGroup fromString(String string) {
                return null;
            }
        });
    }

    private void onGroupSelected(Observable observable) {
        ComboBox<UserGroup> groupComboBox = findById("cbxGroup");
        UserGroup group = groupComboBox.getSelectionModel().getSelectedItem();
        if (group == null || editingUser == null) {
            return;
        }
        editingUser.setGroup(group);
    }

    private void updateSliderValue(Observable observable, Number oldValue, Number newValue) {
        TextField sliderField = findById("txtSpace");
        sliderField.setText(newValue.toString() + " GB");
    }

    public User showDialog(UserRegisterRequest request) {
        if (request == null) {
            return null;
        }

        TextField txtUsername = findById("txtUsername");
        PasswordField txtPassword = findById("txtPassword");
        TextField txtSpace = findById("txtSpace");
        TextField txtNickname = findById("txtNickName");
        Slider slider = findById("sliderSpace");

        ComboBox<UserGroup> groupComboBox = findById("cbxGroup");
        List<UserGroup> groups = userManageService.getGroupsByState(State.NORMAL);
        ObservableList<UserGroup> userGroups = groupComboBox.getItems();
        userGroups.clear();
        userGroups.addAll(groups);


        if (request.getGroup() != null) {
            for (UserGroup g : groups) {
                if (g.getId().equals(request.getGroup().getId())) {
                    groupComboBox.getSelectionModel().select(g);
                    break;
                }
            }
        }

        editingUser = new User();
        editingUser.setPassword(request.getPassword());
        editingUser.setGroup(request.getGroup());
        editingUser.setNickname(request.getNickname());
        editingUser.setUsername(request.getName());
        editingUser.setPassword(request.getPassword());
        editingUser.setTotalSize(serverConfigure.getDefaultSpaceSize() * 1000L * 1000L * 1000L);

        ImageView imageView = findById("avatar");
        Rectangle clip = new Rectangle(0,0,imageView.getFitWidth(),imageView.getFitHeight());
        clip.setArcHeight(8);
        clip.setArcWidth(8);
        imageView.setClip(clip);

        if (request.getNickname() != null) {
            txtNickname.setText(request.getNickname());
        }

        byte[] avatar = commonService.getDefaultAvatar();
        if (avatar != null) {
            ByteArrayInputStream bin = new ByteArrayInputStream(avatar);
            Image image = new Image(bin);
            imageView.setImage(image);
            editingUser.setAvatar(Base64.getEncoder().encodeToString(avatar));
        }

        txtPassword.setText(request.getPassword());
        txtUsername.setText(request.getName());
        txtSpace.setText(serverConfigure.getDefaultSpaceSize() + " GB");
        slider.setValue(serverConfigure.getDefaultSpaceSize());

        show();

        if (this.canceled) {
            return null;
        }
        if (!txtPassword.getText().isBlank()) {
            editingUser.setPassword(txtPassword.getText());
        }
        if (!txtUsername.getText().isBlank()) {
            editingUser.setUsername(txtUsername.getText());
        }

        long nextSpace = Double.valueOf(slider.getValue()).longValue() * 1000 * 1000 * 1000;
        if (editingUser.getUsedSize() == null || nextSpace > editingUser.getUsedSize()) {
            editingUser.setTotalSize(nextSpace);
        }

        if (editingUser.getId() == null) {
            if (editingUser.getUsername() == null || editingUser.getPassword() == null) {
                return null;
            }
            if (editingUser.getUsername().isBlank() || editingUser.getPassword().isBlank()) {
                return null;
            }
        }

        if (this.avatar != null) {
            editingUser.setAvatar(
                    Base64.getEncoder().encodeToString(this.avatar)
            );
        }

        if(!txtNickname.getText().isBlank()) {
            editingUser.setNickname(txtNickname.getText());
        }

        return editingUser;

    }

    public User showDialog(UserGroup group, User user) {
        if (group == null) {
            return null;
        }
        if (user == null) {
            editingUser = new User();
        } else {
            editingUser = user;
        }

        TextField txtUsername = findById("txtUsername");
        PasswordField txtPassword = findById("txtPassword");
        TextField txtSpace = findById("txtSpace");
        TextField txtNickname = findById("txtNickName");
        Slider slider = findById("sliderSpace");

        ComboBox<UserGroup> groupComboBox = findById("cbxGroup");
        List<UserGroup> groups = userManageService.getGroupsByState(State.NORMAL);
        ObservableList<UserGroup> userGroups = groupComboBox.getItems();
        userGroups.clear();
        userGroups.addAll(groups);
        groupComboBox.getSelectionModel()
                .select(group);

        for (UserGroup g : groups) {
            if (g.getId().equals(group.getId())) {
                groupComboBox.getSelectionModel().select(g);
                break;
            }
        }

        ImageView imageView = findById("avatar");
        Rectangle clip = new Rectangle(0,0,imageView.getFitWidth(),imageView.getFitHeight());
        clip.setArcHeight(8);
        clip.setArcWidth(8);
        imageView.setClip(clip);

        if (editingUser.getId() != null) {
            int space = Long.valueOf(editingUser.getTotalSize() / (1000 * 1000 * 1000)).intValue();
            txtUsername.setText(editingUser.getUsername());
            txtSpace.setText(space + " GB");
            slider.setValue(space);
        } else {
            txtSpace.setText(serverConfigure.getDefaultSpaceSize() + " GB");
            slider.setValue(serverConfigure.getDefaultSpaceSize());
        }

        if (editingUser.getNickname() != null) {
            txtNickname.setText(editingUser.getNickname());
        }

        if (editingUser.getAvatar() != null && !editingUser.getAvatar().isBlank()) {

            byte[] avatar = Base64.getDecoder().decode(editingUser.getAvatar());
            ByteArrayInputStream bin = new ByteArrayInputStream(avatar);
            Image image = new Image(bin);
            imageView.setImage(image);

        } else {

            byte[] avatar = commonService.getDefaultAvatar();
            if (avatar != null) {
                ByteArrayInputStream bin = new ByteArrayInputStream(avatar);
                Image image = new Image(bin);
                imageView.setImage(image);
                editingUser.setAvatar(Base64.getEncoder().encodeToString(avatar));
            }

        }

        this.show();
        if (this.canceled) {
            return null;
        }
        if (!txtPassword.getText().isBlank()) {
            editingUser.setPassword(txtPassword.getText());
        }
        if (!txtUsername.getText().isBlank()) {
            editingUser.setUsername(txtUsername.getText());
        }

        long nextSpace = Double.valueOf(slider.getValue()).longValue() * 1000 * 1000 * 1000;
        if (editingUser.getUsedSize() == null || nextSpace > editingUser.getUsedSize()) {
            editingUser.setTotalSize(nextSpace);
        }

        if (editingUser.getId() == null) {
            if (editingUser.getUsername() == null || editingUser.getPassword() == null) {
                return null;
            }
            if (editingUser.getUsername().isBlank() || editingUser.getPassword().isBlank()) {
                return null;
            }
        }

        if (this.avatar != null) {
            editingUser.setAvatar(
                    Base64.getEncoder().encodeToString(this.avatar)
            );
        }

        if(!txtNickname.getText().isBlank()) {
            editingUser.setNickname(txtNickname.getText());
        }

        return editingUser;
    }

    private void onCancel(ActionEvent actionEvent) {
        this.canceled = true;
        this.hide();
    }

    private void onSubmit(ActionEvent event) {
        this.canceled = false;
        this.hide();
    }

    private void onSelectAvatar(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg"));
        chooser.setTitle("选择头像");
        File file = chooser.showOpenDialog(getStage());
        if (file == null) {
            return;
        }
        this.avatar = this.editAvatarView.showModal(file);
        if (avatar == null) {
            return;
        }

        ByteArrayInputStream bin = new ByteArrayInputStream(avatar);
        Image image = new Image(bin);
        ImageView imageView = findById("avatar");
        imageView.setImage(image);
    }

}
