package org.swdc.rmdisk.views.common;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

public class Pagination extends HBox {

    private Button prevButton;
    private Button nextButton;

    private ToggleGroup pageGroup = new ToggleGroup();

    /**
     * 当前展示中的页码
     */
    private SimpleIntegerProperty currentPage = new SimpleIntegerProperty(1);

    /**
     * 总页数
     */
    private SimpleIntegerProperty totalPage = new SimpleIntegerProperty(10);

    /**
     * 最多展示多少页
     */
    private SimpleIntegerProperty pageSize = new SimpleIntegerProperty(5);

    private boolean onRefresh = false;

    public Pagination() {

        FontAwesome awesome = new FontAwesome();
        Glyph glyphLeft = awesome.create(FontAwesome.Glyph.CHEVRON_LEFT);
        glyphLeft.setFontSize(16);
        glyphLeft.getStyleClass().add("arrow");

        prevButton = new Button();
        prevButton.setGraphic(glyphLeft);
        prevButton.setPadding(new Insets(4));
        prevButton.getStyleClass().add("arrow");
        prevButton.setPrefSize(28, 28);
        prevButton.setOnAction(this::onPrevPage);

        Glyph glyphRight = awesome.create(FontAwesome.Glyph.CHEVRON_RIGHT);
        glyphRight.setFontSize(16);
        glyphRight.getStyleClass().add("arrow");

        nextButton = new Button();
        nextButton.setGraphic(glyphRight);
        nextButton.setPadding(new Insets(4));
        nextButton.getStyleClass().add("arrow");
        nextButton.setPrefSize(28, 28);
        nextButton.setOnAction(this::onNextPage);

        setSpacing(4);
        setAlignment(Pos.CENTER);
        setFillHeight(false);
        getStyleClass().add("pagination");

        refreshPagination();

        pageGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (onRefresh) {
                return;
            }

            if (oldValue == null) {
                return;
            } else if (newValue == null) {
                pageGroup.selectToggle(oldValue);
                newValue = oldValue;
            }

            int newPage = (int)newValue.getUserData();
            if (newPage != currentPage.get()) {
                currentPage.set(newPage);
            }

        });

        currentPage.addListener((observable, oldValue, newValue) -> {
            refreshPagination();
        });

        totalPage.addListener((observable, oldValue, newValue) -> {
            refreshPagination();
        });
    }

    private void onPrevPage(ActionEvent event) {
        int current = currentPage.get();
        if (current > 1) {
            current--;
            currentPage.set(current);
        }
    }

    private void onNextPage(ActionEvent event) {
        int current = currentPage.get();
        if (current < totalPage.get()) {
            current++;
            currentPage.set(current);
        }
    }

    private void refreshPagination() {

        onRefresh = true;


        ObservableList<Node> buttons = getChildren();
        buttons.clear();

        pageGroup.getToggles().clear();

        int current = currentPage.get();
        int displayPages = pageSize.get();
        int pageNoStart = 1;

        if (displayPages > totalPage.get()) {
            displayPages = totalPage.get();
        }

        if (current < displayPages) {
            pageNoStart = 1;
        } else if (current > totalPage.get() - displayPages) {
            pageNoStart = totalPage.get() - displayPages + 1;
        } else {
            int middlePage = displayPages / 2;
            pageNoStart = current - middlePage;
        }

        buttons.add(prevButton);

        for (int i = pageNoStart; i < pageNoStart + displayPages; i++) {
            ToggleButton button = new ToggleButton(String.valueOf(i));
            button.setPadding(new Insets(4));
            button.setPrefSize(28, 28);
            button.setUserData(i);
            button.getStyleClass().add("page-number");
            buttons.add(button);
            pageGroup.getToggles().add(button);
            if (i == current) {
                this.pageGroup.selectToggle(button);
            }
        }

        buttons.add(nextButton);
        nextButton.setDisable(current >= totalPage.get());
        prevButton.setDisable(current <= 1);

        onRefresh = false;
    }

    public SimpleIntegerProperty totalPageProperty() {
        return totalPage;
    }

    public SimpleIntegerProperty pageSizeProperty() {
        return pageSize;
    }

    public SimpleIntegerProperty currentPageProperty() {
        return currentPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage.set(totalPage);
    }

    public int getTotalPage() {
        return totalPage.get();
    }

    public void setPageSize(int pageSize) {
        this.pageSize.set(pageSize);
    }

    public int getPageSize() {
        return pageSize.get();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage.set(currentPage);
    }

    public int getCurrentPage() {
        return currentPage.get();
    }
}
