package org.swdc.rmdisk.views.previews;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.views.ClientFileConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@MultipleImplement(ClientPreviewViewFactory.class)
public class ClientImagePreviewFactory implements ClientPreviewViewFactory {

    @Inject
    private Logger logger;

    @Override
    public boolean support(RemoteResource resource) {
        if (resource == null || resource.isFolder()) {
            return false;
        }
        String extension = resource.getName()
                .substring(resource.getName().lastIndexOf('.') + 1)
                .toLowerCase();
        return ClientFileConstants.imageExts.contains(extension);
    }

    @Override
    public Stage createView(ClientFileProtocol protocol, String token, RemoteResource resource) {
        try {

            ImageView view = new ImageView();
            BorderPane root = new BorderPane();
            root.setCenter(view);
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(resource.getName());

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            protocol.loadResourceContent(token, resource.getPath(), (bytes,size) -> {
                try {
                    os.write(bytes, 0, size);
                    os.flush();
                } catch (Exception ignore) {}
            }, v -> {

                Platform.runLater(() -> {
                    Image image = new Image(new ByteArrayInputStream(os.toByteArray()));
                    if (image.getWidth() < 500) {
                        view.setFitHeight(image.getHeight());
                        view.setFitWidth(image.getWidth());
                    } else {
                        double rate = image.getWidth() / 500;
                        view.setFitWidth(500);
                        view.setFitHeight(image.getHeight() / rate);
                    }
                    view.setImage(image);
                    stage.setMinWidth(view.getFitWidth());
                    stage.setMinHeight(view.getFitHeight());
                    stage.setWidth(view.getFitWidth());
                    stage.setHeight(view.getFitHeight());
                });

            });

            return stage;

        } catch (Exception e) {
            logger.error("Error while creating image preview", e);
        }
        return null;
    }

}
