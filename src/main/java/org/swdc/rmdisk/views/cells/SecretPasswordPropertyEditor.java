package org.swdc.rmdisk.views.cells;

import com.password4j.BcryptFunction;
import com.password4j.Password;
import com.password4j.SaltGenerator;
import com.password4j.types.Bcrypt;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SecretPasswordPropertyEditor extends PropEditorView {

    private PasswordField passwordField;

    private SimpleObjectProperty<String> password;

    /**
     * 正则表达式，用于匹配有效的bcrypt hash。
     */
    private final Pattern pattern = Pattern.compile("^\\$2[ab]\\$.{56}$");

    public SecretPasswordPropertyEditor(PropertySheet.Item item) {
        super(item);
    }

    public SecretPasswordPropertyEditor(ConfigPropertiesItem item) {
        super(item);
    }

    @Override
    public Node getEditor() {
        if (passwordField == null) {
            password = new SimpleObjectProperty<>();
            passwordField = new PasswordField();
            passwordField.setPromptText("******");
            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    BcryptFunction function = BcryptFunction.getInstance(Bcrypt.B, 12);
                    String hashed = function.hash(
                            newValue.getBytes(StandardCharsets.UTF_8),
                            SaltGenerator.generate()
                    ).getResult();
                    password.set(hashed);
                    this.getItem().setValue(hashed);
                }

            });
        }
        return null;
    }

    @Override
    public Object getValue() {
        if (passwordField != null) {
            getEditor();
        }
        return password.get();
    }

    @Override
    public void setValue(Object value) {
        if (!(value instanceof String)) {
            return;
        }
        if (passwordField == null) {
            getEditor();
        }
        if (!pattern.matcher((String) value).matches()) {
            // 不是有效的bcrypt hash，应该是明文密码，直接加密它
            BcryptFunction function = BcryptFunction.getInstance(Bcrypt.B, 12);
            String hashed = function.hash(
                    ((String) value).getBytes(StandardCharsets.UTF_8),
                    SaltGenerator.generate()
            ).getResult();
            password.set(hashed);
            return;
        }
        password.set((String) value);
    }
}
