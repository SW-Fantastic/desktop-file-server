package org.swdc.rmdisk.client.protocol;

import org.swdc.ours.common.network.*;
import org.swdc.rmdisk.client.RemoteUser;
import org.swdc.rmdisk.core.dav.BarerLoginRequest;
import org.swdc.rmdisk.service.verticle.http.Resp;

import java.io.File;

public interface StarWebDAVClient {

    /**
     * 登录
     * @param request 登录请求
     * @return 登录成功时返回Token或者SessionId，失败时返回null
     */
    @EndPoint(method = Methods.POST, url = "/@auth/login")
    String login(@HttpBody BarerLoginRequest request);

    /**
     * 是否支持注册
     */
    @EndPoint(method = Methods.GET,url = "/@api/register")
    Resp registerSupport();

    /**
     * 获取用户信息
     * @return 用户信息
     */
    @EndPoint(method = Methods.GET, url = "/@api/user-info")
    Resp getUserInfo();


    /**
     * 获取子文件夹信息
     * @param folderPath 文件夹路径
     * @return 子文件夹信息，xml形式，一般是标准的DAV协议的MultipleState的Xml。
     */
    @HttpHeaders({
            @Header(key = "Depth", value = "1")
    })
    @EndPoint(method = Methods.PROPFIND, url = "{folderPath}")
    String getChildFolders(@Path("folderPath") String folderPath);

    /**
     * 创建文件夹
     * @param folderPath 文件夹路径
     */
    @EndPoint(method = Methods.MKCOL, url = "{folderPath}")
    void createFolder(@Path("folderPath") String folderPath);

    /**
     * 获取资源信息
     * @param resourcePath 文件夹路径
     * @return 文件夹信息，xml形式，一般是标准的DAV协议的MultipleState的Xml。
     */
    @HttpHeaders({
            @Header(key = "Depth", value = "0")
    })
    @EndPoint(method = Methods.PROPFIND, url = "{path}")
    String getResource(@Path("path") String resourcePath);


    /**
     * 获取资源内容
     * @param path 资源路径
     * @return 异步加载资源的对象，通过线程池加载资源的具体内容。
     */
    @EndPoint(method = Methods.GET, url = "{path}")
    NetworkAsync getResourceContent(@Path("path") String path);

    /**
     * 上传资源
     * @param path 资源路径，应当包含文件名
     * @param file 被上传的文件
     * @return 异步上传资源的对象，通过线程池上传资源的内容。
     */
    @EndPoint(method = Methods.PUT, url = "{path}")
    NetworkAsync uploadResource(@Path("path") String path, @HttpBody File file);


    /**
     * 删除资源
     * @param resourcePath 资源路径
     */
    @EndPoint(method = Methods.DELETE, url = "{path}")
    void deleteResource(@Path("path") String resourcePath);


    /**
     * 移动文件夹, 同时也是重命名文件夹。
     * @param resourcePath 资源路径，包含文件名
     * @param destination 目标路径，包含移动后的文件名
     */
    @EndPoint(method = Methods.MOVE, url = "{path}")
    void moveResource(
            @Path("path") String resourcePath,
            @Header(key = "Destination") String destination
    );


    @EndPoint(method = Methods.POST, url = "/@api/user-info")
    Resp updateUserInfo(@HttpBody RemoteUser user);

}
