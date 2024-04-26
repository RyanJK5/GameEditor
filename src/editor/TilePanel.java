package editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class TilePanel extends EditorPanel {

    private EditorWindow window;
    private GamePanel gamePanel;
    private Tileset tileset;

    private Point mousePos;
    private Rectangle highlightBounds;

    public TilePanel(EditorWindow window) {
        this.window = window;
        this.gamePanel = (GamePanel) window.getPanel(EditorWindow.GAME_PANEL);
        mousePos = new Point();
    }

    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;
        gamePanel.setTileset(tileset);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }

    @Override
    protected void paint(Graphics g) {
        paintBox(g);
        g.setFont(EditorWindow.BOLD_FONT);
        
        g.setColor(listBounds().contains(mousePos) ? Color.WHITE : Color.LIGHT_GRAY);
        g.drawString("Objects", BORDER_SIZE + 2, BORDER_SIZE + 20);
        
        g.setColor(Color.WHITE);
        g.drawString("Tiles", BORDER_SIZE + 2 + 20 * 7, BORDER_SIZE + 20);

        if (tileset == null) {
            return;
        }
        final int yPos = BORDER_SIZE + 25;
        g.drawImage(tileset.fullImage.getSubimage(
            0, 
            0, 
            Math.min(tileset.fullImage.getWidth(), width - BORDER_SIZE * 2), 
            Math.min(tileset.fullImage.getHeight(), height - yPos)), 
        BORDER_SIZE, BORDER_SIZE * 2 + 20, null);

        if (highlightBounds != null) {
            g.drawRect(highlightBounds.x, highlightBounds.y, highlightBounds.width, highlightBounds.height);
        }
    }

    private Rectangle listBounds() {
        return new Rectangle(BORDER_SIZE, BORDER_SIZE, 140, 25);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();

        if (tileset == null) {
            return;
        }
        final int xPos = BORDER_SIZE;
        final int yPos = BORDER_SIZE + 25;
        for (int y = yPos; y + tileset.tileSize < height - BORDER_SIZE; y += tileset.tileSize) {
            for (int x = xPos; x + tileset.tileSize < width - BORDER_SIZE; x += tileset.tileSize) {
                Rectangle highlightBounds = new Rectangle(x, y, tileset.tileSize, tileset.tileSize);
                if (highlightBounds.contains(mousePos) && 
                highlightBounds.intersects(BORDER_SIZE, BORDER_SIZE * 2 + 20, tileset.fullImage.getWidth(), tileset.fullImage.getHeight())) {
                    this.highlightBounds = highlightBounds;
                    return;
                }
            }
        }
        highlightBounds = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (listBounds().contains(mousePos)) {
            window.toggleDisplayMode();
            return;
        }

        if (highlightBounds != null) {
            final int xPos = BORDER_SIZE;
            final int yPos = BORDER_SIZE + 25;
            gamePanel.selectedTile = 
                ((highlightBounds.x - xPos) / tileset.tileSize) + (((highlightBounds.y - yPos) / tileset.tileSize) * (tileset.fullImage.getWidth() / tileset.tileSize))
            ;
        }
    }
}