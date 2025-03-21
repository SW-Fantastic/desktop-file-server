package org.swdc.rmdisk.client.protocol;

/**
 * 重置密码的请求。
 * @param password 旧密码
 * @param newPassword 新密码
 */
public record StarWebResetPassword(String password, String newPassword) {
}
