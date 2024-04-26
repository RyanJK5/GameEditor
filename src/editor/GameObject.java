package editor;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;

public class GameObject extends RectangularObject implements Comparable<GameObject> {

    public String name;
    private boolean highlighted;
    private boolean highlightLocked;
    
    public BufferedImage sprite;
    public String imgPath;
    public LinkedHashMap<String, Object> properties;

    public GameObject(String name) {
        this.name = name;
        imgPath = "";
        properties = new LinkedHashMap<>();
    }

    public void paint(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        }
    }

    public void highlight(boolean highlighted) {
        if (!highlightLocked) {
            this.highlighted = highlighted;
        }
    }

    public boolean highlighted() {
        return highlighted;
    }

    public void lockHighlight(boolean lock) {
        highlightLocked = lock;
    }

    public boolean highlightLocked() {
        return highlightLocked;
    }

    @Override
    public int compareTo(GameObject obj) {
        return name.compareTo(obj.name);
    }

    public void pushInBounds(int worldWidth, int worldHeight) {
        if (x + width > worldWidth) {
            x = worldWidth - width;
        }
        else if (x < 0) {
            x = 0;
        }
        if (y + height > worldHeight) {
            y = worldHeight - height;
        }
        else if (y < 0) {
            y = 0;
        }
    }
}