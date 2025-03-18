package org.swdc.rmdisk.views.cells;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

public class PortPropertyEditor extends PropEditorView {

    protected TextField field;

    public PortPropertyEditor(PropertySheet.Item item) {
        super(item);
    }

    public PortPropertyEditor(ConfigPropertiesItem item) {
        super(item);
    }


    @Override
    public Node getEditor() {
        if (field == null) {
            field = new TextField();
            field.textProperty().addListener(((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    try {
                        Integer.parseInt(newValue);
                        this.getItem().setValue(newValue);
                    } catch (Exception e) {
                        field.setText(getItem().getValue().toString());
                    }
                }
            }));
        }
        return field;
    }

    @Override
    public Object getValue() {
        if (field == null) {
            field = (TextField) this.getEditor();
        }
        return field.getText();
    }

    @Override
    public void setValue(Object o) {
        if (field == null) {
            field = (TextField) this.getEditor();
        }
        field.setText(o.toString());
    }
}
