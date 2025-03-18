package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.ViewController;
import org.swdc.ours.common.network.CancelledException;
import org.swdc.rmdisk.client.RemoteResource;
import org.swdc.rmdisk.client.protocol.ClientFileProtocol;
import org.swdc.rmdisk.views.cells.ClientUpDownloadCell;
import org.swdc.rmdisk.views.events.ClientUserRefreshEvent;
import org.swdc.rmdisk.views.modals.ClientNameEditModal;
import org.swdc.rmdisk.views.modals.ClientPropertiesModal;
import org.swdc.rmdisk.views.modals.ClientUserProfileModal;
import org.swdc.rmdisk.views.modals.UpDownloadTask;
import org.swdc.rmdisk.views.previews.ClientPreviewViewFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.*;


@Singleton
public class ClientMainController extends ViewController<ClientMainView> {

    @Inject
    private Logger logger;

    @Inject
    private List<ClientPreviewViewFactory> previewViews;

    @Inject
    private Fontawsome5Service fontawsome5Service;

    @FXML
    private TextField pathField;

    private RemoteResource currentFolder;

    /**
     * 客户端文件协议，用于与服务器交互，通过http的远程调用的方式
     * 访问远程的文件夹结构，文件等。
     */
    private ClientFileProtocol clientFiles;

    /**
     * 用户登录成功后，服务器返回的token。
     */
    private String token;


    // 历史记录栈，用于后退和前进操作。
    private Deque<RemoteResource> history = new ArrayDeque<>();

    private Deque<RemoteResource> forward = new ArrayDeque<>();

    private static final int MAX_HISTORY_DEPTH = 10;

    // 右键菜单，用于文件和文件夹的操作。
    private ContextMenu fileViewContextMenu = new ContextMenu();

    private ContextMenu folderViewContextMenu = new ContextMenu();

    private SimpleBooleanProperty selectionDisabled = new SimpleBooleanProperty(true);

    // 用于展示下载文件进度的弹出框。
    private PopOver downloadPopover = null;

    // 下载任务列表，用于展示正在下载的文件。
    private ListView<UpDownloadTask> downloadTaskListView;

    /**
     * 视图准备就绪时调用的方法，用于初始化视图组件。
     *
     * @param url 视图对应的资源文件URL
     * @param resourceBundle 视图对应的资源文件
     */
    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        pathField.setDisable(true);
        initContextMenu();
        initFileContextMenu();
        downloadPopover = new PopOver();
        downloadPopover.getStyleClass().add("task-popover");
        downloadPopover.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);

        downloadTaskListView = new ListView<>();
        downloadTaskListView.setMinSize(400,280);
        downloadTaskListView.setPrefSize(400,280);
        downloadTaskListView.setCellFactory(v ->
                new ClientUpDownloadCell(getView(), fontawsome5Service)
        );
        downloadPopover.setContentNode(downloadTaskListView);

        ClientMainView view = getView();
        Button btnDownload = view.findById("btnDownload");
        btnDownload.setOnAction(e -> downloadPopover.show(btnDownload));

    }


    private void initFileContextMenu() {

        fileViewContextMenu.setAutoHide(true);

        MenuItem itemOpen = new MenuItem("打开");
        itemOpen.disableProperty().bind(selectionDisabled);
        MenuItem itemRename = new MenuItem("重命名");
        itemRename.disableProperty().bind(selectionDisabled);
        itemRename.setOnAction(this::renameResource);
        MenuItem itemDelete = new MenuItem("删除");
        itemDelete.setOnAction(this::trashResource);
        itemDelete.disableProperty().bind(selectionDisabled);
        MenuItem itemProperty = new MenuItem("属性");
        itemProperty.setOnAction(this::showProperties);
        itemProperty.disableProperty().bind(selectionDisabled);

        MenuItem itemDownload = new MenuItem("下载至...");
        itemDownload.setOnAction(this::downloadResource);

        fileViewContextMenu.getItems().addAll(
                itemOpen,
                itemRename,
                itemDelete,
                new SeparatorMenuItem(),
                itemProperty,
                itemDownload
        );

    }

    /**
     * 初始化上下文菜单
     *
     * 该方法初始化文件夹视图的上下文菜单，包括上传、打开、新建文件夹、刷新、重命名、删除和属性等选项。
     */
    private void initContextMenu() {

        folderViewContextMenu.setAutoHide(true);

        MenuItem itemUpload = new MenuItem("上传");
        itemUpload.setOnAction(this::uploadResource);

        MenuItem itemOpen = new MenuItem("打开");
        itemOpen.setOnAction(this::openFolder);
        itemOpen.disableProperty().bind(selectionDisabled);

        MenuItem itemCreateFolder = new MenuItem("新建文件夹");
        itemCreateFolder.setOnAction(this::addFolder);

        MenuItem itemRefresh = new MenuItem("刷新");
        itemRefresh.setOnAction(e -> refreshFolder());

        MenuItem itemRename = new MenuItem("重命名");
        itemRename.setOnAction(this::renameResource);
        itemRename.disableProperty().bind(selectionDisabled);

        MenuItem itemDelete = new MenuItem("删除");
        itemDelete.setOnAction(this::trashResource);
        itemDelete.disableProperty().bind(selectionDisabled);

        MenuItem itemProperty = new MenuItem("属性");
        itemProperty.disableProperty().bind(selectionDisabled);
        itemProperty.setOnAction(this::showProperties);

        ObservableList<MenuItem> items = folderViewContextMenu.getItems();
        items.addAll(
                itemOpen,
                itemCreateFolder,
                itemRefresh,
                itemUpload,
                new SeparatorMenuItem(),
                itemRename,
                itemDelete,
                itemProperty
        );

    }


    /**
     * 初始化用户的信息，并更新文件夹的TreeView，
     * 该方法应该在用户登录成功后被调用。
     *
     * @param token 用户的Token，用于后续的远程调用。
     * @param clientFiles 客户端文件协议，用于与服务器交互。
     */
    public void update(String token, ClientFileProtocol clientFiles) {

        this.token = token;
        this.clientFiles = clientFiles;
        try {

            this.currentFolder = this.clientFiles.getRootFolder(token);
            toFolder(this.currentFolder);
        } catch (ConnectException e) {
            serverDisconnected();
            return;
        }

        ClientResourceView view = getView().getCurrentView();
        view.setOnMouseClicked(e -> {

            folderViewContextMenu.hide();
            fileViewContextMenu.hide();

            RemoteResource resource = view.getSelection();

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                if (resource == null) {
                    return;
                }
                if (resource.isFolder()) {
                    if (history.size() > MAX_HISTORY_DEPTH) {
                        history.removeLast();
                    }
                    forward.clear();
                    history.push(this.currentFolder);
                    toFolder(resource);
                } else {
                   openFile(resource);
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                selectionDisabled.set(resource == null);
                if (resource == null || resource.isFolder()) {
                    folderViewContextMenu.show(view.getView(), e.getScreenX(), e.getScreenY());
                } else {
                    fileViewContextMenu.show(view.getView(), e.getScreenX(), e.getScreenY());
                }
            }

        });


    }

    @FXML
    public void openProfile() {

        ClientMainView clientMainView = getView();
        ClientUserProfileModal modal = clientMainView.getView(ClientUserProfileModal.class);
        modal.show(clientFiles,token);

        try {
            clientMainView.updateInfo(clientFiles.getUserInfo(token));
        } catch (ConnectException e) {
            serverDisconnected();
        }

    }

    /**
     * 刷新当前文件夹
     *
     * @throws Exception 当与服务器断开连接时抛出异常
     */
    @FXML
    public void refreshFolder() {
        try {
            toFolder(this.currentFolder);
        } catch (Exception e) {
            serverDisconnected();
        }
    }

    /**
     * 显示资源的属性信息
     *
     * @param event 事件对象
     */
    private void showProperties(ActionEvent event) {

        ClientMainView view = getView();
        ClientResourceView resourceView = view.getCurrentView();
        RemoteResource resource = resourceView.getSelection();
        if (resource == null) {
            return;
        }
        ClientPropertiesModal modal = view.getView(ClientPropertiesModal.class);
        modal.show(resource);

    }

    /**
     * 上传资源文件
     *
     * @param event 事件对象
     */
    private void uploadResource(ActionEvent event) {
        ClientMainView view = getView();
        RemoteResource currentFolder = this.currentFolder;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("上传到 " + currentFolder.getName());
        List<File> files = chooser.showOpenMultipleDialog(view.getStage());
        if (files == null) {
            return;
        }
        try {
            for (File file : files) {
                if (!file.exists()) {
                    continue;
                }
                String targetPath = currentFolder.getPath();
                if (!targetPath.endsWith("/")) {
                    targetPath += "/";
                }
                targetPath = targetPath + file.getName();
                RemoteResource resource = clientFiles.getResource(token,targetPath);
                if (resource != null) {
                    Alert alert = view.alert("文件已存在", "目标位置已经有一个 < " + file.getName() + " > 文件，是否覆盖？", Alert.AlertType.CONFIRMATION);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        continue;
                    }
                }

                UpDownloadTask task = new UpDownloadTask(
                        UpDownloadTask.TaskType.UPLOAD,
                        file,
                        file.length()
                );
                ObservableList<UpDownloadTask> tasks = downloadTaskListView.getItems();
                tasks.add(task);
                final String uploadTargetPath = targetPath;
                clientFiles.uploadResource(token,uploadTargetPath,file,(direction, progress, length) -> {
                    if (task.isCancelled()) {
                        throw new CancelledException("upload task is cancelled");
                    }
                    task.updateProgress(progress);
                }, v -> {
                    Platform.runLater(() -> {
                        if (currentFolder.getPath().equals(this.currentFolder.getPath())) {
                            toFolder(this.currentFolder);
                        }
                        if (task.isCancelled()) {
                            try {
                                clientFiles.trashResource(token,uploadTargetPath);
                            } catch (ConnectException e) {
                                serverDisconnected();
                            }
                        }
                        tasks.remove(task);
                    });
                });
            }
        } catch (ConnectException e) {
            serverDisconnected();
        }
    }

    private void downloadResource(ActionEvent event) {
        ClientResourceView view = getView().getCurrentView();
        RemoteResource resource = view.getSelection();
        if (resource == null) {
            return;
        }
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("保存到 " + resource.getName());
        File dir = chooser.showDialog(getView().getStage());
        if (dir == null) {
            return;
        }

        File file = new File(dir, resource.getName());
        if (file.exists()) {
            Alert alert = getView().alert("文件已存在", "目标位置已经有一个同名文件，是否覆盖？", Alert.AlertType.CONFIRMATION);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        try {
            UpDownloadTask task = new UpDownloadTask(
                    UpDownloadTask.TaskType.DOWNLOAD,
                    file,
                    resource.getContentSize()
            );
            ObservableList<UpDownloadTask> tasks = downloadTaskListView.getItems();
            tasks.add(task);
            FileOutputStream fos = new FileOutputStream(file);
            clientFiles.loadResourceContent(token, resource.getPath(), (buf, length) -> {
                if (task.isCancelled()) {
                    throw new CancelledException("download task is cancelled");
                }
                try {
                    fos.write(buf, 0, length);
                    task.updateProgress(length);
                } catch (Exception e) {
                    logger.error("failed to write a buffer to file ", e);
                }
            }, v -> {
                try {
                    fos.close();
                    if (task.isCancelled()) {
                        file.delete();
                    }
                   // tasks.remove(task);
                } catch (Exception e) {
                    logger.error("failed to close file ", e);
                }
            });
        } catch (IOException ex) {
            logger.error("failed to download file ", ex);
        }

    }

    private void openFolder(ActionEvent event) {
        ClientResourceView view = getView().getCurrentView();
        RemoteResource resource = view.getSelection();
        if (resource == null || !resource.isFolder()) {
            return;
        }
        toFolder(resource);
    }

    private void addFolder(ActionEvent event) {
        ClientNameEditModal editModal = getView().getView(ClientNameEditModal.class);
        String name = editModal.show(null);
        if (name == null || name.isEmpty()) {
            return;
        }
        try {
            RemoteResource resource = clientFiles.createFolder(token, currentFolder.getPath(),name);
            if (resource != null) {
                refreshFolder();
            } else {
                Alert alert = getView().alert("创建文件夹失败", "无法在服务器上创建文件夹。", Alert.AlertType.ERROR);
                alert.showAndWait();
            }
        } catch (ConnectException e) {
            serverDisconnected();
        }
    }

    private void renameResource(ActionEvent event) {
        ClientResourceView view = getView().getCurrentView();
        RemoteResource resource = view.getSelection();
        if (resource == null) {
            return;
        }
        try {
            ClientNameEditModal editModal = getView().getView(ClientNameEditModal.class);
            String newName = editModal.show(resource);
            if (newName == null || newName.isEmpty()) {
                return;
            }
            if(clientFiles.renameResource(token, resource.getPath(), newName)) {
                refreshFolder();
            } else {
                Alert alert = getView().alert("重命名失败", "无法在服务器上重命名指定资源。", Alert.AlertType.ERROR);
                alert.showAndWait();
            }
        } catch (ConnectException e) {
            serverDisconnected();
        }
    }

    private void trashResource(ActionEvent event) {
        try {
            ClientResourceView view = getView().getCurrentView();
            RemoteResource resource = view.getSelection();
            if (resource == null) {
                return;
            }

            Alert alertConfirm = getView().alert("确认删除", "你确定要删除《" + resource.getName() + "》吗？这将无法恢复。", Alert.AlertType.CONFIRMATION);
            Optional<ButtonType> buttonType = alertConfirm.showAndWait();
            if (buttonType.isEmpty()) {
                return;
            }
            ButtonType button = buttonType.get();
            if (button == ButtonType.OK ) {
                boolean result = clientFiles.trashResource(token, resource.getPath());
                if (result) {
                    refreshFolder();
                } else {
                    Alert alert = getView().alert("删除失败", "无法在服务器上删除指定资源。", Alert.AlertType.ERROR);
                    alert.showAndWait();
                }
            }

        } catch (ConnectException e) {
            serverDisconnected();
        }
    }

    private void openFile(RemoteResource resource) {

        if (resource == null || resource.isFolder()) {
            return;
        }
        for (ClientPreviewViewFactory previewView: previewViews) {
            if (previewView.support(resource)) {
                Stage stage = previewView.createView(clientFiles,token,resource);
                stage.showAndWait();
                return;
            }
        }
    }

    private void toFolder(RemoteResource folder){

        try {
            List<RemoteResource> resources = clientFiles.getFolderContent(token, folder.getPath());

            ClientMainView view = getView();
            ClientResourceView currentView = view.getCurrentView();
            currentView.clearSelection();
            currentView.clear();

            ObservableList<RemoteResource> remoteResources = currentView.getResources();
            for (RemoteResource resource : resources) {
                if (resource.getPath().equals(folder.getPath())) {
                    continue;
                }
                remoteResources.add(resource);
            }

            this.currentFolder = folder;
            refreshFolderPath();

            ClientMainView clientMainView = getView();
            clientMainView.updateInfo(clientFiles.getUserInfo(token));

            Button toUpper = clientMainView.findById("btnUpperLevel");
            if (currentFolder.getPath().equals("/")) {
                toUpper.setDisable(true);
            } else {
                toUpper.setDisable(false);
            }

            Button goBack = clientMainView.findById("btnGoBack");
            goBack.setDisable(history.isEmpty());

            Button goForward = clientMainView.findById("btnGoForward");
            goForward.setDisable(forward.isEmpty());

        } catch (ConnectException e) {
            serverDisconnected();
        }

    }

    public void serverDisconnected() {
        this.token = null;
        this.clientFiles = null;
        ClientMainView view = getView();
        Alert alert = view.alert("网络错误", "无法连接到服务器，请重新登录。", Alert.AlertType.ERROR);
        alert.showAndWait();
        view.hide();
    }


    private void refreshFolderPath() {
        pathField.setText(currentFolder.getPath());
    }

    public boolean isLoggedIn() {
        try {
            if(token != null && !token.isBlank() && clientFiles != null) {
                clientFiles.getUserInfo(token);
                return true;
            }
            return false;
        } catch (ConnectException e) {
            serverDisconnected();
            return false;
        }
    }

    @FXML
    public void toUpperLevel() {
        if(currentFolder.getPath().equals("/")){
            toFolder(currentFolder);
            return;
        }
        try {
            String parentPath = currentFolder.getPath().substring(0, currentFolder.getPath().lastIndexOf("/"));
            RemoteResource resource = clientFiles.getResource(token, parentPath);
            if (resource.isFolder()) {
                forward.push(this.currentFolder);
                history.clear();
                toFolder(resource);
            }
        } catch (ConnectException e) {
            serverDisconnected();
        }

    }

    @FXML
    public void goBack() {
        if(!history.isEmpty()) {
            forward.push(this.currentFolder);
            toFolder(history.pop());
        }
    }

    @FXML
    private void goForward() {
        if(!forward.isEmpty()) {
            history.push(this.currentFolder);
            toFolder(forward.pop());
        }
    }

    @FXML
    private void logout() {
        getView().hide();
        this.clientFiles = null;
        this.token = null;
    }

}
