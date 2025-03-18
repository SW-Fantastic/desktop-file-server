package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.core.entity.CommonState;

@View(
        viewLocation = "views/main/ServerRegisterTabView.fxml",
        stage=false
)
public class ServerRegisterTabView extends AbstractView {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    private ToggleGroup requestTypeGroup = new ToggleGroup();


    @PostConstruct
    public void init() {

        ScrollPane configScrollPane = findById("regScrollPanel");;
        GridPane gridPane = (GridPane) configScrollPane.getContent();
        gridPane.prefWidthProperty().bind(configScrollPane.widthProperty().subtract(12));

        setupToolButton(findById("btnRegSave"), "save");

        ToggleButton btnAll = findById("btnAll");
        setupRequestTypeButton(btnAll, "address-book", CommonState.PENDING);
        setupRequestTypeButton(findById("btnAccepted"),"user-check",CommonState.ACCEPTED);
        setupRequestTypeButton(findById("btnRejected"),"user-times", CommonState.REJECTED);

        requestTypeGroup.selectedToggleProperty().addListener(this::onRequestTypeChanged);
        requestTypeGroup.selectToggle(btnAll);

    }

    private void onRequestTypeChanged(Observable observable, Toggle oldValue, Toggle newValue) {
        if(newValue == null) {
            requestTypeGroup.selectToggle(oldValue);
        }
    }

    private void setupRequestTypeButton(ToggleButton button, String icon, CommonState state) {
        requestTypeGroup.getToggles().add(button);
        button.setUserData(state);
        setupToolButton(button, icon);
    }

    private void setupToolButton(ButtonBase toolButton, String icon) {
        toolButton.setFont(fontawsome5Service.getSolidFont(FontSize.VERY_SMALL));
        toolButton.setText(fontawsome5Service.getFontIcon(icon));
        toolButton.setPadding(new Insets(4));
    }

    public ToggleGroup getRequestTypeGroup() {
        return requestTypeGroup;
    }
}
