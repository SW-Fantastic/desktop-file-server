package org.swdc.rmdisk.views.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.swdc.rmdisk.core.entity.CommonState;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;

public class RegisterStateTableCell extends TableCell<UserRegisterRequest,Void> {

    private HBox root = new HBox();

    private Label stateLabel = new Label();

    public RegisterStateTableCell() {
        super();
        root.setPadding(new Insets(4));
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(stateLabel);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            UserRegisterRequest request = getTableRow().getItem();
            if (request.getState() == CommonState.PENDING) {
                stateLabel.setText("等待审核");
            } else if (request.getState() == CommonState.ACCEPTED) {
                stateLabel.setText("已通过");
            } else if (request.getState() == CommonState.REJECTED) {
                stateLabel.setText("已拒绝");
            }
            setGraphic(root);
        }
    }
}
