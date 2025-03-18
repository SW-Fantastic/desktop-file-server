package org.swdc.rmdisk.views.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.swdc.rmdisk.core.entity.UserGroup;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;

public class RegisterGroupTableCell extends TableCell<UserRegisterRequest, Void> {

    private HBox root = new HBox();

    private Label groupLabel = new Label();

    public RegisterGroupTableCell() {
        super();
        root.setPadding(new Insets(4));
        root.getChildren().add(groupLabel);
        root.setAlignment(Pos.CENTER);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            UserRegisterRequest request = getTableRow().getItem();
            UserGroup group = request.getGroup();
            groupLabel.setText(group.getGroupName());
            setGraphic(root);
        }
    }

}
