package org.swdc.rmdisk.views.common;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.PopOver;
import org.swdc.fx.font.Fontawsome5Service;

import java.time.LocalDate;

public class DateRangePopover extends PopOver {

    private DateRangePane dateRangePane;

    private SimpleObjectProperty<LocalDate[]> dateRangeProperty = new SimpleObjectProperty<>();


    public DateRangePopover() {
        super();

        dateRangePane = new DateRangePane();
        HBox buttonBar = new HBox();
        buttonBar.setSpacing(8);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(12));


        Button clearButton = new Button();
        clearButton.setOnAction(e -> {
            dateRangeProperty.set(null);
            dateRangePane.selectRange(null, null);
            hide();
        });
        clearButton.setText("清除");
        clearButton.getStyleClass().add("primary-button");
        clearButton.setPadding(new Insets(4));
        clearButton.setPrefSize(80, 32);

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.getChildren().add(clearButton);
        HBox.setHgrow(layout, Priority.ALWAYS);
        buttonBar.getChildren().add(layout);

        Button okButton = new Button("确定");
        okButton.setOnAction(e -> {
            dateRangeProperty.set(new LocalDate[]{
                    dateRangePane.getStartDate(), dateRangePane.getEndDate()
            });
            hide();
        });

        okButton.getStyleClass().add("button-accept");
        okButton.setPrefSize(80, 32);
        okButton.setDefaultButton(true);


        Button cancelButton = new Button("取消");
        cancelButton.setOnAction(e -> {
            hide();
        });
        cancelButton.getStyleClass().add("primary-button");
        cancelButton.setPadding(new Insets(4));
        cancelButton.setCancelButton(true);
        cancelButton.setPrefSize(80, 32);
        buttonBar.getChildren().addAll(okButton, cancelButton);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("date-pane");
        root.setCenter(dateRangePane);
        root.setBottom(buttonBar);

        setContentNode(root);
    }

    public SimpleObjectProperty<LocalDate[]> dateRangeProperty() {
        return dateRangeProperty;
    }

    public LocalDate[] getDateRange() {
        return dateRangeProperty.get();
    }

    public void setDateRangeProperty(LocalDate[] dateRange) {
        dateRangePane.selectRange(dateRange[0], dateRange[1]);
        dateRangeProperty.set(dateRange);
    }

}
