package org.swdc.rmdisk.views.events;

import org.swdc.dependency.event.AbstractEvent;

public class ClientLogoutEvent extends AbstractEvent {

    public ClientLogoutEvent(String text) {
        super(text);
    }


}
