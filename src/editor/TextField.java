package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public abstract class TextField extends JTextField implements KeyListener {
    
    protected abstract void onEscape(KeyEvent e);
    protected abstract void onEnter(KeyEvent e);

    public TextField() {
        addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            onEscape(e);
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER && getText().length() > 0) {
            onEnter(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }
}