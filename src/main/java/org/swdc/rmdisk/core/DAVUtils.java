package org.swdc.rmdisk.core;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DAVUtils {

    /**
     * WebDAV的资源所，通常被包裹在圆括号和尖括号内，
     * 这个方法用于清理这些额外的字符。
     * @param rawLockToken 来自Header的LockTokenString
     * @return Lock的UUID
     */
    public static String getLockToken(String rawLockToken) {
        if (rawLockToken == null) {
            return null;
        }
        return rawLockToken
                .replace("(","")
                .replace(")", "")
                .replace("<","")
                .replace(">", "")
                .replace("urn:uuid:", "")
                .trim();
    }


    public static void deleteFolder(File dir) throws IOException {
        if (!dir.exists()) {
            return;
        }
        Files.walkFileTree(dir.toPath(), new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return null;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        Files.delete(dir.toPath());

    }


}
