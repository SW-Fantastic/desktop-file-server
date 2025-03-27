package org.swdc.rmdisk.views.modals;

import jakarta.inject.Inject;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.core.LanguageKeys;

import java.util.ResourceBundle;

@View(
        viewLocation = "views/modals/ClientPropertiesView.fxml",
        dialog = true, multiple = true, resizeable = false
)
public class ClientPropertiesModal extends AbstractView {

    @Inject
    private FXResources resources;

    public void show(RemoteResource resource) {
        if (resource == null) {
            return;
        }

        ResourceBundle bundle = resources.getResourceBundle();

        Stage stage = getStage();
        stage.setTitle(bundle.getString(LanguageKeys.CLIENT_DLG_PROP) + " - " + resource.getName());
        TextField txtName = findById("txtName");
        TextField txtSize = findById("txtSize");
        TextField txtType = findById("txtType");

        txtName.setText(resource.getName());
        txtType.setText(resource.isFolder() ?
                bundle.getString(LanguageKeys.CLIENT_DLG_PROP_FOLDER) :
                bundle.getString(LanguageKeys.CLIENT_DLG_PROP_FILE)
        );
        if (resource.isFolder()) {
            txtSize.setText("N/A");
        } else {
            txtSize.setText( resource.getContentSize() / (1000.0 * 1000.0) + "MB" );
        }

        Button btnClose = findById("btnClose");
        btnClose.setOnAction(e -> hide());

        super.show();
    }


    @Override
    public void show() {
        throw new RuntimeException("using show(RemoteResource) instead");
    }
}
