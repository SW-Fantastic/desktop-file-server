package org.swdc.rmdisk.service.events;

import org.swdc.dependency.event.AbstractEvent;
import org.swdc.rmdisk.core.entity.User;

public class UserStateChangeEvent extends AbstractEvent {

    public UserStateChangeEvent(User message) {
        super(message);
    }

    public User getUser() {
        return this.getMessage();
    }

}
