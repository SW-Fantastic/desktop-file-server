package org.swdc.rmdisk.views.modals;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBase;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.core.entity.UserGroup;

@View(
        viewLocation = "views/modals/EditFolderStructure.fxml",
        title = "编辑默认文件结构",
        dialog = true,
        multiple = true
)
public class FolderStructureView extends AbstractView {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    private UserGroup group;

    @PostConstruct
    public void init() {
        setupToolButton(findById("addTemplate"), "plus");
    }

    private void setupToolButton(ButtonBase toolButton, String icon) {
        toolButton.setFont(fontawsome5Service.getSolidFont(FontSize.SMALL));
        toolButton.setText(fontawsome5Service.getFontIcon(icon));
        toolButton.setPadding(new Insets(4));
    }

    public UserGroup getGroup() {
        return group;
    }

    public void showDialog(UserGroup group) {
        this.group = group;
        FolderStructureController controller = getController();
        controller.initFolders();
        this.show();
    }


}
