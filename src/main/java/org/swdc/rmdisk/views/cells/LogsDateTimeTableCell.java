package org.swdc.rmdisk.views.cells;

import javafx.scene.control.TableCell;
import org.swdc.rmdisk.core.entity.Activity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogsDateTimeTableCell extends TableCell<Activity,Void> {

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        }  else {
            Activity activity = getTableRow().getItem();
            LocalDateTime dateTime = activity.getCreatedOn();
            String txt = dateTime.atZone(ZoneId.of("UTC"))
                    .format(DateTimeFormatter
                            .ofPattern("yyyy MM dd HH:mm:ss", Locale.ENGLISH)
                    );
            setText(txt);
        }
    }
}
