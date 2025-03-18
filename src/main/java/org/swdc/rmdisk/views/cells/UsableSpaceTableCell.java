package org.swdc.rmdisk.views.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.swdc.rmdisk.core.entity.User;

public class UsableSpaceTableCell extends TableCell<User,Void> {

    private HBox cell;

    private ProgressBar progressBar;

    public UsableSpaceTableCell() {

        progressBar = new ProgressBar();

        cell = new HBox();
        cell.setAlignment(Pos.CENTER);
        cell.getChildren().add(progressBar);
        cell.setFillHeight(true);

    }

    @Override
    protected void updateItem(Void item, boolean empty) {

        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        User user = getTableRow().getItem();
        long total = user.getTotalSize();
        long used = user.getUsedSize();

        double percent = 0d;
        if (total > 0) {
            percent = (double) used / (double) total;
        }
        progressBar.setProgress(percent);
        progressBar.prefWidthProperty().unbind();
        progressBar.prefWidthProperty().bind(getTableColumn().widthProperty().subtract(16));
        setGraphic(cell);

    }


}
