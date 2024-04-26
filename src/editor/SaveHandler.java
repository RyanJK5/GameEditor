package editor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class SaveHandler {
    
    public void writeCurrentLevel(GameWorld gameWorld, TiledWorld tiledWorld, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writeTiledWorldTileset(writer, tiledWorld);
            writeTiledWorld(writer, tiledWorld);
            writeObjects(writer, gameWorld);
        }
    }

    public TiledWorld readTiledWorld(String filePath) throws IOException {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            Tileset tileset = readTileset(scanner);
            return readTiledWorld(scanner, tileset);
        }
    }

    private TiledWorld readTiledWorld(Scanner scanner, Tileset tileset) {
        List<String> lines = new ArrayList<>();
        while (true) {
            String line = scanner.nextLine();
            line = line.trim();
            if (line.contains("]")) {
                break;
            }
            lines.add(line);
        }

        int worldWidth = 0;
        for (char c : lines.get(0).toCharArray()) {
            if (c == ',') {
                worldWidth++;
            }
        }
        
        TiledWorld tiledWorld = new TiledWorld(worldWidth, lines.size());
        tiledWorld.setTileset(tileset);
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            int index = 0;
            int x = 0;
            while (index < line.length()) {
                int nextQuotationMark = line.indexOf("\"", index + 1);
                tiledWorld.setTile(x, y, Integer.parseInt(line.substring(index + 1, nextQuotationMark)));
                index = nextQuotationMark + 2;
                x++;
            }
        }
        return tiledWorld;
    }

    private Tileset readTileset(Scanner scanner) throws IOException {
        String imgPath = getValue(scanner.nextLine());
        int width = Integer.parseInt(getValue(scanner.nextLine()));
        int height = Integer.parseInt(getValue(scanner.nextLine()));
        int tileSize = Integer.parseInt(getValue(scanner.nextLine()));

        BufferedImage spritesheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        spritesheet.getGraphics().drawImage(ImageIO.read(new File(imgPath)), 0, 0, null);

        Tileset tileset = new Tileset(spritesheet, imgPath, tileSize);
        scanner.nextLine();
        return tileset;
    }

    private String getKey(String fullLine) {
        return fullLine.substring(1, fullLine.indexOf("\"", 1));
    }
    
    private String getValue(String fullLine) {
        return fullLine.substring(fullLine.indexOf("\"", fullLine.indexOf(":")) + 1, fullLine.length() - 2);
    }

    public GameWorld readGameWorld(String filePath) throws IOException {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            String nextLine = scanner.nextLine();
            while (!nextLine.contains("object-data")) {
                nextLine = scanner.nextLine();
            }
            nextLine = scanner.nextLine();

            GameWorld gameWorld = new GameWorld();
            while (scanner.hasNextLine()) {
                List<String> lines = new ArrayList<>();
                while (!nextLine.equals("\t}")) {
                    lines.add(nextLine);
                    nextLine = scanner.nextLine();
                }
                nextLine = scanner.nextLine();
                gameWorld.add(readObject(lines.toArray(new String[0])));
            }
            return gameWorld;
        }
    }

    private GameObject readObject(String[] lines) throws IOException {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }

        GameObject obj = new GameObject(getKey(lines[0]));
        obj.x = Integer.parseInt(getValue(lines[1]));
        obj.y = Integer.parseInt(getValue(lines[2]));
        obj.width = Integer.parseInt(getValue(lines[3]));
        obj.height = Integer.parseInt(getValue(lines[4]));
        obj.imgPath = getValue(lines[5]);
        if (obj.imgPath.length() > 0) {
            obj.sprite = ImageIO.read(new File(obj.imgPath));
        }

        for (int i = 7; i < lines.length - 1; i++) {
            obj.properties.put(getKey(lines[i]), getValue(lines[i]));
        }

        return obj;
    }

    private void writeTiledWorldTileset(FileWriter writer, TiledWorld tiledWorld) throws IOException {
        writer.write("\"tileset\":\"" + tiledWorld.tileset.filePath + "\",\n");
        writer.write("\"tileset-width\":\"" + tiledWorld.tileset.fullImage.getWidth() + "\",\n");
        writer.write("\"tileset-height\":\"" + tiledWorld.tileset.fullImage.getHeight() + "\",\n");
        writer.write("\"tile-size\":\"" + tiledWorld.tileset.tileSize + "\",\n");
    }

    private void writeTiledWorld(FileWriter writer, TiledWorld tiledWorld) throws IOException {
        writer.write("\"map-data\": [");
        for (int y = 0; y < tiledWorld.getHeight(); y++) {
            writer.write("\n\t");
            for (int x = 0; x < tiledWorld.getWidth(); x++) {
                writer.write("\"" + tiledWorld.getTile(x, y) + "\"");
                if (x != tiledWorld.getWidth() - 1 || y != tiledWorld.getHeight() - 1) {
                    writer.write(",");
                }
            }
        }
        writer.write("\n],\n");
    }

    private void writeObjects(FileWriter writer, GameWorld gameWorld) throws IOException {
        writer.write("\"object-data\": [");
        for (GameObject obj : gameWorld) {
            writer.write("\n\t\"" + obj.name + "\": {");
            writer.write("\n\t\t\"x\":\"" + obj.x + "\",");
            writer.write("\n\t\t\"y\":\"" + obj.y + "\",");
            writer.write("\n\t\t\"width\":\"" + obj.width + "\",");
            writer.write("\n\t\t\"height\":\"" + obj.height + "\",");
            writer.write("\n\t\t\"sprite\":\"" + obj.imgPath + "\",");
            writer.write("\n\t\t\"properties\": (");
            for (String key : obj.properties.keySet()) {
                writer.write("\n\t\t\t\"" + key + "\":\"" + obj.properties.get(key) + "\",");
            }
            writer.write("\n\t\t)");
            writer.write("\n\t}");
        }
        writer.write("\n]");
    }
}