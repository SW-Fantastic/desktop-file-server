package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(
        viewLocation = "views/main/ServerUserTabView.fxml",
        stage = false
)
public class ServerUserTabView extends AbstractView {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    private ToggleGroup userGroupToggle = new ToggleGroup();


    @PostConstruct
    public void init() {
        setupToolButton(findById("addGroupButton"),"plus");
        setupToolButton(findById("addUserButton"), "user-plus");
        setupToolButton(findById("editStructureButton"),"folder");

        ToggleButton gpNormal = findById("gpNormal");
        setupGroupToggle(gpNormal, "user");
        setupGroupToggle(findById("gpDeleted"), "trash-alt");
        userGroupToggle.selectToggle(gpNormal);
    }

    private void setupGroupToggle(ToggleButton button, String icon) {
        setupToolButton(button,icon);
        userGroupToggle.getToggles().add(button);
    }

    private void setupToolButton(ButtonBase toolButton, String icon) {
        toolButton.setFont(fontawsome5Service.getSolidFont(FontSize.VERY_SMALL));
        toolButton.setText(fontawsome5Service.getFontIcon(icon));
        toolButton.setPadding(new Insets(4));
    }

    public ToggleGroup getUserGroupToggle() {
        return userGroupToggle;
    }

}
