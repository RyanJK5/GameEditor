package editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class EditorPanel extends RectangularObject implements MouseMotionListener, MouseListener, KeyListener {
    
    public static final int CROSS_SIZE = 15;
    public static final int BORDER_SIZE = 5;
    public boolean visible = true;

    public final void repaint(Graphics g) {
        if (visible) {
            paint(g);
            g.setColor(Color.WHITE);
            g.setFont(EditorWindow.FONT);
        }
    }

    protected abstract void paint(Graphics g);

    public void paintBox(Graphics g) {
        g.setColor(new Color(105, 105, 105));
        g.fillRect(x, y, width, height);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + BORDER_SIZE, y + BORDER_SIZE, width - BORDER_SIZE * 2, height - BORDER_SIZE * 2);
        g.setColor(Color.WHITE);
    }

    public void paintTransparentBackground(Graphics g, Rectangle bounds) {
        Color lightGray = new Color(203, 203, 203);
        final float squareWidth = 16f;
        for (int y = 0; y < Math.ceil(bounds.height / squareWidth); y++) {
            for (int x = 0; x < Math.ceil(bounds.width / squareWidth); x++) {
                if ((x + y) % 2 == 0) {
                    g.setColor(lightGray);
                }
                g.fillRect(bounds.x + (int) (x * squareWidth), bounds.y + (int) (y * squareWidth), (int) squareWidth, (int) squareWidth);
                g.setColor(Color.WHITE);
            }
        }
    }

    public void addListenersTo(EditorWindow window) {
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
        window.addKeyListener(this);
    }

    public void removeListenersFrom(EditorWindow window) {
        window.removeMouseListener(this);
        window.removeMouseMotionListener(this);
        window.removeKeyListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

}