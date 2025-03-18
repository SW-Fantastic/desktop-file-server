package org.swdc.rmdisk.views.common;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DatePane extends BorderPane {

    private GridPane grid = new GridPane();

    private HBox top = new HBox();

    private Map<Integer, ToggleButton> datesMap = new HashMap<>();

    private final ObservableList<LocalDate> selectedDates = FXCollections.observableArrayList();

    private final SimpleIntegerProperty year = new SimpleIntegerProperty(
            LocalDate.now().getYear()
    );

    private final SimpleIntegerProperty month = new SimpleIntegerProperty(
            LocalDate.now().getMonthValue()
    );

    private final SimpleBooleanProperty singleSelection = new SimpleBooleanProperty(false);

    private final SimpleIntegerProperty maxSelection = new SimpleIntegerProperty(2);

    private final SimpleBooleanProperty headerVisible = new SimpleBooleanProperty(true);

    private Label monthLabel;


    public DatePane() {

        FontAwesome awesome = new FontAwesome();
        Glyph glyphLeft = awesome.create(FontAwesome.Glyph.CHEVRON_LEFT);
        glyphLeft.setFontSize(16);

        Glyph glyphRight = awesome.create(FontAwesome.Glyph.CHEVRON_RIGHT);
        glyphRight.setFontSize(16);

        grid.setGridLinesVisible(true);
        grid.setHgap(2);
        grid.setVgap(2);

        top = new HBox();
        top.setSpacing(12);
        top.setAlignment(Pos.CENTER);

        HBox leftContainer = new HBox();
        leftContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftContainer, Priority.ALWAYS);

        Button left = new Button();
        left.setPadding(new Insets(8));
        left.getStyleClass().add("arrow");
        left.setOnAction(this::onLeftArrowClick);
        left.setGraphic(glyphLeft);
        leftContainer.getChildren().add(left);

        monthLabel = new Label();
        monthLabel.setText(DateUIUtil.getMonthLabel(month.getValue()));

        HBox rightContainer = new HBox();
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        Button right = new Button();
        right.setOnAction(this::onRightArrowClick);
        right.setPadding(new Insets(8));
        right.getStyleClass().add("arrow");
        right.setGraphic(glyphRight);
        rightContainer.getChildren().add(right);
        top.getChildren().addAll(leftContainer, monthLabel,rightContainer);

        setTop(top);
        setCenter(grid);

        refreshDatePane();
        getStyleClass().add("date-pane");

        this.year.addListener(obs-> {
            refreshDatePane();
            refreshSelectState();
        });

        this.month.addListener(obs-> {
            refreshDatePane();
            monthLabel.setText(DateUIUtil.getMonthLabel(month.getValue()));
            refreshSelectState();
        });

        this.selectedDates.addListener((ListChangeListener<LocalDate>)  c -> {
            refreshSelectState();
        });

        this.headerVisible.addListener(c -> {
            if (!headerVisible.get()) {
                setTop(null);
            } else {
                setTop(top);
            }
        });

    }

    private void onLeftArrowClick(ActionEvent event) {
        if (month.getValue() == 1) {
            setMonth(12);
            setYear(year.getValue() - 1);
        } else {
            setMonth(month.getValue() - 1);
        }
        refreshSelectState();
    }


    private void onRightArrowClick(ActionEvent event) {
        if(month.getValue() == 12) {
            setMonth(1);
            setYear(year.getValue() + 1);
        } else {
            setMonth(month.getValue() + 1);
        }
        refreshSelectState();
    }


    public void setHeaderVisible(boolean headerVisible) {
        this.headerVisible.set(headerVisible);
    }

    public boolean isHeaderVisible() {
        return headerVisible.get();
    }

    public SimpleBooleanProperty headerVisibleProperty() {
        return headerVisible;
    }

    public SimpleIntegerProperty monthProperty() {
        return month;
    }

    public SimpleIntegerProperty maxSelectionProperty() {
        return maxSelection;
    }

    public SimpleIntegerProperty yearProperty() {
        return year;
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public void setMonth(int month) {
        this.month.set(month);
    }

    public int getMonth() {
        return month.get();
    }

    public int getYear() {
        return year.get();
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection.set(maxSelection);
    }

    public int getMaxSelection() {
        return maxSelection.get();
    }

    public void selectDay(int day) {

        LocalDate selected = LocalDate.of(year.getValue(), month.getValue(), day);
        setSelectedDate(selected);

    }


    public void setSelectedDate(LocalDate selected) {
        if (singleSelection.getValue()) {
            selectedDates.clear();
        } else {

            for (LocalDate date : selectedDates) {
                if (date.equals(selected)) {
                    return;
                }
            }

            while (selectedDates.size() + 1 > maxSelection.getValue()) {
                selectedDates.removeFirst();
            }

        }
        selectedDates.add(selected);
    }

    public ObservableList<LocalDate> getSelectedDates() {
        return selectedDates;
    }

    private void refreshDatePane() {

        grid.getChildren().clear();
        grid.getRowConstraints().clear();
        grid.getColumnConstraints().clear();

        datesMap.clear();

        // 总日期数
        int totalDays = DateUIUtil.getMonthDates(year.getValue(), month.getValue());

        // 第一天是周几
        int startWeekDay = DateUIUtil.getDayOfWeek(year.getValue(), month.getValue(), 1);

        for (int i = 0; i < 7; ++i) {
            Label label = new Label(DateUIUtil.getWeekDayLabel(i));
            label.setPadding(new Insets(8,4,8,4));
            grid.add(label, i, 0);
        }
        int currentRow = 1;
        int lastDay = 1;
        // 循环生成按钮，从周日开始
        do {

            for (int col = 0; col < 7; ++col) {
                int day = (currentRow - 1) * 7 + col - startWeekDay + 1;
                if (day <= 0) {
                    continue;
                }
                if (day > totalDays) {
                    break;
                }

                ToggleButton button = new ToggleButton(String.valueOf(day));
                button.setPrefSize(38, 38);
                if (col == 5 || col == 6) {
                    button.getStyleClass().add("weekend");
                } else {
                    button.getStyleClass().add("weekday");
                }
                button.setUserData(day);
                button.selectedProperty().addListener(obs -> {
                    if (button.isSelected()) {
                        selectDay(day);
                    } else {
                        selectedDates.remove(LocalDate.of(year.getValue(), month.getValue(), day));
                    }
                    refreshSelectState();
                });
                datesMap.put(day, button);
                grid.add(button, col, currentRow);

                lastDay = day;
            }
            currentRow++;

        } while (lastDay < totalDays);

        for (int i = 0; i < 7; ++i) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setHalignment(HPos.CENTER);
            constraints.setFillWidth(true);
            grid.getColumnConstraints().add(constraints);
        }
    }

    private void refreshSelectState() {

        if(selectedDates.isEmpty()) {
            for (ToggleButton button : datesMap.values()) {
                button.setSelected(false);
            }
            return;
        }

        LocalDate firstDate = selectedDates.stream().min(Comparator.naturalOrder()).get();
        LocalDate lastDate = selectedDates.stream().max(Comparator.naturalOrder()).get();

        Map<Integer,LocalDate> dates = selectedDates.stream().filter(
                date -> date.getMonthValue() == month.getValue() && date.getYear() == year.getValue()
        ).collect(Collectors.toMap(LocalDate::getDayOfMonth, d -> d));

        for (ToggleButton button : datesMap.values()) {

            button.getStyleClass().remove("date-in-range");

            boolean dayInRange = false;
            Integer cellDay = (Integer) button.getUserData();
            button.setSelected(dates.containsKey(cellDay));

            LocalDate date = LocalDate.of(year.getValue(), month.getValue(), cellDay);
            if(date.isAfter(firstDate) && date.isBefore(lastDate)) {
                dayInRange = true;
            }

            if (dayInRange) {
                button.getStyleClass().add("date-in-range");
            }

        }

    }

    public void update() {
        refreshDatePane();
        refreshSelectState();
    }

}
