package editor;

import java.awt.image.BufferedImage;

public class Tileset {
    
    public final BufferedImage fullImage;
    public final String filePath;
    private BufferedImage[] tileSprites;
    public final int tileSize;

    public Tileset(BufferedImage spritesheet, String filePath, int tileSize) {
        fullImage = spritesheet;
        this.filePath = filePath;
        tileSprites = new BufferedImage[(spritesheet.getWidth() / tileSize) * (spritesheet.getHeight() / tileSize)];
        int index = 0;
        for (int y = 0; y + tileSize <= spritesheet.getHeight(); y += tileSize) {
            for (int x = 0; x + tileSize <= spritesheet.getWidth(); x += tileSize) {
                tileSprites[index] = spritesheet.getSubimage(x, y, tileSize, tileSize);
                index++;
            }
        }
        this.tileSize = tileSize;
    }

    public int maxID() {
        return tileSprites.length;
    }

    public BufferedImage getSprite(int tileID) {
        return tileSprites[tileID];
    }
}