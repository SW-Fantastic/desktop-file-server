package org.swdc.rmdisk.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.swdc.ours.common.type.JSONMapper;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CoreSecurityKey {

    private String key;

    private String iv;

    @JsonIgnore
    private SecretKey secretKey;

    public String getIv() {
        return iv;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public SecretKey getSecretKey() throws Exception {

        if (key == null || key.isBlank() || iv == null || iv.isBlank()) {
            return null;
        }

        if (secretKey != null) {
            return secretKey;
        }

        this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        return secretKey;

    }

    public void generateAndSave(File target) throws Exception {

        SecureRandom random = new SecureRandom();
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256,random);

        SecretKey secretKey = generator.generateKey();
        byte[] iv = random.generateSeed(16);

        this.key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        this.iv = Base64.getEncoder().encodeToString(iv);

        Files.write(target.toPath(), JSONMapper.writeBytes(this));

    }

}
