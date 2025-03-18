package org.swdc.rmdisk.views.previews;

import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.fx.FXResources;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.Theme;
import org.swdc.rmdisk.RmDiskApplicationConfig;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.views.ClientFileConstants;

@MultipleImplement(ClientPreviewViewFactory.class)
public class ClientAudioPreviewFactory implements ClientPreviewViewFactory {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    @Inject
    private RmDiskApplicationConfig config;

    @Inject
    private FXResources resources;

    @Override
    public boolean support(RemoteResource resource) {
        if (resource == null || resource.isFolder()) {
            return false;
        }
        String extension = resource.getName()
                .substring(resource.getName().lastIndexOf('.') + 1)
                .toLowerCase();
        return ClientFileConstants.audioExts.contains(extension);
    }

    @Override
    public Stage createView(ClientFileProtocol protocol, String token, RemoteResource resource) {

        MediaPreviewStage stage = new MediaPreviewStage(
                protocol.getUrl(resource.getPath()) + "?token=" + token, fontawsome5Service
        );

        stage.setMinWidth(480);
        stage.setMinHeight(80);
        stage.setResizable(false);
        stage.setTitle(resource.getName());
        stage.getIcons().addAll(this.resources.getIcons());
        Theme theme = Theme.getTheme(config.getTheme(), resources.getAssetsFolder());
        theme.applyWithStage(stage);

        return stage;
    }

}
