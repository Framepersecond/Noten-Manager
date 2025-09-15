import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class AutoCompleteComboBox<T> extends JComboBox<T> {
    private DefaultComboBoxModel<T> model;
    private java.util.List<T> source;

    public AutoCompleteComboBox(List<T> items) {
        super(new DefaultComboBoxModel<>());
        this.model = (DefaultComboBoxModel<T>) getModel();
        setEditable(true);
        setItems(items);
        JTextComponent editor = (JTextComponent) getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = editor.getText();
                filter(text);
                editor.setText(text);
                editor.setCaretPosition(text.length());
                setPopupVisible(true);
            }
        });
    }

    public void setItems(List<T> items) {
        this.source = items;
        model.removeAllElements();
        for (T it : items) model.addElement(it);
    }

    private void filter(String query) {
        model.removeAllElements();
        String q = query == null ? "" : query.toLowerCase();
        for (T it : source) {
            String s = it.toString().toLowerCase();
            if (s.contains(q)) {
                model.addElement(it);
            }
        }
        if (model.getSize() > 0) setSelectedIndex(0);
    }
}
