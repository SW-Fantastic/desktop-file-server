package org.swdc.rmdisk.views.modals;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.client.RemoteResource;

@View(viewLocation = "views/modals/ClientEditName.fxml",dialog = true,title = "编辑", multiple = true)
public class ClientNameEditModal extends AbstractView {

    private boolean cancel = false;

    public String show(RemoteResource resource) {
        TextField field = findById("txtName");

        Button btnOk = findById("btnOk");
        btnOk.setOnAction(this::onOk);

        Button btnCancel = findById("btnCancel");
        btnCancel.setOnAction(this::onCancel);

        if (resource != null) {
            field.setText(resource.getName());
        }

        super.show();
        if (cancel) {
            field.setText("");
            return null;
        }
        return field.getText();
    }


    private void onOk(ActionEvent actionEvent) {
        super.hide();
    }

    private void onCancel(ActionEvent actionEvent) {
        cancel = true;
        super.hide();
    }

    @Override
    public void show() {
        throw new RuntimeException("use show(RemoteResource resource) instead");
    }
}
