package org.swdc.rmdisk.views;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.swdc.rmdisk.client.RemoteResource;

import java.util.List;

public interface ClientResourceView {

    ObservableList<RemoteResource> getResources();

    void clearSelection();

    void clear();

    RemoteResource getSelection();

    Node getView();

    void select(RemoteResource resource);

    ObjectProperty<ChangeListener<RemoteResource>> selectionChangedProperty();

    void setOnMouseClicked(EventHandler<MouseEvent> clickedListenerProperty);

}
