package editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWorld implements Iterable<GameObject> {
    
    private final List<GameObject> objects = new ArrayList<>();

    public static final int MAX_SIZE = 28;

    public void add(GameObject obj) {
        if (objects.size() >= MAX_SIZE) {
            return;
        }
        objects.add(obj);
        objects.sort(null);
    }

    public void remove(GameObject obj) {
        objects.remove(obj);
        objects.sort(null);
    }

    public void paintAll(Graphics g) {
        for (GameObject object : objects) {
            g.setColor(Color.WHITE);
            ((Graphics2D) g).setStroke(new BasicStroke(1));
            object.paint(g);
        }
    }

    @Override
    public Iterator<GameObject> iterator() {
        return objects.iterator();
    }
}