package org.swdc.rmdisk;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.swdc.data.EMFProviderFactory;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EnvironmentLoader;
import org.swdc.fx.FXApplication;
import org.swdc.fx.FXResources;
import org.swdc.fx.SWFXApplication;
import org.swdc.rmdisk.core.EMFactory;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.views.DiskStartView;
import javafx.scene.image.Image;
import org.swdc.rmdisk.views.TrayView;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

@SWFXApplication(
        splash = SplashView.class,
        configs = RmDiskApplicationConfig.class,
        assetsFolder = "./assets",
        icons = { "disk-16.png","disk-24.png","disk-32.png","disk-64.png","disk-128.png","disk-256.png" }
)
public class RmDiskServiceApplication extends FXApplication {

    @Override
    public void onConfig(EnvironmentLoader environmentLoader) {
        environmentLoader.withProvider(EMFactory.class);
    }

    @Override
    public void onStarted(DependencyContext dependencyContext) {

        EMFProviderFactory factory = dependencyContext.getByClass(EMFProviderFactory.class);
        factory.create();

        DiskStartView diskStartView = dependencyContext.getByClass(DiskStartView.class);

        SystemTray tray = SystemTray.getSystemTray();
        try {

            TrayView trayView = dependencyContext.getByClass(TrayView.class);

            FXResources resources = dependencyContext.getByClass(FXResources.class);
            ResourceBundle bundle = resources.getResourceBundle();
            Image image = resources.getIcons().get(0);
            TrayIcon icon = new TrayIcon(SwingFXUtils.fromFXImage(image, null));
            icon.setToolTip(bundle.getString(LanguageKeys.APP_NAME));
            icon.setImageAutoSize(true);
            icon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        trayView.show(e);
                    } else {
                        Platform.runLater(diskStartView::show);
                    }
                }
            });
            tray.add(icon);
            Platform.setImplicitExit(false);
        } catch (AWTException e) {
        }

        diskStartView.show();

    }

}
