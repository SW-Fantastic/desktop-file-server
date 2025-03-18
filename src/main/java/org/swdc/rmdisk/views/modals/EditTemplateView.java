package org.swdc.rmdisk.views.modals;

import jakarta.annotation.PostConstruct;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.core.entity.TemplateFolder;
import javafx.scene.control.Button;

@View(
        viewLocation = "views/modals/EditFolderTemplate.fxml",
        dialog = true,
        title = "编辑目录",
        resizeable = false
)
public class EditTemplateView extends AbstractView {

    private boolean cancelFlag = false;

    @PostConstruct
    public void init() {
        Button cancelButton = findById("btnCancel");
        cancelButton.setOnAction(this::onCancel);

        Button submitButton = findById("btnSubmit");
        submitButton.setOnAction(this::onOK);
    }

    public String showDialog(TemplateFolder templateFolder) {
        TextField field = findById("txtName");
        if (templateFolder == null) {
            field.setText("");
        } else {
            field.setText(templateFolder.getName());
        }
        show();
        if (cancelFlag) {
            return null;
        }
        return field.getText();
    }

    public void onCancel(ActionEvent event) {
        cancelFlag = true;
        hide();
    }

    public void onOK(ActionEvent event) {
        cancelFlag = false;
        hide();
    }

}
