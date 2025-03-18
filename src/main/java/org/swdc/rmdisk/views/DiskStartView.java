package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

/**
 * 本云盘的启动窗口。
 * 在这里可以选择以服务器模式启动，还是以客户端模式启动。
 */
@View(
        viewLocation = "/views/main/StartView.fxml",
        title = "繁星云"
)
public class DiskStartView extends AbstractView {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    @PostConstruct
    public void init() {

        Stage stage = getStage();
        stage.setMinWidth(550);
        stage.setMinHeight(400);

        setupButton(findById("login-button"), "登录", "hdd");
        setupButton(findById("server-button"), "启动服务", "server");
        setupButton(findById("help-button"), "帮助", "book");

    }

    public void setupButton(Button button, String text,String icon) {
        VBox box = new VBox();
        Label lblIcon = new Label();
        lblIcon.setFont(fontawsome5Service.getSolidFont(FontSize.MIDDLE_LARGE));
        lblIcon.setText(fontawsome5Service.getFontIcon(icon));

        Label bar = new Label();
        bar.setMinHeight(4);
        bar.setPrefHeight(4);
        bar.setMaxHeight(4);
        bar.setMinWidth(60);
        bar.getStyleClass().add("bar");

        Label title = new Label(text);
        title.getStyleClass().add("title");
        box.getChildren().addAll(bar,lblIcon, title);
        box.setSpacing(8);
        box.getStyleClass().add("tile");
        box.setAlignment(Pos.CENTER);
        button.setText("");
        button.setPadding(new Insets(0,8,8,8));
        button.setGraphic(box);
    }

}
