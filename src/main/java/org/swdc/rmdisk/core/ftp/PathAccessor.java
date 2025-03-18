package org.swdc.rmdisk.core.ftp;

import java.io.File;
import java.nio.file.Path;

/**
 * 路径访问器，用于处理路径相关操作，获取标准格式的绝对路径。
 */
public class PathAccessor {

    private Path absolutePath;

    public PathAccessor(String workingDir, String path) {
        this.absolutePath = Path.of(workingDir)
                .resolve(path)
                .normalize();
    }

    private PathAccessor(Path path) {
        this.absolutePath = path;
    }

    public String asDirPath() {
        String curPath = absolutePath.toString()
                .replace(File.separator, "/");
        if (!curPath.endsWith("/")) {
            return curPath + "/";
        }
        return curPath;
    }

    public String getPath() {
        return absolutePath.toString()
                .replace(File.separator, "/");
    }

    public String getParentPath() {
        return absolutePath.getParent()
                .toString()
                .replace(File.separator, "/");
    }

    public PathAccessor getParent() {
        return new PathAccessor(absolutePath.getParent());
    }

}
