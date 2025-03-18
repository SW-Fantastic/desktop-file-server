package org.swdc.rmdisk.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;
import org.swdc.rmdisk.core.ServerConfigure;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Base64;

@Singleton
public class CommonService {

    @Inject
    private ServerConfigure serverConfigure;

    @Inject
    private FXResources resources;

    @Inject
    private Logger logger;

    private byte[] defaultAvatar;

    public byte[] getDefaultAvatar() {
        if (defaultAvatar != null) {
            return defaultAvatar;
        }
        File assetFolder = resources.getAssetsFolder();
        File defaultAvatar = new File(assetFolder.getAbsolutePath() + File.separator + "avatar.png");
        try(FileInputStream fis = new FileInputStream(defaultAvatar)) {
            this.defaultAvatar = fis.readAllBytes();
        } catch (Exception e) {
            logger.error("Failed to load default avatar", e);
            return null;
        }
        return this.defaultAvatar;
    }

    public void setDefaultAvatar(byte[] avatar) {
        this.defaultAvatar = avatar;
        File assetFolder = resources.getAssetsFolder();
        File defaultAvatar = new File(assetFolder.getAbsolutePath() + File.separator + "avatar.png");
        try {
            Files.write(defaultAvatar.toPath(), avatar);
        } catch (Exception e) {
            logger.error("Failed to save default avatar", e);
        }
    }

}
