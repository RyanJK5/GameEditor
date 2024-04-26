package editor;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

public class EditorWindow extends JPanel {

    private JFrame frame;
    private GameWorld gameWorld;
    private List<EditorPanel> panels;
    private final Timer globalTimer;
    
    private boolean defaultMode;

    private JFileChooser saveChooser;
    private JButton saveButton;
    private JButton loadButton;
    private File currentFile;

    public static final Font FONT = new Font(null, Font.PLAIN, 20);
    public static final Font BOLD_FONT = new Font(null, Font.BOLD, 22);

    public static final int
        OBJECT_LIST_PANEL = 0,
        GAME_PANEL = 1,
        INFO_PANEL = 2,
        TILE_PANEL = 3,
        TILE_INFO_PANEL = 4
    ;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            EditorWindow window = new EditorWindow();
            window.start();
        });
    }

    public EditorWindow() {
        defaultMode = true;
        gameWorld = new GameWorld();
        panels = new ArrayList<>();
        
        saveChooser = new JFileChooser("dat");
        saveChooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".lvl");
            }

            @Override
            public String getDescription() {
                return ".lvl";
            }
            
        });

        initSwingComponents();
        initObjList();
        initGamePanel();
        initObjInfo();
        initTilePanel();
        initTileInfoPanel();
        initSaveButton();
        initLoadButton();
        
        globalTimer = new Timer(12, e -> repaint());
    }

    private void initLoadButton() {
        GamePanel gamePanel = (GamePanel) getPanel(GAME_PANEL);
        
        loadButton = new JButton("Load");
        loadButton.setBounds(saveButton.getX(), saveButton.getY() + saveButton.getHeight(), 100, 40);
        loadButton.setFocusable(false);
        loadButton.addActionListener(e -> {
            try {
                saveChooser.showOpenDialog(EditorWindow.this);
                if (saveChooser.getSelectedFile() == null || saveChooser.getSelectedFile() == currentFile) {
                    return;
                }
                currentFile = saveChooser.getSelectedFile();

                TiledWorld tiledWorld = new SaveHandler().readTiledWorld("" + currentFile);
                gamePanel.setTiledWorld(tiledWorld);
                TiledInfoPanel tiledInfoPanel = (TiledInfoPanel) getPanel(TILE_INFO_PANEL);
                tiledInfoPanel.setTiledWorld(tiledWorld);
                TilePanel tilePanel = (TilePanel) getPanel(TILE_PANEL);
                tilePanel.setTileset(tiledWorld.tileset);

                gameWorld = new SaveHandler().readGameWorld("" + currentFile);
                gamePanel.setGameWorld(gameWorld);
                ObjectListPanel listPanel = (ObjectListPanel) getPanel(OBJECT_LIST_PANEL);
                listPanel.setGameWorld(gameWorld);
                ObjectInfoPanel infoPanel = (ObjectInfoPanel) getPanel(INFO_PANEL);
                infoPanel.setGameWorld(gameWorld);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        add(loadButton);
    }

    private void initSaveButton() {
        GamePanel gamePanel = (GamePanel) getPanel(GAME_PANEL);
        
        saveButton = new JButton("Save") {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setEnabled(gamePanel.getTiledWorld().tileset != null && gamePanel.getTiledWorld().getWidth() != 0 && gamePanel.getTiledWorld().getHeight() != 0);
            }
        };
        saveButton.setBounds(frame.getWidth() - 100 - EditorPanel.BORDER_SIZE - 19, EditorPanel.BORDER_SIZE, 100, 40);
        saveButton.setFocusable(false);
        saveButton.addActionListener(e -> {
            saveChooser.showSaveDialog(EditorWindow.this);
            if (saveChooser.getSelectedFile() == null) {
                return;
            }
            try {
                new SaveHandler().writeCurrentLevel(gameWorld, gamePanel.getTiledWorld(), "" + saveChooser.getSelectedFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        add(saveButton);
    }

    private void initSwingComponents() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, 1600, 900);
        frame.setVisible(true);
        frame.setTitle("Game Editor");
        frame.setContentPane(this);
        setLayout(null);
    }

    private void initObjList() {
        final EditorPanel objectList = new ObjectListPanel(this, gameWorld);
        objectList.setBounds(0, 0, 320, 600);
        panels.add(objectList);
        objectList.addListenersTo(this);
    }

    private void initObjInfo() {
        final EditorPanel infoPanel = new ObjectInfoPanel(gameWorld, this);
        final EditorPanel objectList = getPanel(OBJECT_LIST_PANEL);
        infoPanel.setBounds(0, objectList.height - EditorPanel.BORDER_SIZE, objectList.getWidth(), frame.getHeight() - objectList.height - 39 + EditorPanel.BORDER_SIZE);
        panels.add(infoPanel);
        infoPanel.addListenersTo(this);
    }

    private void initGamePanel() {
        final EditorPanel gamePanel = new GamePanel(this, gameWorld, new TiledWorld(0, 0));
        final EditorPanel objectList = getPanel(OBJECT_LIST_PANEL);
        gamePanel.setBounds(objectList.width, 0, frame.getWidth() - objectList.width - 16, frame.getHeight() - 39);
        panels.add(gamePanel);
        gamePanel.addListenersTo(this);
    }

    private void initTilePanel() {
        final EditorPanel tilePanel = new TilePanel(this);
        tilePanel.setBounds(getPanel(OBJECT_LIST_PANEL).getBounds());
        panels.add(tilePanel);
    }

    private void initTileInfoPanel() {
        final TiledInfoPanel tileInfoPanel = new TiledInfoPanel(this);
        tileInfoPanel.setBounds(getPanel(INFO_PANEL).getBounds());
        panels.add(tileInfoPanel);
    }

    public boolean objectDisplayMode() {
        return defaultMode;
    }

    public void toggleDisplayMode() {
        defaultMode = !defaultMode;
        if (defaultMode) {
            getPanel(OBJECT_LIST_PANEL).addListenersTo(this);
            getPanel(TILE_PANEL).removeListenersFrom(this);
            ((TiledInfoPanel) getPanel(TILE_INFO_PANEL)).setSwingComponentsVisible(false);
        }
        else {
            for (GameObject obj : gameWorld) {
                obj.lockHighlight(false);
                obj.highlight(false);
            }
            ((ObjectInfoPanel) getPanel(INFO_PANEL)).setSwingComponentsVisible(false);
            ((TiledInfoPanel) getPanel(TILE_INFO_PANEL)).setSwingComponentsVisible(true);
            getPanel(OBJECT_LIST_PANEL).removeListenersFrom(this);
            getPanel(TILE_PANEL).addListenersTo(this);
        }
    }

    public void start() {
        globalTimer.start();
    }

    public EditorPanel getPanel(int id) {
        return panels.get(id);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (EditorPanel panel : panels) {
            if (defaultMode && (panel == getPanel(TILE_PANEL) || panel == getPanel(TILE_INFO_PANEL))) {
                continue;
            }
            else if (!defaultMode && (panel == getPanel(OBJECT_LIST_PANEL) || panel == getPanel(INFO_PANEL))) {
                continue;
            }
            panel.repaint(g);
        }
    }

    @Override
    public void addKeyListener(KeyListener l) {
        frame.addKeyListener(l);
    }

}