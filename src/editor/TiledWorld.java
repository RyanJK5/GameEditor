package editor;

import java.awt.Graphics;

public class TiledWorld {
    
    private int[] tiles;
    public Tileset tileset;
    private int width;
    private int height;

    public TiledWorld(int width, int height) {
        tiles = new int[width * height];
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        int[] newArr = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < Math.min(width, this.width); x++) {
                newArr[x + y * width] = tiles[x + y * this.width];
            }
        }
        this.width = width;
        tiles = newArr;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        int[] newArr = new int[width * height];
        for (int y = 0; y < Math.min(height, this.height); y++) {
            for (int x = 0; x < width; x++) {
                newArr[x + y * width] = tiles[x + y * width];
            }
        }
        this.height = height;
        tiles = newArr;
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] > tileset.maxID()) {
                tiles[i] = 0;
            }
        }
    }

    public void setTile(int x, int y, int value) {
        tiles[x + y * width] = value;
    }

    public int getTile(int x, int y) {
        return tiles[x + y * width];
    }

    public void paint(Graphics g) {
        if (tileset == null) {
            return;
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                g.drawImage(tileset.getSprite(tiles[x + y * width]), x * tileset.tileSize, y * tileset.tileSize, null);
            }
        }
    }
}