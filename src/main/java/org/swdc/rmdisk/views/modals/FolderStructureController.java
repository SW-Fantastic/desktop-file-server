package org.swdc.rmdisk.views.modals;

import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.swdc.dependency.annotations.Prototype;
import org.swdc.fx.FXResources;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.entity.TemplateFolder;
import org.swdc.rmdisk.core.entity.UserGroup;
import org.swdc.rmdisk.service.TemplateService;
import org.swdc.rmdisk.views.cells.FolderStructureCell;

import java.util.List;
import java.util.ResourceBundle;

@Prototype
public class FolderStructureController extends ViewController<FolderStructureView> {

    @Inject
    private TemplateService templateService;

    @Inject
    private Fontawsome5Service fontawsome5Service;

    @Inject
    private FXResources resources;

    @FXML
    private TreeView<TemplateFolder> folderTree;

    private SimpleBooleanProperty itemEditDisabled = new SimpleBooleanProperty(true);

    public void initFolders() {

        ResourceBundle bundle = resources.getResourceBundle();

        UserGroup group = getView().getGroup();
        TemplateFolder root = templateService.getRoot(group.getId());
        TreeItem<TemplateFolder> rootNode = new TreeItem<>(root);
        reverseLoadTemplateFolders(rootNode, root);
        folderTree.setRoot(rootNode);
        folderTree.setShowRoot(false);
        folderTree.setCellFactory(tree -> new FolderStructureCell(fontawsome5Service));
        folderTree.getSelectionModel().selectedItemProperty().addListener(this::folderSelected);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem itemAddFolder = new MenuItem(bundle.getString(LanguageKeys.SERVER_DLG_FOLDER_TEMPLATE_MENU_ADD_ROOT));
        itemAddFolder.setOnAction(this::onActionAddFolder);

        MenuItem itemAddSubFolder = new MenuItem(bundle.getString(LanguageKeys.SERVER_DLG_FOLDER_TEMPLATE_MENU_ADD_FOLDER));
        itemAddSubFolder.disableProperty().bind(itemEditDisabled);
        itemAddSubFolder.setOnAction(this::onActionAddSubFolder);

        MenuItem itemEditFolder = new MenuItem(bundle.getString(LanguageKeys.SERVER_DLG_FOLDER_TEMPLATE_MENU_RENAME));
        itemEditFolder.disableProperty().bind(itemEditDisabled);
        itemEditFolder.setOnAction(this::onActionEditFolder);

        MenuItem itemDeleteFolder = new MenuItem(bundle.getString(LanguageKeys.SERVER_DLG_FOLDER_TEMPLATE_MENU_REMOVE));
        itemDeleteFolder.disableProperty().bind(itemEditDisabled);
        itemDeleteFolder.setOnAction(this::onActionDeleteFolder);

        ObservableList<MenuItem> menuItems = contextMenu.getItems();
        menuItems.addAll(
                itemAddFolder,
                new SeparatorMenuItem(),
                itemAddSubFolder,
                itemEditFolder,
                itemDeleteFolder
        );

        folderTree.setContextMenu(contextMenu);

    }

    private void folderSelected(Observable observable) {
        TreeItem<TemplateFolder> selectedNode = folderTree.getSelectionModel().getSelectedItem();
        if (selectedNode != null) {
            itemEditDisabled.set(false);
        } else {
            itemEditDisabled.set(true);
        }
    }


    private void refreshFolder() {
        TreeItem<TemplateFolder> rootNode = folderTree.getRoot();
        TemplateFolder root = templateService.getRoot(getView().getGroup().getId());
        reverseLoadTemplateFolders(rootNode, root);
    }

    private void reverseLoadTemplateFolders(TreeItem<TemplateFolder> parent, TemplateFolder folder) {
        if (folder == null || parent == null) {
            return;
        }
        parent.getChildren().clear();
        List<TemplateFolder> children = templateService.getTemplateFolders(folder.getId());
        for (TemplateFolder child : children) {
            TreeItem<TemplateFolder> node = new TreeItem<>(child);
            parent.getChildren().add(node);
            reverseLoadTemplateFolders(node, child);
        }
    }

    private void onActionDeleteFolder(ActionEvent event) {
        TreeItem<TemplateFolder> selectedNode = folderTree.getSelectionModel().getSelectedItem();
        if (selectedNode == null) {
            return;
        }
        if (templateService.trashTemplateFolder(selectedNode.getValue().getId())) {
            refreshFolder();
        }
    }

    private void onActionEditFolder(ActionEvent event) {

        TreeItem<TemplateFolder> selectedNode = folderTree.getSelectionModel().getSelectedItem();
        if (selectedNode == null) {
            return;
        }
        TemplateFolder folder = selectedNode.getValue();
        EditTemplateView templateView = getView().getView(EditTemplateView.class);
        String text = templateView.showDialog(folder);
        if (text == null || text.isBlank()) {
            return;
        }
        templateService.renameTemplateFolder(folder.getId(), text);
        refreshFolder();

    }

    private void onActionAddSubFolder(ActionEvent event) {
        TreeItem<TemplateFolder> selectedNode = folderTree.getSelectionModel().getSelectedItem();
        if (selectedNode == null) {
            return;
        }
        EditTemplateView templateView = getView().getView(EditTemplateView.class);
        String text = templateView.showDialog(null);
        if (text == null || text.isBlank()) {
            return;
        }
        TemplateFolder folder = selectedNode.getValue();
        templateService.addTemplateFolder(folder.getId(), text);
        refreshFolder();
    }

    @FXML
    private void onActionAddFolder(ActionEvent event) {

        FolderStructureView view = getView();
        EditTemplateView templateView = view.getView(EditTemplateView.class);
        String text = templateView.showDialog(null);
        if (text == null || text.isBlank()) {
            return;
        }

        TemplateFolder root = templateService.getRoot(view.getGroup().getId());
        templateService.addTemplateFolder(root.getId(), text);
        refreshFolder();

    }

}
