package org.swdc.rmdisk.views;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.GridView;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.views.cells.ClientFileGridCell;

import java.util.List;
import java.util.Optional;

public class ClientGridView implements ClientResourceView {

    private GridView<RemoteResource> filesGridView;

    private ObservableList<RemoteResource> selectedFiles = FXCollections.observableArrayList();

    private SimpleObjectProperty<ChangeListener<RemoteResource>> selectListenerProperty = new SimpleObjectProperty<>();

    private SimpleObjectProperty<EventHandler<MouseEvent>> clickedListenerProperty = new SimpleObjectProperty<>();

    private SimpleObjectProperty<RemoteResource> cellSelectedProperty = new SimpleObjectProperty<>();

    private SimpleBooleanProperty itemClicked = new SimpleBooleanProperty();

    public ClientGridView(Fontawsome5Service fontawsome5Service) {

        filesGridView = new GridView<>();
        filesGridView.setCellHeight(80);
        filesGridView.setCellWidth(80);
        filesGridView.setPadding(new Insets(12));
        filesGridView.setHorizontalCellSpacing(4);
        filesGridView.setVerticalCellSpacing(4);
        filesGridView.getStyleClass().add("gridView");
        filesGridView.setCellFactory(param -> new ClientFileGridCell(fontawsome5Service, cellSelectedProperty,itemClicked));
        filesGridView.setOnMouseClicked(e -> {
            if (!itemClicked.get()) {
                cellSelectedProperty.set(null);
            }
            itemClicked.set(false);
            if (clickedListenerProperty.get() != null) {
                clickedListenerProperty.get().handle(e);
            }
        });

    }

    @Override
    public ObservableList<RemoteResource> getResources() {
        return filesGridView.getItems();
    }

    @Override
    public void clearSelection() {
        selectedFiles.clear();
    }

    @Override
    public void clear() {
        filesGridView.getItems().clear();
    }

    @Override
    public RemoteResource getSelection() {
        return cellSelectedProperty.get();
    }

    @Override
    public Node getView() {
        return filesGridView;
    }

    @Override
    public void select(RemoteResource resource) {
        Optional<RemoteResource> selected = getResources().stream()
                .filter(r -> r.getPath().equals(resource.getPath()))
                .findFirst();
        if (selected.isPresent()) {
            selectedFiles.add(selected.get());
        }
    }

    @Override
    public ObjectProperty<ChangeListener<RemoteResource>> selectionChangedProperty() {
        return selectListenerProperty;
    }

    @Override
    public void setOnMouseClicked(EventHandler<MouseEvent> clickedListenerProperty) {
        this.clickedListenerProperty.set(clickedListenerProperty);
    }
}
