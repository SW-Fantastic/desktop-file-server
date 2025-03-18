package org.swdc.rmdisk.views.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.swdc.rmdisk.core.entity.UserGroup;

import java.util.List;

public class GroupListCell extends ListCell<UserGroup> {

    private HBox box;

    private Label text;

    public GroupListCell() {
        box = new HBox();
        box.setPadding(new Insets(4));

        text = new Label();
        box.getChildren().add(text);
        box.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(UserGroup item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        text.setText(item.getGroupName());
        List<String> classes = text.getStyleClass();
        classes.clear();
        if (item.isRegistrable()) {
            classes.add("accepted");
        } else {
            classes.add("rejected");
        }
        setGraphic(box);
    }

}
