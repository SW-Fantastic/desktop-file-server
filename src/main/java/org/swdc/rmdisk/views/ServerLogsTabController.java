package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PopOver;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.view.ViewController;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.core.entity.Activity;
import org.swdc.rmdisk.core.entity.ActivityType;
import org.swdc.rmdisk.core.entity.CommonState;
import org.swdc.rmdisk.service.ActivityService;
import org.swdc.rmdisk.views.cells.LogsDateTimeTableCell;
import org.swdc.rmdisk.views.cells.LogsUserNameTableCell;
import org.swdc.rmdisk.views.common.DateRangePopover;
import org.swdc.rmdisk.views.common.Pagination;
import org.swdc.rmdisk.views.events.ConfigureUpdateEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Singleton
public class ServerLogsTabController extends ViewController<ServerLogTabView> {

    @Inject
    private ServerConfigure serverConfigure;

    @Inject
    private ActivityService activityService;

    @FXML
    private ToggleButton btnLog;

    @FXML
    private TextField txtDateFilter;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableColumn<Activity,String> colUserName;

    @FXML
    private TableColumn<Activity,String> colAddress;

    @FXML
    private TableColumn<Activity, ActivityType> colOperation;

    @FXML
    private TableColumn<Activity,String> colClassName;

    @FXML
    private TableColumn<Activity, Void> colDateTime;

    @FXML
    private TableColumn<Activity,String> colProperty;

    @FXML
    private TableColumn<Activity,String> colOldValue;

    @FXML
    private TableColumn<Activity,String> colNewValue;

    @FXML
    private TableView<Activity> logTable;

    private Pagination pagination;

    private DateRangePopover filterDateRangePopover;

    private static final int RECORD_COUNT_PRE_PAGE = 80;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        ServerLogTabView view = getView();
        view.setupToolButton(btnLog, "clipboard-check");
        if (serverConfigure.getRecordLogs()) {
            btnLog.setSelected(true);
        }

        filterDateRangePopover = new DateRangePopover(resourceBundle);
        filterDateRangePopover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        filterDateRangePopover.dateRangeProperty()
                .addListener(observable -> refreshLogTable());
        txtDateFilter.setOnMouseClicked(
                event -> filterDateRangePopover.show(txtDateFilter)
        );

        txtSearch.textProperty().addListener(
                obs -> refreshLogTable()
        );

        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colOperation.setCellValueFactory(new PropertyValueFactory<>("operation"));
        colClassName.setCellValueFactory(new PropertyValueFactory<>("resource"));
        colProperty.setCellValueFactory(new PropertyValueFactory<>("property"));
        colOldValue.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        colNewValue.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        colDateTime.setCellFactory(v -> new LogsDateTimeTableCell());
        colUserName.setCellFactory(v -> new LogsUserNameTableCell());

        refreshLogTable();
        initPagination();

    }

    private void initPagination() {

        ServerLogTabView view = getView();
        HBox container = view.findById("pagination-pane");

        pagination = new Pagination();
        pagination.setCurrentPage(1);
        pagination.setTotalPage(1);
        container.getChildren().add(pagination);

    }

    private void refreshLogTable() {
        if (pagination == null) {
            return;
        }

        LocalDate[] range = filterDateRangePopover.getDateRange();
        if (range == null) {
            txtDateFilter.setText("");
            range = new LocalDate[]{null, null};
        }

        LocalDateTime start = null;
        LocalDateTime end = null;
        if (range[0] != null) {
            start = LocalDateTime.of(range[0], LocalTime.MIN);
        }
        if (range[1] != null) {
            end = LocalDateTime.of(range[1],LocalTime.MIN);
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
        txtDateFilter.setText(text.toString());

        int resultCount = activityService.countLogFiltered(start,end,txtSearch.getText(),null);
        int pageCount = resultCount / RECORD_COUNT_PRE_PAGE + 1;
        if (pagination.getCurrentPage() > pageCount) {
            pagination.setCurrentPage(pageCount);
        } else if (pagination.getCurrentPage() < 1) {
            pagination.setCurrentPage(1);
        }

        List<Activity> activities = activityService.getLogsFiltered(
                start,end,txtSearch.getText(),null,pagination.getCurrentPage(),RECORD_COUNT_PRE_PAGE
        );

        ObservableList<Activity> logTableItems = logTable.getItems();
        logTableItems.clear();
        logTableItems.addAll(activities);
    }

    @EventListener(type = ConfigureUpdateEvent.class)
    public void onConfigRefresh(ConfigureUpdateEvent event) {
        Platform.runLater(() -> {
            btnLog.setSelected(serverConfigure.getRecordLogs());
        });
    }

    @FXML
    public void logClicked(){
        try {
            serverConfigure.setRecordLogs(btnLog.isSelected());
            serverConfigure.save();
        } catch (Exception e) {
            btnLog.setSelected(!btnLog.isSelected());
        }
    }

    @FXML
    public void trashLogs() {
        try {
            List<Activity> activities = logTable.getItems();
            activityService.removeLogs(activities);
            refreshLogTable();
        } catch (Exception e) {

        }
    }


}
