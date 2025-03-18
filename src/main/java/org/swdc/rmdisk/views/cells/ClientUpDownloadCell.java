package org.swdc.rmdisk.views.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.rmdisk.views.modals.UpDownloadTask;

import java.awt.*;
import java.io.File;
import java.util.Optional;

public class ClientUpDownloadCell extends ListCell<UpDownloadTask> {


    private VBox cellRoot;

    private ProgressBar bar;

    private Label lblTitle;

    private Label lblIcon;

    private Button btnCancel;

    private Button btnOpen;

    private Fontawsome5Service fontawsome5Service;

    private AbstractView view;

    public ClientUpDownloadCell(AbstractView view, Fontawsome5Service fontawsome5Service) {
        this.view = view;
        this.fontawsome5Service = fontawsome5Service;
        this.cellRoot = new VBox();
        this.cellRoot.setPadding(new Insets(6));

        HBox label = new HBox();
        label.setAlignment(Pos.CENTER_LEFT);
        label.setSpacing(8);
        label.setPadding(new Insets(0,4,0,4));

        btnCancel = createButton("times");
        btnCancel.setOnAction( e -> {
            if(this.isEmpty()) {
                return;
            }
            btnCancel.setDisable(true);
            UpDownloadTask task = getItem();
            if(task.isFinished()) {
                if (task.getTaskType() == UpDownloadTask.TaskType.DOWNLOAD) {
                    File file = task.getFile();
                    if (file.exists()) {
                        Alert alert = view.alert("确认", "需要同时删除文件吗？", Alert.AlertType.CONFIRMATION);
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if (!buttonType.isEmpty() && buttonType.get() == ButtonType.OK) {
                            if (task.getFile().exists()) {
                                task.getFile().delete();
                            }
                        }
                    }
                }
                getListView().getItems().remove(task);
            } else {
                task.cancel();
                getListView().getItems().remove(task);
            }
        });

        btnOpen = createButton("folder-open");
        btnOpen.setOnAction( e -> {
            if(this.isEmpty()) {
                return;
            }
            UpDownloadTask task = getItem();
            if (task.getTaskType() == UpDownloadTask.TaskType.DOWNLOAD) {
                File file = task.getFile();
                if (file.exists()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (Exception ex) {
                    }
                }
            }
        });

        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        right.getChildren().addAll(btnOpen, btnCancel);
        right.setSpacing(6);
        HBox.setHgrow(right, Priority.ALWAYS);

        lblIcon = new Label();
        lblIcon.setFont(fontawsome5Service.getSolidFont(FontSize.SMALL));

        lblTitle = new Label();
        lblTitle.setText("Title");
        label.getChildren().addAll(lblIcon,lblTitle, right);

        bar = new ProgressBar();
        bar.prefWidthProperty().bind(this.widthProperty().subtract(32));
        bar.setMaxHeight(14);

        HBox prog = new HBox();
        prog.setAlignment(Pos.CENTER_LEFT);
        prog.getChildren().add(bar);

        cellRoot.setSpacing(4);
        cellRoot.getChildren().addAll(label, prog);
    }

    private Button createButton(String icon) {
        Button btnTool = new Button();
        btnTool.setFont(fontawsome5Service.getSolidFont(FontSize.SMALL));
        btnTool.setText(fontawsome5Service.getFontIcon(icon));
        btnTool.getStyleClass().add("tool-button");
        btnTool.setPadding(new Insets(6));
        btnTool.setPrefSize(32, 32);
        return btnTool;
    }

    @Override
    protected void updateItem(UpDownloadTask item, boolean empty) {
        super.updateItem(item, empty);

        if (empty){
            setGraphic(null);
            return;
        }

        setGraphic(cellRoot);
        bar.progressProperty().unbind();
        bar.progressProperty().bind(item.progressProperty());
        btnCancel.setDisable(item.isCancelled());
        lblTitle.setText(item.getFile().getName());
        lblIcon.setText(fontawsome5Service.getFontIcon(item.getTaskType().getIcon()));
        btnOpen.visibleProperty().unbind();

        if (item.getTaskType() == UpDownloadTask.TaskType.DOWNLOAD) {
            btnOpen.visibleProperty().bind(item.finishedProperty());
        }

    }

}
