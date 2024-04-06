package commons;

import javax.swing.*;
import java.io.PrintStream;

public class FormPrintStream extends PrintStream {
    private final JTextArea textArea;

    public FormPrintStream(JTextArea textArea) {
        super(System.out); // Use System.out as the underlying output stream
        this.textArea = textArea;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        String str = new String(b, off, len);
        SwingUtilities.invokeLater(() -> textArea.append(str));
    }
}
