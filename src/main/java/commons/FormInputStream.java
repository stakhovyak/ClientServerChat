package commons;

import javax.swing.*;
import java.io.InputStream;

/**
 * Custom Input stream to display data in GUI.
 */
public class FormInputStream extends InputStream {
    private final JTextField textField;
    private final String textContent;
    private int position;

    public FormInputStream(JTextField textField) {
        this.textField = textField;
        this.textContent = this.textField.getText();
        this.position = 0;
    }

    @Override
    public int read() {
        if (position >= textContent.length()) {
            return -1; // end of stream
        }

        char c = textContent.charAt(position);
        position++;
        return (int) c;
    }
}