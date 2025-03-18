package org.swdc.rmdisk.views.events;

import org.swdc.dependency.event.AbstractEvent;

/**
 * 收到此消息，客户端应当刷新主界面的用户信息。
 */
public class ClientUserRefreshEvent extends AbstractEvent {
    public ClientUserRefreshEvent() {
        super(null);
    }
}
