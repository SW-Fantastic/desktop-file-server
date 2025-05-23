package org.swdc.rmdisk;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
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
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ResourceBundle;

@SWFXApplication(
        splash = SplashView.class,
        configs = RmDiskApplicationConfig.class,
        assetsFolder = "./assets",
        icons = { "disk-16.png","disk-24.png","disk-32.png","disk-64.png","disk-128.png","disk-256.png" }
)
public class RmDiskServiceApplication extends FXApplication {

    private FileLock applicationLock;

    @Override
    public void onConfig(EnvironmentLoader environmentLoader) {
        environmentLoader.withProvider(EMFactory.class);
    }

    @Override
    public void onStarted(DependencyContext dependencyContext) {

        FXResources resources = dependencyContext.getByClass(FXResources.class);

        try {
            Path lock = Path.of(resources.getAssetsFolder().getAbsolutePath(),".lock");
            if (!Files.exists(lock)) {
                Files.createFile(lock);
            }
            RandomAccessFile file = new RandomAccessFile(lock.toAbsolutePath().toString(),"rw");
            applicationLock = file.getChannel().tryLock();
            if (applicationLock == null || !applicationLock.isValid() || applicationLock.isShared()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText(resources.getResourceBundle().getString(LanguageKeys.SERVER_ALREADY_EXIST));
                alert.showAndWait();
                Platform.exit();
                return;
            }
        } catch (Exception e) {
            Platform.exit();
            return;
        }

        EMFProviderFactory factory = dependencyContext.getByClass(EMFProviderFactory.class);
        factory.create();

        DiskStartView diskStartView = dependencyContext.getByClass(DiskStartView.class);

        SystemTray tray = SystemTray.getSystemTray();
        try {

            TrayView trayView = dependencyContext.getByClass(TrayView.class);

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

    @Override
    public void onShutdown(DependencyContext context) {
        if (applicationLock != null) {
            try {
                applicationLock.release();
            } catch (Exception ignore) {
            }
        }
    }
}
