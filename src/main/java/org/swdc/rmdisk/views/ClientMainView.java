package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.core.LanguageKeys;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@View(
        viewLocation = "views/main/ClientView.fxml",
        title = LanguageKeys.UI_APP_NAME
)
public class ClientMainView extends AbstractView {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    private ClientGridView filesGridView = null;

    private ClientResourceView currentView = null;


    @PostConstruct
    public void init() {

        ImageView avatar = findById("avatar");
        Rectangle clip = new Rectangle(0,0,avatar.getFitWidth(),avatar.getFitHeight());
        clip.setArcHeight(8);
        clip.setArcWidth(8);
        avatar.setClip(clip);

        filesGridView = new ClientGridView(fontawsome5Service);
        currentView = filesGridView;

        BorderPane folderPane = findById("folderView");
        folderPane.setCenter(currentView.getView());

        setupToolButton(findById("btnLogout"), "power-off");
        setupToolButton(findById("btnRefresh"), "sync");
        setupToolButton(findById("btnGoBack"), "arrow-left");
        setupToolButton(findById("btnGoForward"), "arrow-right");
        setupToolButton(findById("btnUpperLevel"), "level-up-alt");
        setupToolButton(findById("btnDownload"), "download");

    }

    public ClientResourceView getCurrentView() {
        return filesGridView;
    }

    private void setupToolButton(ButtonBase toolButton, String icon) {
        toolButton.setFont(fontawsome5Service.getSolidFont(FontSize.VERY_SMALL));
        toolButton.setText(fontawsome5Service.getFontIcon(icon));
        toolButton.setPadding(new Insets(4));
    }


    public void show(String token, ClientFileProtocol clientFiles) {

        if (token == null || token.isEmpty() || clientFiles == null) {
            return;
        }

        ClientMainController controller = getController();
        controller.update(token, clientFiles);

        super.show();

    }

    public void updateInfo(RemoteUser user) {
        ImageView avatarView = findById("avatar");
        String avatar = user.getAvatar();

        byte[] avatarData = Base64.getDecoder().decode(avatar);
        Image image = new Image(new ByteArrayInputStream(avatarData));
        avatarView.setImage(image);

        Label nicknameLabel = findById("lblnickname");
        nicknameLabel.setText(user.getNickname());

        Label usernameLabel = findById("lblusername");
        usernameLabel.setText(user.getUsername());

        ProgressBar progressBar = findById("progSpace");
        progressBar.setProgress((double)user.getUsedSize() / (double)user.getTotalSize());

        Label lblSpace = findById("lblSpace");
        double totalGb = ((double) user.getTotalSize()) / (1024 * 1024 * 1024);
        double usedGb = ((double) user.getUsedSize()) / (1024 * 1024 * 1024);
        lblSpace.setText(String.format("%.1f Gb / %.1f Gb", usedGb, totalGb));
    }

    public boolean isLoggedIn() {
        ClientMainController controller = getController();
        return controller.isLoggedIn();
    }

    @Override
    public void show() {
        if (isLoggedIn()) {
            ClientMainController controller = getController();
            controller.refreshFolder();
            super.show();
        } else {
            ClientLoginView loginView = getView(ClientLoginView.class);
            loginView.show();
        }
    }


}
