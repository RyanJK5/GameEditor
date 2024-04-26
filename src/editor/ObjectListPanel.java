package editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class ObjectListPanel extends EditorPanel {

    private EditorWindow window;
    private GameWorld gameWorld;
    private Point mousePos;

    private static final int FONT_SIZE = EditorWindow.FONT.getSize();
    private static final int TITLE_BUFFER = EditorWindow.BOLD_FONT.getSize() + 2;

    public ObjectListPanel(EditorWindow window, GameWorld gameWorld) {
        this.window = window;
        this.gameWorld = gameWorld;
        mousePos = new Point(0, 0);
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void paint(Graphics g) {
        paintBox(g);
        
        ((Graphics2D) g).setStroke(new BasicStroke(3));
        g.setColor(Color.GREEN);
        g.drawLine(plusBounds().x + CROSS_SIZE / 2, plusBounds().y, plusBounds().x + CROSS_SIZE / 2, plusBounds().y + CROSS_SIZE);
        g.drawLine(plusBounds().x + CROSS_SIZE, plusBounds().y + CROSS_SIZE / 2, plusBounds().x, plusBounds().y + CROSS_SIZE / 2);
        
        g.setFont(EditorWindow.BOLD_FONT);
        
        g.setColor(Color.WHITE);
        g.drawString("Objects", BORDER_SIZE + 2, BORDER_SIZE + 20);

        g.setColor(tileBounds().contains(mousePos) ? Color.WHITE : Color.LIGHT_GRAY);
        g.drawString("Tiles", BORDER_SIZE + 2 + 20 * 7, BORDER_SIZE + 20);
        
        g.setFont(EditorWindow.FONT);
        int i = 0;
        for (GameObject obj : gameWorld) {
            i++;
            g.setColor(Color.WHITE);
            g.drawString(obj.name, BORDER_SIZE + 2, y + BORDER_SIZE + i * FONT_SIZE + TITLE_BUFFER);
            
            if (!obj.highlighted()) {
                continue;
            }
            g.setColor(new Color(1, 1, 1, 0.5f));
            final Rectangle detectBounds = detectBounds(i);
            g.fillRect(detectBounds.x, detectBounds.y, detectBounds.width, detectBounds.height);

            final Rectangle cross = crossBounds(i);
            g.setColor(Color.RED);
            g.drawLine(cross.x, cross.y, cross.x + CROSS_SIZE, cross.y + CROSS_SIZE);
            g.drawLine(cross.x + CROSS_SIZE, cross.y, cross.x, cross.y + CROSS_SIZE);
        }
    }

    private Rectangle detectBounds(int index) {
        return new Rectangle(x, y + BORDER_SIZE + (index - 1) * FONT_SIZE + TITLE_BUFFER + 2, width, FONT_SIZE);
    }

    private Rectangle crossBounds(int index) {
        return new Rectangle(x + width - CROSS_SIZE - 10, y + BORDER_SIZE + (index - 1) * FONT_SIZE + TITLE_BUFFER + 5, CROSS_SIZE, CROSS_SIZE);
    }

    private Rectangle plusBounds() {
        return new Rectangle(x + width - CROSS_SIZE - 10, y + BORDER_SIZE + 5, CROSS_SIZE, CROSS_SIZE);
    }

    private Rectangle tileBounds() {
        return new Rectangle(BORDER_SIZE + 140, BORDER_SIZE, 140, 25);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        boolean firstHighlight = false;
        int i = 0;
        for (GameObject obj : gameWorld) {
            final Rectangle detectBounds = new Rectangle(x, y + BORDER_SIZE + i * FONT_SIZE + TITLE_BUFFER + 2, width, FONT_SIZE + 2);
            if (!firstHighlight & detectBounds.contains(mousePos)) {
                obj.highlight(true);
                firstHighlight = true;          
            }
            else {
                obj.highlight(false);
            }
            i++;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (tileBounds().contains(e.getPoint())) {
            window.toggleDisplayMode();
            return;
        }
        
        final GamePanel gamePanel = (GamePanel) window.getPanel(EditorWindow.GAME_PANEL);
        if (gamePanel.getTiledWorld().getWidth() == 0 || gamePanel.getTiledWorld().getHeight() == 0 || gamePanel.getTiledWorld().tileset == null) {
            return;
        }

        if (plusBounds().contains(e.getPoint())) {
            gameWorld.add(new GameObject("New Object"));
            return;
        }

        int i = 0;
        for (GameObject obj : gameWorld) {
            i++;
            
            if (crossBounds(i).contains(mousePos) && obj.highlighted()) {
                gameWorld.remove(obj);
                return;
            }

            final Rectangle detectBounds = detectBounds(i);
            if (detectBounds.contains(mousePos) && obj.highlighted()) {
                obj.lockHighlight(!obj.highlightLocked());
                for (GameObject oObj : gameWorld) {
                    if (obj == oObj) { 
                        continue;
                    }
                    oObj.lockHighlight(false);
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() != KeyEvent.VK_ESCAPE) {
            return;
        }
        int i = 0;
        for (GameObject obj : gameWorld) {
            obj.lockHighlight(false);
            final Rectangle detectBounds = new Rectangle(x, y + BORDER_SIZE + i * FONT_SIZE + TITLE_BUFFER + 2, width, FONT_SIZE + 2);
            if (detectBounds.contains(mousePos)) {
                obj.highlight(true);                
            }
            else {
                obj.highlight(false);
            }    
            i++;
        }
    }
}