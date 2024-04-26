package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class GamePanel extends EditorPanel {
   
    private EditorWindow window;

    private GameWorld gameWorld;
    private TiledWorld tiledWorld;
    public int selectedTile;

    private GameObject dragObj;
    private Dimension dragDif;
    private Point mousePos;

    public GamePanel(EditorWindow window, GameWorld gameWorld, TiledWorld tiledWorld) {
        this.window = window;
        this.gameWorld = gameWorld;
        this.tiledWorld = tiledWorld;
        mousePos = new Point();
    }

    public TiledWorld getTiledWorld() {
        return tiledWorld;
    }

    public void setTiledWorld(TiledWorld tiledWorld) {
        this.tiledWorld = tiledWorld;
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void setTileset(Tileset tileset) {
        tiledWorld.setTileset(tileset);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(x, y, width, height);
        g.translate(x, y);
        if (tiledWorld != null && tiledWorld.tileset != null) {
            paintTransparentBackground(g, new Rectangle(
                0, 
                0, 
                tiledWorld.getWidth() * tiledWorld.tileset.tileSize, 
                tiledWorld.getHeight() * tiledWorld.tileset.tileSize
            ));
            tiledWorld.paint(g);

            if (!window.objectDisplayMode()) {
                final int tileSize = tiledWorld.tileset.tileSize;
                for (int y = 0; y < tiledWorld.getHeight(); y++) {
                    for (int x = 0; x < tiledWorld.getWidth(); x++) {
                        final Rectangle rect = new Rectangle(x * tileSize, y * tileSize, tileSize, tileSize);
                        if (rect.contains(mousePos)) {
                            g.drawRect(rect.x, rect.y, rect.width, rect.height);
                        }
                    }
                }
            }
        }
        gameWorld.paintAll(g);
        g.translate(-x, -y);
    }

    private Point convertScreenToRelative(Point p) {
        return new Point(p.x - x, p.y - y);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        final Point mousePos = convertScreenToRelative(e.getPoint());

        if (!window.objectDisplayMode() && tiledWorld.tileset != null) {
            setTileAt(mousePos);
            return;
        }

        for (GameObject obj : gameWorld) {
            if (obj.getBounds().contains(mousePos)) {
                obj.lockHighlight(true);
                dragObj = obj;
                dragDif = new Dimension(mousePos.x - obj.x, mousePos.y - obj.y);
                return;
            }
        }
        dragObj = null;
        dragDif = null;
    }

    private void setTileAt(final Point mousePos) {
        final int tileSize = tiledWorld.tileset.tileSize;
        for (int y = 0; y < tiledWorld.getHeight(); y++) {
            for (int x = 0; x < tiledWorld.getWidth(); x++) {
                if (new Rectangle(x * tileSize, y * tileSize, tileSize, tileSize).contains(mousePos)) {
                    tiledWorld.setTile(x, y, selectedTile);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragObj = null;
        dragDif = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePos = convertScreenToRelative(e.getPoint());
        if (!window.objectDisplayMode() && tiledWorld.tileset != null) {
            setTileAt(convertScreenToRelative(e.getPoint()));
            return;
        }
        if (dragObj != null) {
            final Point mousePos = convertScreenToRelative(e.getPoint());
            dragObj.setLocation(mousePos.x - dragDif.width, mousePos.y - dragDif.height);
            dragObj.pushInBounds(tiledWorld.getWidth() * tiledWorld.tileset.tileSize, tiledWorld.getHeight() * tiledWorld.tileset.tileSize);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!getBounds().contains(e.getPoint())) {
            return;
        }
        mousePos = convertScreenToRelative(e.getPoint());
        for (GameObject obj : gameWorld) {
            obj.highlight(obj.getBounds().contains(convertScreenToRelative(e.getPoint())));
        }
    }
}