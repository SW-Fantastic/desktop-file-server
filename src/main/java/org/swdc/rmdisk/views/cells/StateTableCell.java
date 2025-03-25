package org.swdc.rmdisk.views.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.entity.State;
import org.swdc.rmdisk.core.entity.User;

import java.util.ResourceBundle;

public class StateTableCell extends TableCell<User,Void> {

    private HBox root = null;

    private HBox container = null;

    private Label label = null;

    private Label icon = null;

    private Fontawsome5Service fontawsome5Service = null;

    private ResourceBundle bundle = null;

    public StateTableCell(ResourceBundle bundle, Fontawsome5Service fontawsome5Service) {

        this.bundle = bundle;
        container = new HBox();
        this.label = new Label();
        this.icon = new Label();
        this.icon.setFont(
                fontawsome5Service.getSolidFont(FontSize.SMALL)
        );
        this.icon.setText(fontawsome5Service.getFontIcon("paw"));
        this.label = new Label();

        container.setSpacing(2);
        container.setPadding(new Insets(4));
        container.getChildren().addAll(icon, label);
        container.setAlignment(Pos.CENTER);

        root = new HBox();
        root.setPadding(new Insets(2));
        root.getChildren().add(container);
        root.setAlignment(Pos.CENTER);

    }

    @Override
    protected void updateItem(Void item, boolean empty) {

        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        container.getStyleClass().clear();
        User user = getTableRow().getItem();
        if (user.getState() == State.NORMAL) {
            label.setText(bundle.getString(LanguageKeys.STATE_NORMAL));
            container.getStyleClass().add("normal");
        } else {
            label.setText(bundle.getString(LanguageKeys.STATE_DISABLE));
            container.getStyleClass().add("trashed");
        }
        root.prefWidthProperty().unbind();
        root.prefWidthProperty().bind(widthProperty());
        setGraphic(root);

    }
}
