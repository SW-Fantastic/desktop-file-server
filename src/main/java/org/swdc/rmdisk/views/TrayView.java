package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import javafx.stage.StageStyle;
import org.swdc.fx.view.AbstractSwingDialogView;
import org.swdc.fx.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

@View(viewLocation = "views/main/TrayView.fxml",windowStyle = StageStyle.UNDECORATED)
public class TrayView extends AbstractSwingDialogView {

    @PostConstruct
    public void init() {
        JDialog stage = getStage();
        stage.setSize(200,168);
        stage.setAlwaysOnTop(true);
        getView().setOnMouseExited(e -> {
            this.hide();
        });
    }

    public void show(MouseEvent e) {
        JDialog frame = getStage();
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
