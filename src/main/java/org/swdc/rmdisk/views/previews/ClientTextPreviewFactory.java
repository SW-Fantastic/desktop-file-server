package org.swdc.rmdisk.views.previews;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.Theme;
import org.swdc.rmdisk.RmDiskApplicationConfig;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.views.ClientFileConstants;

@MultipleImplement(ClientPreviewViewFactory.class)
public class ClientTextPreviewFactory implements ClientPreviewViewFactory {

    @Inject
    private FXResources resources;

    @Inject
    private RmDiskApplicationConfig config;

    @Inject
    private Logger logger;

    @Override
    public boolean support(RemoteResource resource) {
        if (resource.isFolder()) {
            return false;
        }
        String extension = resource.getName()
                .substring(resource.getName().lastIndexOf(".") + 1)
                .toLowerCase();
        return ClientFileConstants.textExts.contains(extension);
    }

    @Override
    public Stage createView(ClientFileProtocol protocol,String token, RemoteResource resource) {
        try {


            BorderPane root = new BorderPane();

            TextArea area = new TextArea();
            area.setWrapText(true);
            area.setEditable(false);

            root.setCenter(area);
            root.getStyleClass().add("stage-dialog");

            StringBuilder text = new StringBuilder();
            protocol.loadResourceContent(token,resource.getPath(), (buffer,size) -> {
                text.append(new String(buffer));
            }, v-> {
                Platform.runLater(() -> {
                    area.setText(text.toString());
                });
            });

            Stage stage = new Stage();
            stage.setTitle(resource.getName());
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setMinHeight(400);
            stage.setMinWidth(600);
            Theme theme = Theme.getTheme(config.getTheme(), resources.getAssetsFolder());
            theme.applyWithStage(stage);

            return stage;
        } catch (Exception e) {
            logger.error("Error while creating text preview", e);
            return null;
        }
    }

}
