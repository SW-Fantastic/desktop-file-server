package org.swdc.rmdisk;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.data.EMFProviderFactory;
import org.swdc.dependency.AnnotationLoader;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.LoggerProvider;
import org.swdc.fx.FXResources;
import org.swdc.ours.common.helper.PlatformHelper;
import org.swdc.rmdisk.core.EMFactory;
import org.swdc.rmdisk.core.ManagedServerConfigure;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.service.FTPVertxService;
import org.swdc.rmdisk.service.HttpVertxService;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RmDiskHeadlessApplication {

    public static Logger logger = LoggerFactory.getLogger(RmDiskHeadlessApplication.class);

    public RmDiskHeadlessApplication() {

    }

    public void launch(String[] args) throws IOException {
        try {

            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options(),args);
            if (GraphicsEnvironment.isHeadless() || !commandLine.hasOption("cli")) {
                logger.warn("Command is only working with CLI mode, add -cli parameter or do not start with command line, exit");
                return;
            }

            if (!commandLine.hasOption("dav") && ( !commandLine.hasOption("ftp"))) {
                logger.warn("No service was configured, exit.");
            }

            // Load DI Environment
            DependencyContext context = loadResources(args).load();

            // Load JPA resource
            EMFProviderFactory factory = context.getByClass(EMFProviderFactory.class);
            factory.create();

            // init services with configs.
            if (commandLine.hasOption("dav")) {
                HttpVertxService httpVertxService = context.getByClass(HttpVertxService.class);
                httpVertxService.startService(v -> {
                    if (v) {
                        logger.info("WebDAV started");
                    }
                });
            }

            if (commandLine.hasOption("ftp")) {
                FTPVertxService ftpVertxService = context.getByClass(FTPVertxService.class);
                ftpVertxService.startService(v -> {
                    if (v) {
                        logger.info("FTP started.");
                    }
                });
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {

                if (commandLine.hasOption("dav")) {
                    HttpVertxService httpVertxService = context.getByClass(HttpVertxService.class);
                    httpVertxService.stopService(v -> {
                        if (v) {
                            logger.info("DAV Server stopped.");
                        }
                    });
                }

                if (commandLine.hasOption("ftp")) {
                    FTPVertxService ftpVertxService = context.getByClass(FTPVertxService.class);
                    ftpVertxService.stopService(v -> {
                        if (v) {
                            logger.info("FTP Server stopped.");
                        }
                    });
                }

            }));


        } catch (Exception e) {
            logger.error("Failed to read command line", e);
        }
    }

    private AnnotationLoader loadResources(String[] args) {

        FXResources resources = new FXResources();
        resources.setArgs(Arrays.asList(args));
        resources.setConfigures(Arrays.asList(
                RmDiskApplicationConfig.class,
                ServerConfigure.class,
                ManagedServerConfigure.class
        ));
        resources.setDefaultConfig(RmDiskApplicationConfig.class);
        resources.setAssetsFolder(PlatformHelper.getPlatformAssetFolder("./assets"));

        AnnotationLoader loader = new AnnotationLoader();
        loader.withProvider(EMFactory.class);
        loader.withProvider(LoggerProvider.class);
        loader.withInstance(FXResources.class,resources);
        loader.withInstance(ThreadPoolExecutor.class,  new ThreadPoolExecutor(4,12,30, TimeUnit.MINUTES,new ArrayBlockingQueue<>(6,true)));
        return loader;

    }

    private Options options(){

        Options option = new Options();
        option.addOption(new Option("cli","headless",false, "Cli mode, do not start GUI and create windows"));
        option.addOption(new Option("dav","webdav",false,"Enable dav service with ports on config file"));
        option.addOption(new Option("ftp","ftpserver",false,"Enable ftp service with ports on config file"));

        return option;

    }
}
