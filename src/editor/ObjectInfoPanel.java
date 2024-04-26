package editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class ObjectInfoPanel extends EditorPanel {

    private GameObject obj;
    private GameWorld gameWorld;
    
    private TextField nameField;
    private TextField objectX, objectY, objectW, objectH;
    
    private JTextField fileName;
    private JButton openFile;
    private JFileChooser fileChooser;

    private JComboBox<String> propertyList;
    private TextField keyEditor;
    private TextField valueEditor;
    private int propertyNum;


    public ObjectInfoPanel(GameWorld gameWorld, EditorWindow window) {
        this.gameWorld = gameWorld;
        initNamePanel(window);
        initBoundsPanels(window);
        initFileChooser(window);
        initPropertyList(window);
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    private void initPropertyList(EditorWindow window) {
        propertyList = new JComboBox<String>();
        propertyList.setBounds(0, 0, 200, EditorWindow.BOLD_FONT.getSize());
        propertyList.addActionListener(e -> {
            refreshKeyAndValue();
        });
        propertyList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    window.requestFocusInWindow();
                }
            }
        });
        window.add(propertyList);

        keyEditor = new TextField() {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + propertyList.getSelectedItem());
                window.requestFocusInWindow();
            }

            @Override
            protected void onEnter(KeyEvent e) {
                if (obj.properties.containsKey(getText())) {
                    return;
                }
                LinkedHashMap<String, Object> newProp = new LinkedHashMap<>();
                for (int i = 0; i < obj.properties.size(); i++) {
                    String key = obj.properties.keySet().toArray(new String[0])[i];
                    if (i == propertyList.getSelectedIndex()) {
                        newProp.put(getText(), obj.properties.get(key));
                    }
                    newProp.put(key, obj.properties.get(key));
                }
                newProp.remove(propertyList.getSelectedItem());
                obj.properties = newProp;

                int index = propertyList.getSelectedIndex();
                refreshPropertyList();
                propertyList.setSelectedIndex(index);

                window.requestFocusInWindow();
            }
        };
        keyEditor.setBounds(0, 0, 100, 20);
        window.add(keyEditor);

        valueEditor = new TextField() {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + obj.properties.get(getKey()));
                window.requestFocusInWindow();
            }

            @Override
            protected void onEnter(KeyEvent e) {
                obj.properties.remove(getKey());
                obj.properties.put(getKey(), getText());
                window.requestFocusInWindow();
            }

            private String getKey() {
                return "" + propertyList.getSelectedItem();
            }
        };
        valueEditor.setBounds(0, 0, 100, 20);
        window.add(valueEditor);
    }

    private void initNamePanel(EditorWindow window) {
        nameField = new TextField() {
            @Override
            protected void onEscape(KeyEvent e) {
                setText(obj.name);
                window.requestFocusInWindow();
            }
            @Override
            protected void onEnter(KeyEvent e) {
                obj.name = getText();
                window.requestFocusInWindow();
            }
        };
        nameField.setBounds(0, 0, 200, EditorWindow.BOLD_FONT.getSize());
        window.add(nameField);
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
            obj.imgPath = "" + fileChooser.getSelectedFile();
            fileName.setText(obj.imgPath);
            try {
                obj.sprite = ImageIO.read(fileChooser.getSelectedFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            fileChooser.setSelectedFile(null);

            final EditorPanel gamePanel = window.getPanel(EditorWindow.GAME_PANEL);
            obj.width = obj.sprite.getWidth();
            if (obj.width > gamePanel.width) {
                obj.width = gamePanel.width;
            }
            obj.height = obj.sprite.getHeight();
            if (obj.height > gamePanel.height) {
                obj.height = gamePanel.height;
            }
            objectW.setText(obj.width + "");
            objectH.setText(obj.height + "");
        });
        window.add(openFile);
    }

    private void initBoundsPanels(EditorWindow window) {
        final EditorPanel gamePanel = window.getPanel(EditorWindow.GAME_PANEL);
        
        objectX = new NumberTextField(window, Integer.MAX_VALUE) {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + obj.x);
                window.requestFocusInWindow();
            }
            @Override
            protected void onEnter(KeyEvent e) {
                obj.x = Integer.parseInt(getText());
                window.requestFocusInWindow();
            }
        };
        objectY = new NumberTextField(window, Integer.MAX_VALUE) {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + obj.y);
                window.requestFocusInWindow();
            }
            @Override
            protected void onEnter(KeyEvent e) {
                obj.y = Integer.parseInt(getText());
                window.requestFocusInWindow();
            }
        };
        objectW = new NumberTextField(window, gamePanel.getWidth()) {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + obj.width);
                window.requestFocusInWindow();
            }
            @Override
            protected void onEnter(KeyEvent e) {
                obj.width = Integer.parseInt(getText());
                obj.pushInBounds(gamePanel.getWidth(), gamePanel.getHeight());
                window.requestFocusInWindow();
            }
        };
        objectH = new NumberTextField(window, gamePanel.getHeight()) {
            @Override
            protected void onEscape(KeyEvent e) {
                setText("" + obj.height);
                window.requestFocusInWindow();
            }
            @Override
            protected void onEnter(KeyEvent e) {
                obj.height = Integer.parseInt(getText());
                obj.pushInBounds(gamePanel.getWidth(), gamePanel.getHeight());
                window.requestFocusInWindow();
            }
        };
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        setSwingComponentLocations();
    }

    private void setSwingComponentLocations() {
        final int fontSize = EditorWindow.BOLD_FONT.getSize();
        nameField.setLocation(x + BORDER_SIZE * 2, y + BORDER_SIZE * 2 + fontSize);
        objectX.setLocation(x + BORDER_SIZE * 2 , nameField.getY() + BORDER_SIZE * 2 + fontSize * 2);
        objectY.setLocation(x + BORDER_SIZE * 2 + 54, objectX.getY());
        objectW.setLocation(x + BORDER_SIZE * 2 + 2 * 54, objectX.getY());
        objectH.setLocation(x + BORDER_SIZE * 2 + 3 * 54, objectX.getY());
        
        fileName.setLocation(objectX.getX(), objectX.getY() + objectX.getHeight() + BORDER_SIZE + fontSize);
        openFile.setLocation(fileName.getX() + fileName.getWidth(), fileName.getY());
        
        propertyList.setLocation(fileName.getX(), fileName.getY() + (BORDER_SIZE + fontSize) * 2);
        keyEditor.setLocation(propertyList.getX(), propertyList.getY() + BORDER_SIZE + fontSize);
        valueEditor.setLocation(propertyList.getX() + keyEditor.getWidth() + BORDER_SIZE, keyEditor.getY());
    }

    @Override
    public void paint(Graphics g) {
        paintBox(g);
        setCurrentObject();
        
        if (obj == null) {
            return;
        }
        g.drawString("Name", nameField.getX(), nameField.getY() - BORDER_SIZE);
        g.drawString("X", objectX.getX(), objectX.getY() - BORDER_SIZE);
        g.drawString("Y", objectY.getX(), objectX.getY() - BORDER_SIZE);
        g.drawString("W", objectW.getX(), objectX.getY() - BORDER_SIZE);
        g.drawString("H", objectH.getX(), objectX.getY() - BORDER_SIZE);
        g.drawString("Sprite", fileName.getX(), fileName.getY() - BORDER_SIZE);
        g.drawString("Properties", propertyList.getX(), propertyList.getY() - BORDER_SIZE);

        ((Graphics2D) g).setStroke(new BasicStroke(3));
        
        g.setColor(Color.GREEN);
        g.drawLine(plusBounds().x + CROSS_SIZE / 2, plusBounds().y, plusBounds().x + CROSS_SIZE / 2, plusBounds().y + CROSS_SIZE);
        g.drawLine(plusBounds().x + CROSS_SIZE, plusBounds().y + CROSS_SIZE / 2, plusBounds().x, plusBounds().y + CROSS_SIZE / 2);

        g.setColor(Color.RED);
        g.drawLine(crossBounds().x, crossBounds().y, crossBounds().x + CROSS_SIZE, crossBounds().y + CROSS_SIZE);
        g.drawLine(crossBounds().x + CROSS_SIZE, crossBounds().y, crossBounds().x, crossBounds().y + CROSS_SIZE);
    }

    private Rectangle plusBounds() {
        return new Rectangle(propertyList.getX() + propertyList.getWidth() + 10, propertyList.getY(), CROSS_SIZE, CROSS_SIZE);
    }

    private Rectangle crossBounds() {
        return new Rectangle(plusBounds().x + CROSS_SIZE * 2, plusBounds().y, CROSS_SIZE, CROSS_SIZE);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (plusBounds().contains(e.getPoint())) {
            obj.properties.put(propertyNum == 0 ? "New Property" : "New Property (" + propertyNum + ")", "");
            propertyNum++;
            refreshPropertyList();
            propertyList.setSelectedIndex(obj.properties.size() - 1);
            refreshKeyAndValue();
        }
        else if (crossBounds().contains(e.getPoint())) {
            obj.properties.remove(propertyList.getSelectedItem());
            refreshPropertyList();
            refreshKeyAndValue();
        }
    }
    
    private void setCurrentObject() {
        GameObject oldObj = obj;
        for (GameObject obj : gameWorld) {
            if (obj.highlightLocked()) {
                this.obj = obj;
                if (obj != oldObj) {
                    nameField.setText(obj.name);
                    objectW.setText("" + obj.width);
                    objectH.setText("" + obj.height);
                    fileName.setText(obj.imgPath);
                    refreshPropertyList();
                    refreshKeyAndValue();
                }
                if (!String.valueOf(obj.x).equals(objectX.getText())) {
                    objectX.setText("" + obj.x);
                }
                if (!String.valueOf(obj.y).equals(objectY.getText())) {
                    objectY.setText("" + obj.y);
                }
                setSwingComponentsVisible(true);
                return;
            }
        }
        obj = null;
        setSwingComponentsVisible(false);
    }

    private void refreshKeyAndValue() {
        boolean propertiesExist = obj.properties.size() != 0;
        valueEditor.setEditable(propertiesExist);
        keyEditor.setEditable(propertiesExist);
        if (!propertiesExist) {
            valueEditor.setText("");
            keyEditor.setText("");
            return;
        }
        valueEditor.setText("" + obj.properties.get(propertyList.getSelectedItem()));
        keyEditor.setText("" + propertyList.getSelectedItem());
    }

    private void refreshPropertyList() {
        propertyList.removeAllItems();
        for (String property : obj.properties.keySet()) {
            propertyList.addItem(property);
        }
    }

    public void setSwingComponentsVisible(boolean visible, JComponent... ignore) {
        JComponent[] components = { nameField, objectX, objectY, objectW, objectH, openFile, fileName, propertyList, valueEditor, keyEditor };
        for (JComponent component : components) {
            if (Arrays.stream(ignore).noneMatch(o -> o == component)) {
                component.setVisible(visible);
            }
        }
    }
}