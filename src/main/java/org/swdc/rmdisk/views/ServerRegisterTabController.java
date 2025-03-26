package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.Toast;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.ManagedServerConfigure;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.core.entity.CommonState;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;
import org.swdc.rmdisk.service.CommonService;
import org.swdc.rmdisk.service.UserManageService;
import org.swdc.rmdisk.views.cells.RegisterGroupTableCell;
import org.swdc.rmdisk.views.cells.RegisterStateTableCell;
import org.swdc.rmdisk.views.common.DateRangePopover;
import org.swdc.rmdisk.views.common.Pagination;
import org.swdc.rmdisk.views.modals.EditAvatarView;
import org.swdc.rmdisk.views.modals.EditUserView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

@Singleton
public class ServerRegisterTabController extends ViewController<ServerRegisterTabView> {

    @Inject
    private ManagedServerConfigure config;

    @Inject
    private CommonService commonService;

    @Inject
    private UserManageService userManageService;

    @Inject
    private Logger logger;

    @FXML
    private CheckBox chkAllowReg;

    @FXML
    private CheckBox chkNewUserVerify;

    @FXML
    private TextField txtDefaultSpaceSize;

    @FXML
    private Slider sliderSpaceSize;

    @FXML
    private ImageView defaultAvatar;

    @FXML
    private TableView<UserRegisterRequest> requestTable;

    @FXML
    private TableColumn<UserRegisterRequest, Long> columnId;

    @FXML
    private TableColumn<UserRegisterRequest, String> columnName;

    @FXML
    private TableColumn<UserRegisterRequest, String> columnNickname;

    @FXML
    private TableColumn<UserRegisterRequest, Void> columnGroup;

    @FXML
    private TableColumn<UserRegisterRequest, Void> columnState;

    @FXML
    private TextField dateFilterField;

    @FXML
    private TextField searchField;

    @Inject
    private FXResources resources;

    private static final int RECORD_COUNT_PRE_PAGE = 80;

    private DateRangePopover filterDateRangePopover;

    private SimpleBooleanProperty requestSelectionProperty = new SimpleBooleanProperty(true);

    private Pagination pagination;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        Rectangle clip = new Rectangle(0,0,defaultAvatar.getFitWidth(),defaultAvatar.getFitHeight());
        clip.setArcHeight(8);
        clip.setArcWidth(8);
        defaultAvatar.setClip(clip);
        sliderSpaceSize.valueProperty().addListener(this::onSpaceChanged);
        reloadConfigs();

        ServerRegisterTabView view = getView();
        ToggleGroup group = view.getRequestTypeGroup();
        group.selectedToggleProperty().addListener(this::onRequestTypeChanged);

        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnNickname.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        columnGroup.setCellFactory(t -> new RegisterGroupTableCell());
        columnState.setCellFactory(t -> new RegisterStateTableCell());

        requestTable.getSelectionModel().selectedItemProperty()
                .addListener(this::onRequestTableSelectionChanged);

        requestTable.setOnMouseClicked(this::onRequestTableClicked);

        filterDateRangePopover = new DateRangePopover(resourceBundle);
        filterDateRangePopover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        filterDateRangePopover.dateRangeProperty()
                .addListener(observable -> refreshRequestTable());
        dateFilterField.setOnMouseClicked(
                event -> filterDateRangePopover.show(dateFilterField)
        );

        searchField.textProperty()
                .addListener(obs -> refreshRequestTable());

        refreshRequestTable();
        initMenuContextMenu();
        initPagination();
    }

    private void onRequestTableClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            onAcceptRequest(null);
        }
    }

    private void initPagination() {

        ServerRegisterTabView view = getView();
        HBox container = view.findById("pagination-pane");

        pagination = new Pagination();
        pagination.setCurrentPage(1);
        pagination.setTotalPage(1);
        container.getChildren().add(pagination);

    }

    private void initMenuContextMenu() {

        ResourceBundle bundle = resources.getResourceBundle();
        ContextMenu contextMenu = new ContextMenu();

        MenuItem itemAccept = new MenuItem(bundle.getString(LanguageKeys.SERVER_REG_MENU_ACCEPT));
        itemAccept.disableProperty().bind(requestSelectionProperty);
        itemAccept.setOnAction(this::onAcceptRequest);

        MenuItem itemReject = new MenuItem(bundle.getString(LanguageKeys.SERVER_REG_MENU_REJECT));
        itemReject.disableProperty().bind(requestSelectionProperty);
        itemReject.setOnAction(this::onRejectRequest);

        contextMenu.getItems().addAll(
                itemAccept,
                itemReject
        );
        requestTable.setContextMenu(contextMenu);

    }

    private CommonState getState() {
        ServerRegisterTabView view = getView();
        ToggleGroup group = view.getRequestTypeGroup();
        ToggleButton button = (ToggleButton) group.getSelectedToggle();
        if (button == null) {
            return CommonState.PENDING;
        }
        return (CommonState) button.getUserData();
    }

    private void onRequestTypeChanged(Observable observable) {

        CommonState state = getState();
        if (state != CommonState.PENDING) {
            requestSelectionProperty.set(true);
        }
        refreshRequestTable();
    }

    private void onRequestTableSelectionChanged(Observable observable) {

        CommonState state = getState();
        requestSelectionProperty.set(state != CommonState.PENDING);
    }

    private void refreshRequestTable() {

        if (pagination == null) {
            return;
        }

        CommonState state = getState();

        LocalDate[] range = filterDateRangePopover.getDateRange();
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

        int resultCount = userManageService.countRegisterRequestFiltered(range[0],range[1],searchField.getText(),state);
        int pageCount = resultCount / RECORD_COUNT_PRE_PAGE + 1;
        if (pagination.getCurrentPage() > pageCount) {
            pagination.setCurrentPage(pageCount);
        } else if (pagination.getCurrentPage() < 1) {
            pagination.setCurrentPage(1);
        }

        ObservableList<UserRegisterRequest> requestList = requestTable.getItems();
        requestList.clear();

        List<UserRegisterRequest> requests = userManageService.getRegisterRequestsFiltered(
                range[0],range[1],searchField.getText(),state,
                pagination.getCurrentPage(),RECORD_COUNT_PRE_PAGE
        );
        requestList.addAll(requests);

    }

    private void reloadConfigs() {
        sliderSpaceSize.setValue(config.getDefaultSpaceSize());
        txtDefaultSpaceSize.setText(sliderSpaceSize.getValue() + " GB");

        chkAllowReg.setSelected(config.getEnableRegister());
        chkNewUserVerify.setSelected(!config.getAutoRegisterUser());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(commonService.getDefaultAvatar());
        defaultAvatar.setImage(new Image(inputStream));
    }


    private void onSpaceChanged(Observable observable) {
        txtDefaultSpaceSize.setText(sliderSpaceSize.getValue() + " GB");
    }

    @FXML
    private void selectDefaultAvatar() {

        ResourceBundle bundle = resources.getResourceBundle();
        EditAvatarView editAvatarView = getView().getView(EditAvatarView.class);

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(bundle.getString(LanguageKeys.SERVER_DLG_EDIT_AVATAR_FILTER), "*.png", "*.jpg", "*.jpeg")
        );
        chooser.setTitle(bundle.getString(LanguageKeys.SERVER_DLG_EDIT_AVATAR_TITLE));
        File file = chooser.showOpenDialog(getView().getStage());
        if (file == null) {
            return;
        }
        byte[] data = editAvatarView.showModal(file);
        if (data == null) {
            return;
        }

        defaultAvatar.setImage(new Image(new ByteArrayInputStream(data)));
        commonService.setDefaultAvatar(data);

    }

    public void onRejectRequest(ActionEvent event) {
        UserRegisterRequest request = requestTable.getSelectionModel().getSelectedItem();
        if (request == null) {
            return;
        }
        userManageService.updateRegisterRequestState(
                request.getId(), CommonState.REJECTED
        );
        refreshRequestTable();
    }


    public void onAcceptRequest(ActionEvent event) {

        UserRegisterRequest request = requestTable.getSelectionModel().getSelectedItem();
        if (request == null) {
            return;
        }

        CommonState state = getState();
        if (state != CommonState.PENDING) {
            return;
        }

        ServerRegisterTabView view = getView();
        EditUserView editUserView = view.getView(EditUserView.class);
        User user = editUserView.showDialog(request);
        if (user == null) {
            return;
        }

        User created = userManageService.saveUser(user);
        if(created != null) {
            userManageService.updateRegisterRequestState(
                    request.getId(), CommonState.ACCEPTED
            );
            refreshRequestTable();
        }

    }

    @FXML
    public void onConfigSave() {
        config.setEnableRegister(chkAllowReg.isSelected());
        config.setAutoRegisterUser(!chkNewUserVerify.isSelected());
        config.setDefaultSpaceSize((int) sliderSpaceSize.getValue());
        try {
            config.save();
            Toast.showMessage("设置已经保存");
        } catch (Exception e) {
            logger.error("Failed to save configure", e);
        }
    }



}
