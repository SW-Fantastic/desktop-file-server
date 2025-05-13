package org.swdc.rmdisk.views;

import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBase;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(
        viewLocation = "views/main/ServerLogsTabView.fxml",
        stage=false
)
public class ServerLogTabView extends AbstractView {

    @Inject
    private Fontawsome5Service fontawsome5Service;

    void setupToolButton(ButtonBase toolButton, String icon) {
        toolButton.setFont(fontawsome5Service.getSolidFont(FontSize.VERY_SMALL));
        toolButton.setText(fontawsome5Service.getFontIcon(icon));
        toolButton.setPadding(new Insets(4));
    }

}
