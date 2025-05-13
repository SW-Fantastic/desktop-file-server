package org.swdc.rmdisk.views.cells;

import javafx.scene.control.TableCell;
import org.swdc.rmdisk.core.entity.Activity;

public class LogsUserNameTableCell extends TableCell<Activity,String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        Activity activity = getTableRow().getItem();
        if (activity == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(activity.getUser().getUsername());
        }
    }

}
