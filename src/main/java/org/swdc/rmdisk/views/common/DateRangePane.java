package org.swdc.rmdisk.views.common;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class DateRangePane extends BorderPane {

    private DatePane startDatePane;

    private DatePane endDatePane;

    private HBox header;

    private Label labelLeft;

    private Label labelRight;


    public DateRangePane() {

        FontAwesome awesome = new FontAwesome();
        Glyph glyphLeft = awesome.create(FontAwesome.Glyph.CHEVRON_LEFT);
        glyphLeft.setFontSize(16);

        Glyph glyphRight = awesome.create(FontAwesome.Glyph.CHEVRON_RIGHT);
        glyphRight.setFontSize(16);

        header = new HBox();

        HBox leftContainer = new HBox();
        leftContainer.setSpacing(12);
        leftContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftContainer, Priority.ALWAYS);

        Button left = new Button();
        left.setOnAction(this::onLeftArrowClick);
        left.setPadding(new Insets(8));
        left.setGraphic(glyphLeft);
        left.getStyleClass().add("arrow");
        glyphLeft.getStyleClass().add("arrow");

        labelLeft = new Label();
        leftContainer.getChildren().addAll(left, labelLeft);

        HBox rightContainer = new HBox();
        rightContainer.setSpacing(12);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        Button right = new Button();
        right.setOnAction(this::onRightArrowClick);
        right.setPadding(new Insets(8));
        right.setGraphic(glyphRight);
        right.getStyleClass().add("arrow");
        glyphRight.getStyleClass().add("arrow");

        labelRight = new Label();
        rightContainer.getChildren().addAll(labelRight, right);

        header.getChildren().addAll(leftContainer, rightContainer);

        setTop(header);

        startDatePane = new DatePane();
        endDatePane = new DatePane();

        Bindings.bindContentBidirectional(
                startDatePane.getSelectedDates(),
                endDatePane.getSelectedDates()
        );

        startDatePane.setHeaderVisible(false);
        endDatePane.setHeaderVisible(false);

        HBox center = new HBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(12);
        center.getChildren().addAll(startDatePane, endDatePane);

        setCenter(center);
        setPadding(new Insets(12));

        if (startDatePane.getMonth() == 12) {
            endDatePane.setMonth(1);
            endDatePane.setYear(startDatePane.getYear() + 1);
        } else {
            endDatePane.setMonth(startDatePane.getMonth() + 1);
        }

        labelLeft.setText(
                startDatePane.getYear() + "  " + DateUIUtil.getMonthLabel(startDatePane.getMonth())
        );

        labelRight.setText(
                endDatePane.getYear() + "  " + DateUIUtil.getMonthLabel(endDatePane.getMonth())
        );

        getStyleClass().add("date-pane");
    }

    private void onLeftArrowClick(ActionEvent event) {
        int month = startDatePane.getMonth();
        int year = startDatePane.getYear();
        if (month == 1) {
            startDatePane.setMonth(12);
            startDatePane.setYear(year - 1);
        } else {
            startDatePane.setMonth(month - 1);
        }

        month = endDatePane.getMonth();
        year = endDatePane.getYear();
        if (month == 1) {
            endDatePane.setMonth(12);
            endDatePane.setYear(year - 1);
        } else {
            endDatePane.setMonth(month - 1);
        }

        labelLeft.setText(
                startDatePane.getYear() + "  " + DateUIUtil.getMonthLabel(startDatePane.getMonth())
        );

        labelRight.setText(
                endDatePane.getYear() + "  " + DateUIUtil.getMonthLabel(endDatePane.getMonth())
        );

    }


    private void onRightArrowClick(ActionEvent event) {
        int month = endDatePane.getMonth();
        int year = endDatePane.getYear();
        if (month == 12) {
            endDatePane.setMonth(1);
            endDatePane.setYear(year + 1);
        } else {
            endDatePane.setMonth(month + 1);
        }

        month = startDatePane.getMonth();
        year = startDatePane.getYear();
        if (month == 12) {
            startDatePane.setMonth(1);
            startDatePane.setYear(year + 1);
        } else {
            startDatePane.setMonth(month + 1);
        }

        labelLeft.setText(
                startDatePane.getYear() + "  " + DateUIUtil.getMonthLabel(startDatePane.getMonth())
        );

        labelRight.setText(
                endDatePane.getYear() + "  " + DateUIUtil.getMonthLabel(endDatePane.getMonth())
        );
    }

    public LocalDate getStartDate() {
        List<LocalDate> localDates = startDatePane.getSelectedDates();
        if (localDates.isEmpty()) {
            return null;
        } else {
            return localDates.stream().min(Comparator.naturalOrder()).get();
        }
    }


    public LocalDate getEndDate() {
        List<LocalDate> localDates = endDatePane.getSelectedDates();
        if (localDates.isEmpty()) {
            return null;
        } else {
            return localDates.stream().max(Comparator.naturalOrder()).get();
        }
    }

    public void selectDate(LocalDate date) {
        startDatePane.selectDay(date.getDayOfMonth());
    }

    public void selectRange(LocalDate start, LocalDate end) {
        ObservableList<LocalDate> dates = startDatePane.getSelectedDates();
        dates.clear();
        if (start != null) {
            dates.add(start);
        }
        if (end != null) {
            dates.add(end);
        }
        startDatePane.update();
        endDatePane.update();
    }

}
