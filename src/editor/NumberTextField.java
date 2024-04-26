package editor;

import javax.swing.text.PlainDocument;

public abstract class NumberTextField extends TextField {
    
    public NumberTextField(EditorWindow window, int max) {
        setBounds(0, 0, 54, EditorWindow.BOLD_FONT.getSize());
        PlainDocument doc = (PlainDocument) getDocument();
        doc.setDocumentFilter(new NumberFilter(max, false));
        window.add(this);
        addKeyListener(this);
    }

    public void setMax(int max) {
        PlainDocument doc = (PlainDocument) getDocument();
        doc.setDocumentFilter(new NumberFilter(max, false));
    }
}