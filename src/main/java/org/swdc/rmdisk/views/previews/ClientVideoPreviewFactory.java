package org.swdc.rmdisk.views.previews;

import jakarta.inject.Inject;
import javafx.stage.Stage;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.fx.FXResources;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.Theme;
import org.swdc.rmdisk.RmDiskApplicationConfig;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.views.ClientFileConstants;

@MultipleImplement(ClientPreviewViewFactory.class)
public class ClientVideoPreviewFactory implements ClientPreviewViewFactory {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    @Inject
    private FXResources resources;

    @Inject
    private RmDiskApplicationConfig config;

    @Override
    public boolean support(RemoteResource resource) {
        if (resource == null || resource.isFolder()) {
            return false;
        }
        String extension = resource.getName()
                .substring(resource.getName().lastIndexOf('.') + 1)
                .toLowerCase();
        return ClientFileConstants.videoExts.contains(extension);
    }

    @Override
    public Stage createView(ClientFileProtocol protocol, String token, RemoteResource resource) {
        MediaPreviewStage stage = new MediaPreviewStage(
                protocol.getUrl(resource.getPath()) + "?token=" + token, fontawsome5Service
        );

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle(resource.getName());
        stage.previewVideo();

        stage.getIcons().addAll(this.resources.getIcons());
        Theme theme = Theme.getTheme(config.getTheme(), resources.getAssetsFolder());
        theme.applyWithStage(stage);

        return stage;
    }

}
