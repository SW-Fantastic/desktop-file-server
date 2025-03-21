package org.swdc.rmdisk.client.protocol;

import org.swdc.ours.common.network.ByteHandler;
import org.swdc.ours.common.helper.ProgressListener;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.RemoteUser;

import java.io.File;
import java.net.ConnectException;
import java.util.List;
import java.util.function.Consumer;

public interface ClientFileProtocol {

    /**
     * 是否可以注册
     * @return
     */
    boolean registerable();

    /**
     * 登录到服务器
     * @param username 用户名
     * @param password 密码
     * @return token or session id on success
     */
    String login(String username, String password);


    /**
     * 获取用户信息
     *
     * @param tokenOrSessionId 用户认证信息（Token或SessionId）
     * @return 返回用户信息对象，若未找到用户信息则返回null
     */
    RemoteUser getUserInfo(String tokenOrSessionId) throws ConnectException;


    /**
     * 更新用户信息
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param user 用户信息对象，仅支持更新昵称、头像和用户名。 若为null则不进行任何操作。
     * @return 更新后的用户信息对象， 若未找到用户信息则返回null
     * @throws ConnectException 连接异常
     */
    RemoteUser updateUserInfo(String tokenOrSessionId, RemoteUser user) throws ConnectException;


    /**
     * 重置密码
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @throws ConnectException
     */
    RemoteUser resetPassword(String tokenOrSessionId, String passwordOld, String passwordNew) throws ConnectException;

    /**
     * 登出服务器
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @return 是否登出成功
     */
    boolean logout(String tokenOrSessionId) throws ConnectException;


    /**
     * 获取根目录
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @return 根目录
     */
    RemoteResource getRootFolder(String tokenOrSessionId) throws ConnectException;

    /**
     * 获取资源对象
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param path 资源路径
     * @return 远程资源对象
     * @throws ConnectException 连接异常
     */
    RemoteResource getResource(String tokenOrSessionId, String path) throws ConnectException;

    /**
     * 加载远程资源
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param path 资源路径
     * @return 异步加载资源对象
     * @throws ConnectException
     */
    void loadResourceContent(String tokenOrSessionId, String path, ByteHandler reader, Consumer<Void> then) throws ConnectException;

    /**
     * 上传资源
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param path 资源路径, 可以是文件夹路径，会自动追加文件名，也可以是包含文件名的完整路径。
     * @param target 上传的文件对象
     * @param then 上传完成后的回调
     * @throws ConnectException 连接异常
     */
    void uploadResource(String tokenOrSessionId, String path, File target, ProgressListener listener, Consumer<Void> then) throws ConnectException;

    /**
     * 重命名远程资源
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param folderPath 资源路径
     * @param newName 新名称
     * @return 资源对象
     */
    boolean renameResource(String tokenOrSessionId, String folderPath, String newName) throws ConnectException;

    /**
     * 删除远程资源
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param folderPath 资源路径
     * @return 是否删除成功
     */
    boolean trashResource(String tokenOrSessionId, String folderPath) throws ConnectException;


    /**
     * 创建文件夹
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param parentPath 父文件夹路径
     * @param newName 新文件夹名称
     * @return 文件夹对象
     */
    RemoteResource createFolder(String tokenOrSessionId, String parentPath, String newName) throws ConnectException;


    /**
     * 获取文件夹的内容
     * @param tokenOrSessionId 该协议需要的认证凭证
     * @param folderPath 文件夹路径
     * @return 资源列表
     */
    List<RemoteResource> getFolderContent(String tokenOrSessionId, String folderPath) throws ConnectException;


    String getUrl(String path);

}
