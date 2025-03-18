package org.swdc.rmdisk.core;

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


}
