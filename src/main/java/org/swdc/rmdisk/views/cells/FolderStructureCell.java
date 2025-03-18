package org.swdc.rmdisk.views.cells;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.rmdisk.core.entity.TemplateFolder;

public class FolderStructureCell extends TreeCell<TemplateFolder> {

    private HBox root;

    private Label icon;

    private Label name;

    private Fontawsome5Service fontawsome5Service;
    public FolderStructureCell(Fontawsome5Service fontawsome5Service) {
        this.fontawsome5Service = fontawsome5Service;
        root = new HBox();
        root.setSpacing(8);
        root.setAlignment(Pos.CENTER_LEFT);

        icon = new Label();
        icon.setFont(fontawsome5Service.getSolidFont(FontSize.MIDDLE_SMALL));

        name = new Label();
        root.getChildren().addAll(icon, name);
    }

    @Override
    protected void updateItem(TemplateFolder item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        if (getTreeItem().isExpanded()) {
            icon.setText(fontawsome5Service.getFontIcon("folder-open"));
        } else {
            icon.setText(fontawsome5Service.getFontIcon("folder"));
        }
        name.setText(item.getName());
        setGraphic(root);
    }

}
