package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import javafx.stage.StageStyle;
import org.swdc.fx.view.AbstractSwingDialogView;
import org.swdc.fx.view.AbstractSwingView;
import org.swdc.fx.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

@View(viewLocation = "views/main/TrayView.fxml",windowStyle = StageStyle.TRANSPARENT)
public class TrayView extends AbstractSwingView {

    @PostConstruct
    public void init() {
        JFrame stage = getStage();
        stage.setSize(200,168);
        stage.setAlwaysOnTop(true);
        getView().setOnMouseExited(e -> {
            this.hide();
        });
    }

    public void show(MouseEvent e) {
        JFrame frame = getStage();
        GraphicsConfiguration configuration = frame.getGraphicsConfiguration();
        AffineTransform transform = configuration.getDefaultTransform();

        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = environment.getCenterPoint();

        centerPoint.setLocation(centerPoint.x / transform.getScaleX(), centerPoint.y / transform.getScaleY());
        if (e.getYOnScreen() < centerPoint.getY()) {
            frame.setLocation(
                    Double.valueOf(e.getXOnScreen() / transform.getScaleX()).intValue(),
                    0
            );
        } else {
            frame.setLocation(
                    Double.valueOf(e.getXOnScreen() / transform.getScaleX()).intValue() - frame.getWidth(),
                    Double.valueOf(e.getYOnScreen() / transform.getScaleY()).intValue() - frame.getHeight()
            );
        }
        this.show();
    }

}
