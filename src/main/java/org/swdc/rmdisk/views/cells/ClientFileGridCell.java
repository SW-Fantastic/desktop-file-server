package org.swdc.rmdisk.views.cells;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.GridCell;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.rmdisk.client.RemoteResource;

import static org.swdc.rmdisk.views.ClientFileConstants.*;

public class ClientFileGridCell extends GridCell<RemoteResource> {



    private Fontawsome5Service fontawsome5Service;

    private ObjectProperty<RemoteResource> selectContainer;

    private Label icon;

    private Label text;

    private VBox root;

    public ClientFileGridCell(Fontawsome5Service fontawsome5Service, SimpleObjectProperty<RemoteResource> simpleObjectProperty,SimpleBooleanProperty anyChanged) {
        this.fontawsome5Service = fontawsome5Service;
        this.selectContainer = simpleObjectProperty;
        this.selectContainer.addListener( c -> {
            if (root == null) {
                return;
            }
            if (selectContainer.get() != null && getItem() != null && selectContainer.get().getPath().equals(getItem().getPath())){
                root.getStyleClass().add("selected");
            } else {
                root.getStyleClass().remove("selected");
            }
        });
        setOnMouseClicked(e -> {
            selectContainer.set(getItem());
            anyChanged.set(true);
        });

    }


    @Override
    protected void updateItem(RemoteResource item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        if (root == null) {
            root = new VBox();
            text = new Label();
            text.getStyleClass().add("file-name");
            text.setWrapText(true);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setAlignment(Pos.CENTER);
            text.maxWidthProperty().bind(getGridView().widthProperty());

            icon = new Label();
            root.getChildren().addAll(
                    icon, text
            );
            root.setAlignment(Pos.TOP_CENTER);
            root.setSpacing(6);
            root.setPadding(new Insets(4));

        }


        String ext = "";
        if (item.getName().contains(".")) {
            ext = item.getName().substring(
                    item.getName().lastIndexOf(".") + 1
            );
        }

        icon.setFont(fontawsome5Service.getSolidFont(FontSize.MIDDLE_LARGE));
        icon.getStyleClass().add("icon");

        text.setText(item.getName());
        if (ext.isBlank()) {
            icon.setText(fontawsome5Service.getFontIcon("folder"));
        } else {
            ext = ext.toLowerCase();
            if (audioExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("volume-up"));
            } else if (videoExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("video"));
            } else if (imageExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("image"));
            } else if (archiveExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("file-archive"));
            } else if (textExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("file-alt"));
            } else if (configExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("cog"));
            } else if (webExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("html5"));
            } else if (codeExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("code"));
            } else if (libExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("cube"));
            } else if (executableExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("box"));
            } else if (documentExts.contains(ext)) {
                icon.setText(fontawsome5Service.getFontIcon("file-word"));
            } else {
                icon.setText(fontawsome5Service.getFontIcon("file"));
            }
        }
        setGraphic(root);
    }

}
