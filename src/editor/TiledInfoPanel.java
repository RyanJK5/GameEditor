package editor;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class TiledInfoPanel extends EditorPanel {

    private GamePanel gamePanel;
    private TilePanel tilePanel;

    private BufferedImage tilesetImg;

    private JTextField fileName;
    private JButton openFile;
    private JFileChooser fileChooser;
    private NumberTextField tileSize;

    private NumberTextField mapWidth;
    private NumberTextField mapHeight;

    public TiledInfoPanel(EditorWindow window) {
        tilePanel = (TilePanel) window.getPanel(EditorWindow.TILE_PANEL);
        gamePanel = (GamePanel) window.getPanel(EditorWindow.GAME_PANEL);
        initFileChooser(window);
        initTileSize(window);
        initMapBounds(window);
        setSwingComponentsVisible(false);
    }

    public void setTiledWorld(TiledWorld tiledWorld) {
        fileName.setText(tiledWorld.tileset.filePath);

        tilesetImg = tiledWorld.tileset.fullImage;
        
        mapWidth.setMax(Integer.MAX_VALUE);
        mapWidth.setText("" + tiledWorld.getWidth());
        mapHeight.setMax(Integer.MAX_VALUE);
        mapHeight.setText("" + tiledWorld.getHeight());
        
        tileSize.setText("" + tiledWorld.tileset.tileSize);
        tileSize.onEnter(null);
        

        if (fileName.isVisible()) {
            tileSize.setVisible(true);
        }
    }

    private void initFileChooser(EditorWindow window) {
        fileChooser = new JFileChooser("img");
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".png") || f.isDirectory();
            }
            @Override
            public String getDescription() {
                return ".png";
            }
        });
        fileChooser.setAcceptAllFileFilterUsed(false);

        fileName = new JTextField();
        fileName.setBounds(0, 0, 100, EditorWindow.BOLD_FONT.getSize());
        fileName.setEditable(false);
        window.add(fileName);

        openFile = new JButton();
        openFile.setBounds(0, 0, EditorWindow.BOLD_FONT.getSize() + 10, EditorWindow.BOLD_FONT.getSize());
        openFile.addActionListener(e -> {
            fileChooser.showOpenDialog(window);
            if (fileChooser.getSelectedFile() == null) {
                return;
            }
            fileName.setText("" + fileChooser.getSelectedFile());
            try {
                tilesetImg = ImageIO.read(fileChooser.getSelectedFile());
                tileSize.setVisible(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        window.add(openFile);
    }

    private void initTileSize(EditorWindow window) {
        tileSize = new NumberTextField(window, 48) {
            @Override
            protected void onEscape(KeyEvent e) {
                if (tilePanel.getTileset() != null) {
                    setText("" + tilePanel.getTileset().tileSize);
                }
                window.requestFocusInWindow();
            }

            @Override
            protected void onEnter(KeyEvent e) {
                final int newSize = Integer.parseInt(getText());
                BufferedImage newTileset = new BufferedImage(
                    (int) Math.ceil(tilesetImg.getWidth() / (float) newSize) * newSize, 
                    (int) Math.ceil(tilesetImg.getHeight() / (float) newSize) * newSize, 
                    BufferedImage.TYPE_INT_ARGB
                );
                newTileset.getGraphics().drawImage(tilesetImg, 0, 0, null);

                tilePanel.setTileset(new Tileset(newTileset, fileName.getText(), newSize));
                
                final int widthMax = gamePanel.getWidth() / tilePanel.getTileset().tileSize;
                gamePanel.getTiledWorld().setWidth(Math.min(Integer.parseInt(mapWidth.getText()), widthMax));
                mapWidth.setText("" + gamePanel.getTiledWorld().getWidth());
                mapWidth.setMax(widthMax);

                final int heightMax = gamePanel.getHeight() / tilePanel.getTileset().tileSize;
                gamePanel.getTiledWorld().setHeight(Math.min(Integer.parseInt(mapHeight.getText()), heightMax));
                mapHeight.setText("" + gamePanel.getTiledWorld().getHeight());
                mapHeight.setMax(heightMax);
                
                window.requestFocusInWindow();
            }
        };
        tileSize.setVisible(false);
    }

    private void initMapBounds(EditorWindow window) {
        mapWidth = new NumberTextField(window, gamePanel.getWidth() / 48) {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + gamePanel.getTiledWorld().getWidth());
                window.requestFocusInWindow();
            }

            @Override
            protected void onEnter(KeyEvent e) {
                gamePanel.getTiledWorld().setWidth(Integer.parseInt(getText()));
                window.requestFocusInWindow();
            }
        };
        mapWidth.setText("0");
        mapHeight = new NumberTextField(window, gamePanel.getHeight() / 48) {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + gamePanel.getTiledWorld().getHeight());
                window.requestFocusInWindow();
            }

            @Override
            protected void onEnter(KeyEvent e) {
                gamePanel.getTiledWorld().setHeight(Integer.parseInt(getText()));
                window.requestFocusInWindow();
            }
        };
        mapHeight.setText("0");
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        setSwingComponentLocations();
    }

    @Override
    public void setBounds(Rectangle r) {
        super.setBounds(r);
        setSwingComponentLocations();
    }

    private void setSwingComponentLocations() {
        fileName.setLocation(x + BORDER_SIZE * 2, y + BORDER_SIZE * 2 + fileName.getHeight());
        openFile.setLocation(fileName.getX() + fileName.getWidth(), fileName.getY());
        tileSize.setLocation(openFile.getX() + openFile.getWidth() + BORDER_SIZE, openFile.getY());
        
        mapWidth.setLocation(fileName.getX(), fileName.getY() + fileName.getHeight() + BORDER_SIZE + EditorWindow.BOLD_FONT.getSize());
        mapHeight.setLocation(mapWidth.getX() + mapWidth.getWidth() + BORDER_SIZE * 10, mapWidth.getY());
    }

    public void setSwingComponentsVisible(boolean visible, JComponent... ignore) {
        if (visible && fileName.getText().length() > 0) {
            tileSize.setVisible(true);
        }
        else {
            tileSize.setVisible(false);
        }
        
        JComponent[] components = { openFile, fileName, mapWidth, mapHeight };
        for (JComponent component : components) {
            if (Arrays.stream(ignore).noneMatch(o -> o == component)) {
                component.setVisible(visible);
            }
        }
    }

    @Override
    protected void paint(Graphics g) {
        paintBox(g);

        g.drawString("Tileset Image", fileName.getX(), fileName.getY() - BORDER_SIZE);

        if (tileSize.isVisible()) {
            g.drawString("Tile Size", tileSize.getX(), tileSize.getY() - BORDER_SIZE);
        }
        
        g.drawString("Map Width", mapWidth.getX(), mapWidth.getY() - 4);
        g.drawString("Map Height", mapHeight.getX(), mapWidth.getY() - 4);
    }
    
}