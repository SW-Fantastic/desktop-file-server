package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PopOver;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.entity.State;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.entity.UserGroup;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.UserManageService;
import org.swdc.rmdisk.views.cells.GroupListCell;
import org.swdc.rmdisk.views.cells.OnlineTableCell;
import org.swdc.rmdisk.views.cells.StateTableCell;
import org.swdc.rmdisk.views.cells.UsableSpaceTableCell;
import org.swdc.rmdisk.views.common.DateRangePopover;
import org.swdc.rmdisk.views.common.Pagination;
import org.swdc.rmdisk.service.events.UserStateChangeEvent;
import org.swdc.rmdisk.views.modals.EditGroupView;
import org.swdc.rmdisk.views.modals.EditUserView;
import org.swdc.rmdisk.views.modals.FolderStructureView;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Singleton
public class ServerUserTabController extends ViewController<ServerUserTabView> {

    /**
     * 用户组列表，用于显示用户组，用户组可以分为正常状态和注销状态
     * 两种状态的用户组通过ToggleButton进行切换
     */
    @FXML
    private ListView<UserGroup> groupListView;

    /**
     * 用户表格，用于显示用户信息
     */
    @FXML
    private TableView<User> userTableView;

    /**
     * 用户表的Id列，用于显示用户的Id
     */
    @FXML
    private TableColumn<User,Long> idColumn;

    /**
     * 用户表的用户名列，用于显示用户的用户名
     */
    @FXML
    private TableColumn<User,String> usernameColumn;

    /**
     * 用户的昵称列，用于显示用户的昵称
     */
    @FXML
    private TableColumn<User,String> nameColumn;

    /**
     * 用户表的状态列，用于显示用户的状态
     * 用户的状态可以是正常状态，注销状态，用户被删除后将会处于注销状态
     * 此时的用户可以被彻底删除。
     */
    @FXML
    private TableColumn<User,Void> stateColumn;

    /**
     * 用户表的可用空间列，用于显示用户的可用空间
     */
    @FXML
    private TableColumn<User, Void> spaceColumn;

    /**
     * 用户表的在线状态列，用于显示用户是否在线
     * 在线指的是用户已经通过认证，获取到Token。
     */
    @FXML
    private TableColumn<User,Void> onlineColumn;

    /**
     * 用户组标签，用于显示当前浏览的用户组的名称
     */
    @FXML
    private Label groupLabel;


    /**
     * 按注册日期过滤
     */
    @FXML
    private TextField dateFilterField;

    /**
     * 用于按用户名搜索用户的搜索框
     */
    @FXML
    private TextField searchField;

    /**
     * 用户组的右键菜单的禁用状态，当没有用户组被选中时或者正在浏览被删除的组时，
     * 将会通过本属性禁用部分右键菜单
     */
    private SimpleBooleanProperty selectionGroupDisabled = new SimpleBooleanProperty(true);

    /**
     * 用户组的右键菜单的彻底删除状态，当没有用户组被选中时或者正在浏览普通的用户组时，
     * 将会通过本属性禁用彻底删除菜单。
     */
    private SimpleBooleanProperty selectionGroupPurgeDisabled = new SimpleBooleanProperty(true);

    /**
     * 用户组的右键菜单的可注册状态
     */
    private SimpleBooleanProperty selectionGroupRegisterable = new SimpleBooleanProperty(false);

    /**
     * 用户的右键菜单的禁用状态，当没有用户被选中时将会通过本属性禁用部分右键菜单
     */
    private SimpleBooleanProperty selectionUserDisabled = new SimpleBooleanProperty(true);

    /**
     * 用户的右键菜单的彻底删除状态，当没有用户被选中时或者用户处于普通状态时将会通过本属性禁用彻底删除菜单。
     */
    private SimpleBooleanProperty selectionUserPurgeDisabled = new SimpleBooleanProperty(true);

    private DateRangePopover filterDateRangePopover;

    private Pagination pagination;

    @Inject
    private Fontawsome5Service fontawsome5Service;

    @Inject
    private SecureService secureService;

    @Inject
    private UserManageService userManageService;

    @Inject
    private FXResources resources;

    private volatile boolean userTableRefreshing = false;

    private static final int RECORD_COUNT_PRE_PAGE = 80;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        ServerUserTabView view = getView();
        ToggleGroup group = view.getUserGroupToggle();
        group.selectedToggleProperty().addListener(this::groupStateChanged);

        initGroupContextMenu();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        spaceColumn.setCellFactory(val -> new UsableSpaceTableCell());
        stateColumn.setCellFactory(col -> new StateTableCell(resourceBundle,fontawsome5Service));
        onlineColumn.setCellFactory(col -> new OnlineTableCell(fontawsome5Service,secureService));

        initUserContextMenu();
        userTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::userSelectionChanged);


        groupListView.setPadding(new Insets(8,0,8,0));
        groupListView.setCellFactory(lv -> new GroupListCell());
        groupListView.getSelectionModel().selectedItemProperty()
                .addListener(this::userGroupChange);

        filterDateRangePopover = new DateRangePopover(resourceBundle);
        filterDateRangePopover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        dateFilterField.setOnMouseClicked(
                event -> filterDateRangePopover.show(dateFilterField)
        );
        filterDateRangePopover.dateRangeProperty()
                .addListener(observable -> refreshUserTable());
        searchField.textProperty()
                .addListener(obs -> refreshUserTable());

        selectionGroupRegisterable.addListener(this::onGroupRegisterStateChanged);

        initPagination();
    }

    private void initPagination() {
        pagination = new Pagination();
        HBox paginationPane = getView().findById("pagination-pane");
        paginationPane.getChildren().add(pagination);
        pagination.currentPageProperty().addListener(c -> {
            refreshUserTable();
        });
        pagination.setTotalPage(1);
        pagination.setCurrentPage(1);
    }

    private void onGroupRegisterStateChanged(Observable observable, Boolean oldValue, Boolean newValue) {
        UserGroup group = groupListView.getSelectionModel().getSelectedItem();
        if (group == null) {
            return;
        }
        if(userManageService.updateRegisterPermission(group.getId(),newValue != null && newValue)) {
            group.setRegistrable(newValue != null && newValue);
        }
    }

    private void refreshUserTable() {

        String keyword = searchField.getText();
        LocalDate[] range = filterDateRangePopover.getDateRange();
        UserGroup group = groupListView.getSelectionModel().getSelectedItem();
        ObservableList<User> currentList = userTableView.getItems();
        currentList.clear();

        if (group == null) {
            return;
        }

        List<User> users = null;
        if (range == null) {
            dateFilterField.setText("");
            range = new LocalDate[]{null, null};
        }

        StringBuilder text = new StringBuilder();
        for (int idx = 0; idx < range.length; ++idx) {
            if(range[idx] == null) {
                continue;
            }
            LocalDate date = range[idx];
            text.append(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if (idx < range.length - 1) {
                text.append(" - ");
            }
        }

        dateFilterField.setText(text.toString());

        int resultCount = userManageService.countUserFiltered(group,range[0],range[1],keyword);
        int pageCount = resultCount / RECORD_COUNT_PRE_PAGE + 1;
        pagination.setTotalPage(pageCount);
        if (pagination.getCurrentPage() > pageCount) {
            pagination.setCurrentPage(pageCount);
        } else if (pagination.getCurrentPage() < 1) {
            pagination.setCurrentPage(1);
        }

        users = userManageService.getUserFiltered(
                group,range[0],range[1], keyword,
                pagination.getCurrentPage() - 1, RECORD_COUNT_PRE_PAGE
        );

        currentList.addAll(users);
    }


    @EventListener(type = UserStateChangeEvent.class)
    public void onUserTableRefresh(UserStateChangeEvent event) {
        Platform.runLater(this::refreshUserTable);
    }

    /**
     * 初始化用户上下文菜单。
     *
     * 在此方法中，创建了一个包含多个菜单项的上下文菜单，
     * 并将该菜单设置为用户表格的上下文菜单。
     *
     */
    private void initUserContextMenu() {

        ResourceBundle bundle = resources.getResourceBundle();
        ContextMenu userContextMenu = new ContextMenu();
        MenuItem menuItemAdd = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_ADD_USER));
        menuItemAdd.setOnAction(this::onActionAddUser);

        MenuItem menuItemDelete = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_TRASH_USER));
        menuItemDelete.setOnAction(this::onActionDeleteUser);
        menuItemDelete.disableProperty().bind(selectionUserDisabled);

        MenuItem menuItemEdit = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_EDIT_USER));
        menuItemEdit.setOnAction(this::onActionEditUser);
        menuItemEdit.disableProperty().bind(selectionUserDisabled);

        MenuItem menuItemPurge = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_PURGE_USER));
        menuItemPurge.setOnAction(this::onActionPurgeUser);
        menuItemPurge.disableProperty().bind(selectionUserPurgeDisabled);

        ObservableList<MenuItem> items = userContextMenu.getItems();
        items.addAll(
                menuItemAdd,
                new SeparatorMenuItem(),
                menuItemDelete,
                menuItemEdit,
                menuItemPurge
        );

        userTableView.setContextMenu(userContextMenu);
    }

    /**
     * 初始化用户组上下文菜单。
     * 在此方法中，创建了一个包含多个菜单项的上下文菜单，并将该菜单设置为组列表视图的上下文菜单。
     */
    private void initGroupContextMenu() {

        ResourceBundle bundle = resources.getResourceBundle();

        ContextMenu groupContextMenu = new ContextMenu();

        MenuItem menuItemAdd = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_ADD_GROUP));
        menuItemAdd.setOnAction(this::onActionAddGroup);
        menuItemAdd.disableProperty().bind(
                selectionGroupPurgeDisabled.not()
        );

        MenuItem menuItemDelete = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_TRASH_GROUP));
        menuItemDelete.setOnAction(this::onActionDeleteGroup);
        menuItemDelete.disableProperty().bind(
                selectionGroupDisabled
        );

        MenuItem menuItemRename = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_EDIT_GROUP));
        menuItemRename.setOnAction(this::onActionEditGroup);
        menuItemRename.disableProperty().bind(
                selectionGroupDisabled
        );

        MenuItem menuItemPurge = new MenuItem(bundle.getString(LanguageKeys.SERVER_MENU_PURGE_GROUP));
        menuItemPurge.disableProperty().bind(
                selectionGroupPurgeDisabled
        );

        CheckMenuItem menuItemRegistrable = new CheckMenuItem(bundle.getString(LanguageKeys.SERVER_MENU_REGISTERABLE));
        menuItemRegistrable.disableProperty().bind(
                selectionGroupDisabled
        );
        menuItemRegistrable.selectedProperty().bindBidirectional(
                selectionGroupRegisterable
        );

        ObservableList<MenuItem> items = groupContextMenu.getItems();
        items.addAll(
                menuItemAdd,
                new SeparatorMenuItem(),
                menuItemDelete,
                menuItemRename,
                menuItemPurge,
                menuItemRegistrable
        );

        groupListView.setContextMenu(groupContextMenu);
    }


    private void userGroupChange(Observable observable) {
        UserGroup group = groupListView.getSelectionModel().getSelectedItem();
        if (group == null) {
            selectionGroupDisabled.set(true);
            return;
        }
        selectionGroupRegisterable.set(
                group.isRegistrable() && group.getState() == State.NORMAL
        );

        refreshUserTable();
        if (selectionGroupPurgeDisabled.get()) {
            selectionGroupDisabled.set(false);
        } else {
            selectionGroupDisabled.set(true);
        }
        groupLabel.setText(group.getGroupName());
    }


    private void userSelectionChanged(Observable observable) {

        User user = userTableView.getSelectionModel().getSelectedItem();
        if (user == null) {
            selectionUserDisabled.set(true);
            selectionUserPurgeDisabled.set(true);
            return;
        }
        selectionUserDisabled.set(user.getState() == State.TRASHED);
        selectionUserPurgeDisabled.set(user.getState() == State.NORMAL);

    }

    private State getGroupState() {
        ServerUserTabView view = getView();
        ToggleGroup group = view.getUserGroupToggle();
        ToggleButton button = (ToggleButton) group.getSelectedToggle();
        if (button.getId().equals("gpNormal")) {
            return State.NORMAL;
        } else {
            return State.TRASHED;
        }
    }


    private void groupStateChanged(Observable observable, Toggle oldValue, Toggle newValue) {
        ServerUserTabView view = getView();
        ToggleGroup group = view.getUserGroupToggle();
        if (newValue == null) {
            group.selectToggle(oldValue);
            return;
        }

        ToggleButton button = (ToggleButton) newValue;
        if (button.getId().equals("gpNormal")) {
            refreshGroupByState(State.NORMAL);
        } else {
            refreshGroupByState(State.TRASHED);
        }
    }


    private void refreshGroupByState(State state) {
        List<UserGroup> groups = userManageService.getGroupsByState(state);
        groupListView.getItems().clear();
        groupListView.getItems().addAll(groups);
        groupListView.refresh();
        selectionGroupPurgeDisabled.set(state != State.TRASHED);
    }


    @FXML
    private void onActionAddGroup(ActionEvent event) {

        ServerUserTabView view = getView();
        EditGroupView editGroupView = view.getView(EditGroupView.class);
        String groupName = editGroupView.showDialog(null);
        if (groupName == null || groupName.isBlank()) {
            return;
        }
        UserGroup added = userManageService.addGroup(groupName);
        if(added != null) {
            refreshGroupByState(getGroupState());
        }

    }

    private void onActionEditGroup(ActionEvent event) {
        UserGroup group = groupListView.getSelectionModel().getSelectedItem();
        if (group == null) {
            return;
        }
        EditGroupView editGroupView = getView().getView(EditGroupView.class);
        String newName = editGroupView.showDialog(group);
        if (newName != null && !newName.isBlank()) {
            userManageService.renameGroup(group.getId(), newName);
            refreshGroupByState(getGroupState());
        }
    }

    private void onActionDeleteGroup(ActionEvent event) {
        UserGroup group = groupListView.getSelectionModel().getSelectedItem();
        if (group == null) {
            return;
        }

        ResourceBundle bundle = resources.getResourceBundle();

        ServerUserTabView view = getView();
        Alert alert = view.alert(
                bundle.getString(LanguageKeys.WARN),
                bundle.getString(LanguageKeys.SERVER_DLG_DELETE_GROUP),
                Alert.AlertType.CONFIRMATION
        );
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userManageService.transGroup(group.getId());
                refreshGroupByState(getGroupState());
            }
        });
    }

    @FXML
    private void onActionAddUser(ActionEvent event) {
        UserGroup group = groupListView.getSelectionModel().getSelectedItem();
        if (group == null) {
            return;
        }
        ServerUserTabView view = getView();
        EditUserView editUserView = view.getView(EditUserView.class);
        User newUser = editUserView.showDialog(group, null);
        if (newUser == null) {
            return;
        }

        ResourceBundle bundle = resources.getResourceBundle();

        User user = userManageService.saveUser(newUser);
        if (user == null) {
            Alert alert = view.alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.SERVER_DLG_USER_ADD_FAILED),
                    Alert.AlertType.ERROR
            );
            alert.showAndWait();
        } else {
            refreshUserTable();
        }
    }

    private void onActionEditUser(ActionEvent event) {
        User user = userTableView.getSelectionModel().getSelectedItem();
        if (user == null) {
            return;
        }
        ServerUserTabView view = getView();
        EditUserView editUserView = view.getView(EditUserView.class);
        User updated = editUserView.showDialog(user.getGroup(), user);
        if (updated == null) {
            return;
        }

        ResourceBundle bundle = resources.getResourceBundle();

        updated = userManageService.saveUser(updated);
        if (updated == null) {
            Alert alert = view.alert(
                    bundle.getString(LanguageKeys.ERROR),
                    bundle.getString(LanguageKeys.SERVER_DLG_USER_EDIT_FAILED),
                    Alert.AlertType.ERROR
            );
            alert.showAndWait();
            return;
        }
        refreshUserTable();
    }

    private void onActionDeleteUser(ActionEvent event) {
        User user = userTableView.getSelectionModel().getSelectedItem();
        if (user == null) {
            return;
        }
        ResourceBundle bundle = resources.getResourceBundle();
        ServerUserTabView view = getView();

        String text = String.format(
                bundle.getString(LanguageKeys.SERVER_DLG_DELETE_USER),
                user.getUsername()
        );
        Alert alert = view.alert(bundle.getString(LanguageKeys.WARN), text, Alert.AlertType.CONFIRMATION);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if(userManageService.deleteUser(user)) {
                    refreshUserTable();
                }
            }
        });
    }

    private void onActionPurgeUser(ActionEvent event) {
        User user = userTableView.getSelectionModel().getSelectedItem();
        if (user == null) {
            return;
        }
        ResourceBundle bundle = resources.getResourceBundle();
        ServerUserTabView view = getView();
        String text = String.format(
                bundle.getString(LanguageKeys.SERVER_DLG_PURGE_USER),
                user.getUsername()
        );
        Alert alert = view.alert(bundle.getString(LanguageKeys.WARN), text, Alert.AlertType.CONFIRMATION);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if(userManageService.purgeUser(user)) {
                    refreshUserTable();
                }
            }
        });
    }

    @FXML
    private void onActionEditStructure(ActionEvent event) {
        UserGroup group = groupListView.getSelectionModel().getSelectedItem();
        if (group == null) {
            return;
        }
        ServerUserTabView view = getView();
        FolderStructureView folderStructureView = view.getView(FolderStructureView.class);
        folderStructureView.showDialog(group);
    }


}
