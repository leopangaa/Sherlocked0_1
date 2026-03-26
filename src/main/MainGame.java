package main;

import controller.MusicPlayer;
import core.GameState;
import utils.Assets;
import utils.UiScale;
import view.components.GameHud;
import view.screens.CluePuzzlePanel;
import view.screens.Floor1Panel;
import view.screens.InventoryPanel;
import view.screens.LobbyPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainGame {

    static MainGame instance;
    JFrame frame;
    CardLayout cardLayout;
    JPanel container;
    CluePuzzlePanel puzzlePanel;
    InventoryPanel inventoryPanel;
    GameHud hud;
    JLayeredPane root;
    String lastScreen = "LOBBY";
    boolean inventoryOpen = false;
    boolean hudEnabled = true;
    MusicPlayer musicPlayer;

    public MainGame() {
        instance = this;
        frame = new JFrame("Sherlocked");
        root = new JLayeredPane();
        root.setPreferredSize(new Dimension(UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel();
        container.setLayout(cardLayout);
        container.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        LobbyPanel lobby = new LobbyPanel();
        Floor1Panel floor1 = new Floor1Panel();
        puzzlePanel = new CluePuzzlePanel();
        inventoryPanel = new InventoryPanel();
        hud = new GameHud();
        hud.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);
        musicPlayer = new MusicPlayer();

        container.add(lobby, "LOBBY");
        container.add(floor1, "FLOOR1");
        container.add(puzzlePanel, "PUZZLE");
        container.add(inventoryPanel, "INVENTORY");

        root.add(container, JLayeredPane.DEFAULT_LAYER);
        root.add(hud, JLayeredPane.PALETTE_LAYER);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        GameState.getInstance().setCurrentFloor(0);
        musicPlayer.playLoop(Assets.audio("lobby.wav"));
        musicPlayer.setVolume(1f);

        installGlobalKeybinds();
    }

    private void installGlobalKeybinds() {
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "toggleInventory");

        am.put("toggleInventory", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!inventoryOpen && !hudEnabled) return;
                toggleInventory();
            }
        });
    }

    public void openPuzzle(String clue, String returnTo) {
        lastScreen = returnTo;
        puzzlePanel.startPuzzle(clue, returnTo);
        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }
        setHudEnabled(false);
        cardLayout.show(container, "PUZZLE");
    }

    public void openInventory() {
        inventoryPanel.open(lastScreen);
        cardLayout.show(container, "INVENTORY");
        inventoryOpen = true;
        hud.setObjectivesVisible(false);
    }

    public void closeInventory(String returnTo) {
        inventoryOpen = false;
        hud.setObjectivesVisible(true);
        cardLayout.show(container, returnTo);
    }

    public void toggleInventory() {
        if (inventoryOpen) {
            closeInventory(lastScreen);
        } else {
            openInventory();
        }
    }

    public static MainGame getInstance() {
        return instance;
    }

    public void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
        if (hud != null) hud.setHudEnabled(enabled);
    }

    public void switchFloor(String floorName) {
        lastScreen = floorName;
        cardLayout.show(container, floorName);
        if (hud != null) {
            hud.setVisible(true);
            hud.setObjectivesVisible(true);
        }
        setHudEnabled(true);
        if ("LOBBY".equals(floorName)) {
            GameState.getInstance().setCurrentFloor(0);
            musicPlayer.playLoop(Assets.audio("lobby.wav"));
        } else if ("FLOOR1".equals(floorName)) {
            GameState.getInstance().setCurrentFloor(1);
            musicPlayer.playLoop(Assets.audio("floor1.wav"));
        }
    }

    public static void main(String[] args) {
        new MainGame();
    }
}
