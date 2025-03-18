package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

/**
 * 云服务器的主窗口。
 * 本Class的用途是初始化云服务器的界面。
 */
@View(
        viewLocation = "views/main/ServerView.fxml",
        title = "繁星云服务"
)
public class DiskServerView extends AbstractView {

    @Inject
    private Fontawsome5Service fontawsome5Service;



    @PostConstruct
    public void init() {

        Stage stage = getStage();
        stage.setMinWidth(880);
        stage.setMinHeight(600);

        setupToolButton(findById("startButton"), "play");
        setupToolButton(findById("loginButton"), "link");
        setupToolButton(findById("ftpStartButton"), "play");
        setupToolButton(findById("btnSetting"),"cog");

    }


    private void setupToolButton(ButtonBase toolButton, String icon) {

        toolButton.setFont(fontawsome5Service.getSolidFont(FontSize.VERY_SMALL));
        toolButton.setText(fontawsome5Service.getFontIcon(icon));

        toolButton.setPadding(new Insets(4));

    }


    public void switchServerState(boolean state) {
        Platform.runLater(() -> {
            Button startButton = findById("startButton");
            if (state) {
                startButton.setText(fontawsome5Service.getFontIcon("stop"));
            } else {
                startButton.setText(fontawsome5Service.getFontIcon("play"));
            }
        });
    }

    public void switchFTPServerState(boolean state) {
        Platform.runLater(() -> {
            Button ftpStartButton = findById("ftpStartButton");
            if (state) {
                ftpStartButton.setText(fontawsome5Service.getFontIcon("stop"));
            } else {
                ftpStartButton.setText(fontawsome5Service.getFontIcon("play"));
            }
        });
    }

}
