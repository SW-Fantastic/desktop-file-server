package org.swdc.rmdisk.views.previews;


import javafx.stage.Stage;
import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;

/**
 * 客户端预览视图接口。
 * 这个接口实质是一个Factory，用于创建不同类型的预览视图。
 */
@ImplementBy({
        ClientTextPreviewFactory.class,
        ClientImagePreviewFactory.class,
        ClientAudioPreviewFactory.class,
        ClientVideoPreviewFactory.class,
})
public interface ClientPreviewViewFactory {


    /**
     * 判断本预览视图是否支持给定的远程资源。
     *
     * @param resource 远程资源
     * @return 如果支持返回 true，否则返回 false
     */
    boolean support(RemoteResource resource);

    /**
     * 创建预览视图。
     * @param protocol 客户端协议
     * @param resource 远程资源
     * @return 预览视图的窗口对象
     */
    Stage createView(ClientFileProtocol protocol, String token, RemoteResource resource);

}
