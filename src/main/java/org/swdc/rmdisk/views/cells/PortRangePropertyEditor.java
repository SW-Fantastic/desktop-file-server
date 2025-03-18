package org.swdc.rmdisk.views.cells;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

public class PortRangePropertyEditor extends PropEditorView {

    protected TextField portRangeA;

    protected TextField portRangeB;

    private HBox hbox;

    public PortRangePropertyEditor(PropertySheet.Item item) {
        super(item);
    }

    public PortRangePropertyEditor(ConfigPropertiesItem item) {
        super(item);
    }


    @Override
    public Node getEditor() {
        if (hbox == null) {
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_LEFT);
            portRangeA = new TextField();
            portRangeA.setMaxWidth(80);
            portRangeA.textProperty().addListener(((observable, oldValue, newValue) -> {
                try {
                    Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    portRangeA.setText(oldValue);
                    return;
                }
                getItem().setValue(portRangeA.getText() + "-" + portRangeB.getText());

            }));

            portRangeB = new TextField();
            portRangeB.setMaxWidth(80);
            portRangeB.textProperty().addListener(((observable, oldValue, newValue) -> {
                try {
                    Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    portRangeB.setText(oldValue);
                    return;
                }
                getItem().setValue(portRangeA.getText() + "-" + portRangeB.getText());
            }));

            Label label = new Label("-");
            hbox.getChildren().addAll(portRangeA,label, portRangeB);
            hbox.setSpacing(8);

        }

        return hbox;
    }

    @Override
    public Object getValue() {
        if (hbox == null) {
            getEditor();
        }
        return portRangeA.getText() + "-" + portRangeB.getText();
    }

    @Override
    public void setValue(Object o) {
        if (hbox == null) {
            getEditor();
        }
        if (o == null) {
            return;
        }

        String text = o.toString();
        if (!text.contains("-")) {
            return;
        }
        String[] ports = o.toString().split("-");
        portRangeA.setText(ports[0]);
        portRangeB.setText(ports[1]);
    }

}
