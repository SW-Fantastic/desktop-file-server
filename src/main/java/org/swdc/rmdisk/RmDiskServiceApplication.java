package org.swdc.rmdisk;

import org.swdc.data.EMFProviderFactory;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EnvironmentLoader;
import org.swdc.fx.FXApplication;
import org.swdc.fx.SWFXApplication;
import org.swdc.rmdisk.core.EMFactory;
import org.swdc.rmdisk.views.DiskStartView;

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
        diskStartView.show();

    }

}
