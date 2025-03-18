package org.swdc.rmdisk.views;

import java.util.Arrays;
import java.util.List;

public interface ClientFileConstants {

    List<String> videoExts = Arrays.asList("mp4", "avi", "mov", "mkv", "flv", "wmv", "3gp", "mpg", "mpeg", "m4v");

    List<String> audioExts = Arrays.asList("mp3", "wav", "aac", "flac", "m4a", "ogg", "wma", "aiff");

    List<String> imageExts = Arrays.asList("png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif");

    List<String> archiveExts = Arrays.asList("zip", "rar", "tar");

    List<String> textExts = Arrays.asList("txt", "md");

    List<String> configExts = Arrays.asList("ini", "cfg", "xml", "properties", "conf", "inf", "json", "htaccess", "htpasswd", "yml", "yaml");

    List<String> webExts = Arrays.asList("html", "css", "php", "less", "sass", "scss", "htm", "hta", "xhtml");

    List<String> codeExts = Arrays.asList("java", "js", "c", "cc", "cpp", "h", "hpp", "cxx", "cs", "ts", "py", "swift", "go");

    List<String> libExts = Arrays.asList("dll", "so", "dylib", "framework", "class", "jar", "jmod", "pyo", "pyc", "pyd", "o", "a", "lib");

    List<String> executableExts = Arrays.asList("exe", "bat", "sh", "msi", "apk", "app", "deb");

    List<String> documentExts = Arrays.asList("pdf", "docx", "doc", "pptx", "ppt", "xlsx", "xls");

}
