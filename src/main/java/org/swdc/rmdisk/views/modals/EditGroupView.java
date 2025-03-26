package org.swdc.rmdisk.views.modals;

import jakarta.annotation.PostConstruct;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.entity.State;
import org.swdc.rmdisk.core.entity.UserGroup;

@View(
        title = LanguageKeys.UI_SERVER_DLG_EDIT_GROUP_TITLE,
        dialog = true,
        multiple = true,
        resizeable = false,
        viewLocation = "views/modals/EditGroup.fxml"
)
public class EditGroupView extends AbstractView {

    @PostConstruct
    public void init() {

        Button submit = findById("submit");
        submit.setOnAction(this::onSubmit);

        Button cancel = findById("cancel");
        cancel.setOnAction(this::hideDialog);

    }

    public String showDialog(UserGroup group) {

        TextField field = findById("txtName");
        if (group != null) {
            field.setText(group.getGroupName());
        } else {
            field.setText("");
        }
        this.show();
        return field.getText();

    }

    public void hideDialog(ActionEvent event) {
        TextField field = findById("txtName");
        field.setText("");
        this.hide();
    }

    public void onSubmit(ActionEvent event) {
        this.hide();
    }

}
