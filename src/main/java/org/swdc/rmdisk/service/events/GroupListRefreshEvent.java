package org.swdc.rmdisk.service.events;

import org.swdc.dependency.event.AbstractEvent;

public class GroupListRefreshEvent extends AbstractEvent {

    public GroupListRefreshEvent(Long groupId) {
        super(groupId);
    }

}
