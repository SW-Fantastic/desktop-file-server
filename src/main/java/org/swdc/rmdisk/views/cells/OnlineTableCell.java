package org.swdc.rmdisk.views.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.service.SecureService;

public class OnlineTableCell extends TableCell<User,Void> {

    private HBox root = null;

    private HBox container = null;

    private Label label = new Label();

    private SecureService secureService = null;

    public OnlineTableCell(Fontawsome5Service fontawsome5Service, SecureService secureService) {

        this.secureService = secureService;

        label.setFont(fontawsome5Service.getSolidFont(FontSize.SMALL));
        label.setText(fontawsome5Service.getFontIcon("power-off"));
        container = new HBox(label);
        container.setPadding(new Insets(4));

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

        User user = getTableRow().getItem();

        container.getStyleClass().clear();
        if(secureService.isOnline(user.getId())) {
            container.getStyleClass().add("normal");
        } else {
            container.getStyleClass().add("trashed");
        }
        setGraphic(root);
    }

}
