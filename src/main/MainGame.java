package main;

import controller.MusicPlayer;
import core.GameState;
import utils.Assets;
import utils.UiScale;
import view.components.GameDialogPanel;
import view.components.GameHud;
import view.screens.CluePuzzlePanel;
import view.screens.Floor1Panel;
import view.screens.IntroPanel;
import view.screens.InventoryPanel;
import view.screens.LobbyPanel;
import view.screens.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

public class MainGame {

    private static MainGame instance;

    JFrame frame;
    CardLayout cardLayout;
    JPanel container;
    CluePuzzlePanel puzzlePanel;
    IntroPanel introPanel;
    InventoryPanel inventoryPanel;
    GameHud hud;
    GameDialogPanel dialogPanel;
    JLayeredPane root;
    String lastScreen = "MENU";
    boolean inventoryOpen = false;
    boolean hudEnabled = false;
    MusicPlayer musicPlayer;

    public MainGame() {
        instance = this;

        frame = new JFrame("Sherlocked");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TRUE FULL SCREEN
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(frame);
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }

        root = new JLayeredPane();
        root.setPreferredSize(new Dimension(UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT));
        root.setBackground(Color.BLACK);
        root.setOpaque(true);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // We'll center the container in the root if the screen is larger
        int screenW = gd.getDisplayMode().getWidth();
        int screenH = gd.getDisplayMode().getHeight();
        int offsetX = (screenW - UiScale.GAME_WIDTH) / 2;
        int offsetY = (screenH - UiScale.GAME_HEIGHT) / 2;
        if (offsetX < 0)
            offsetX = 0;
        if (offsetY < 0)
            offsetY = 0;

        container.setBounds(offsetX, offsetY, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        MainMenuPanel menu = new MainMenuPanel();
        LobbyPanel lobby = new LobbyPanel();
        Floor1Panel floor1 = new Floor1Panel();
        puzzlePanel = new CluePuzzlePanel();
        introPanel = new IntroPanel();
        inventoryPanel = new InventoryPanel();
        hud = new GameHud();
        hud.setBounds(offsetX, offsetY, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        dialogPanel = new GameDialogPanel();
        dialogPanel.setBounds(0, 0, screenW, screenH);
        dialogPanel.setDialogOffset(offsetX, offsetY);

        musicPlayer = new MusicPlayer();

        container.add(menu, "MENU");
        container.add(lobby, "LOBBY");
        container.add(floor1, "FLOOR1");
        container.add(puzzlePanel, "PUZZLE");
        container.add(introPanel, "INTRO");
        container.add(inventoryPanel, "INVENTORY");

        root.add(container, JLayeredPane.DEFAULT_LAYER);
        root.add(hud, JLayeredPane.PALETTE_LAYER);
        root.add(dialogPanel, JLayeredPane.DRAG_LAYER);

        frame.setContentPane(root);

        // NO PACK/LOCATION IF FULLSCREEN
        if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH && !frame.isUndecorated()) {
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        }

        showMenu();
        installGlobalKeybinds();
    }

    private void installGlobalKeybinds() {
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "toggleInventory");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "openSettings");

        am.put("toggleInventory", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!inventoryOpen && !hudEnabled)
                    return;
                toggleInventory();
            }
        });

        am.put("openSettings", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!hudEnabled)
                    return; // Only in-game
                openSettings();
            }
        });
    }

    public void showMenu() {
        lastScreen = "MENU";
        inventoryOpen = false;

        cardLayout.show(container, "MENU");

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);

        if (musicPlayer != null) {
            musicPlayer.playLoop(Assets.audio("intro.wav"));
            musicPlayer.setVolume(1f);
        }
    }

    public void startNewGame() {
        resetGameState();
        lastScreen = "INTRO";
        inventoryOpen = false;
        setHudEnabled(false);
        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        cardLayout.show(container, "INTRO");
        introPanel.startIntro();
    }

    public void continueGame() {
        dialogPanel.showMessage("Continue", "No saved game yet.", null);
    }

    public void openSettings() {
        dialogPanel.setPanelSize(UiScale.s(400), UiScale.s(300));
        JPanel settingsMenu = new JPanel(null);
        settingsMenu.setOpaque(false);

        int btnW = UiScale.s(200);
        int btnH = UiScale.s(45);
        int startY = UiScale.s(20);
        int gap = UiScale.s(20);

        JButton optionsBtn = createMenuButton("Game Options", 0, startY, btnW, btnH);
        JButton saveExitBtn = createMenuButton("Save and Exit", 0, startY + (btnH + gap), btnW, btnH);
        JButton quitBtn = createMenuButton("Quit Game", 0, startY + 2 * (btnH + gap), btnW, btnH);

        settingsMenu.add(optionsBtn);
        settingsMenu.add(saveExitBtn);
        settingsMenu.add(quitBtn);

        // Center buttons in the custom panel
        int panelWidth = UiScale.s(500) - UiScale.s(120); // from GameDialogPanel content width
        optionsBtn.setLocation((panelWidth - btnW) / 2, optionsBtn.getY());
        saveExitBtn.setLocation((panelWidth - btnW) / 2, saveExitBtn.getY());
        quitBtn.setLocation((panelWidth - btnW) / 2, quitBtn.getY());

        optionsBtn.addActionListener(e -> openOptions());
        saveExitBtn.addActionListener(e -> {
            // Placeholder for save logic
            dialogPanel.showMessage("Save", "Game saved. Returning to menu...", () -> showMenu());
        });
        quitBtn.addActionListener(e -> quitGame());

        dialogPanel.showCustom("Settings", settingsMenu, Arrays.asList("Back"), choice -> {
        });
    }

    private JButton createMenuButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(200, 190, 170));
        btn.setForeground(new Color(30, 25, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void openOptions() {
        dialogPanel.setPanelSize(UiScale.s(550), UiScale.s(450));
        JPanel optionsPanel = new JPanel(null);
        optionsPanel.setOpaque(false);

        JLabel volumeLabel = new JLabel("Master Volume");
        volumeLabel.setForeground(new Color(60, 40, 20));
        volumeLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(16)));
        volumeLabel.setBounds(0, UiScale.s(10), UiScale.s(150), UiScale.s(25));
        optionsPanel.add(volumeLabel);

        JSlider volumeSlider = new JSlider(0, 100, (int) (musicPlayer.getVolume() * 100));
        volumeSlider.setOpaque(false);
        volumeSlider.setBounds(UiScale.s(160), UiScale.s(10), UiScale.s(200), UiScale.s(40));
        optionsPanel.add(volumeSlider);

        JLabel textSpeedLabel = new JLabel("Text Speed");
        textSpeedLabel.setForeground(new Color(60, 40, 20));
        textSpeedLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(16)));
        textSpeedLabel.setBounds(0, UiScale.s(60), UiScale.s(150), UiScale.s(25));
        optionsPanel.add(textSpeedLabel);

        String[] textSpeeds = { "Slow", "Normal", "Fast" };
        JComboBox<String> textSpeedBox = new JComboBox<>(textSpeeds);
        textSpeedBox.setSelectedIndex(1);
        textSpeedBox.setBounds(UiScale.s(160), UiScale.s(60), UiScale.s(120), UiScale.s(28));
        optionsPanel.add(textSpeedBox);

        JCheckBox fullscreenBox = new JCheckBox("Fullscreen", true);
        fullscreenBox.setOpaque(false);
        fullscreenBox.setForeground(new Color(60, 40, 20));
        fullscreenBox.setFont(new Font("Serif", Font.BOLD, UiScale.font(16)));
        fullscreenBox.setBounds(0, UiScale.s(110), UiScale.s(150), UiScale.s(25));
        optionsPanel.add(fullscreenBox);

        List<String> buttons = Arrays.asList("Save", "Back");
        dialogPanel.showCustom("Game Options", optionsPanel, buttons, choice -> {
            if (choice == 0) { // Save
                float volume = volumeSlider.getValue() / 100f;
                musicPlayer.setVolume(volume);
                dialogPanel.showMessage("Options", "Options saved.", null);
            }
        });
    }

    public void quitGame() {
        dialogPanel.showConfirm("Quit Game", "Are you sure you want to quit?", confirmed -> {
            if (confirmed) {
                System.exit(0);
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
        inventoryOpen = false;
        cardLayout.show(container, "PUZZLE");
    }

    public void openInventory() {
        inventoryPanel.open(lastScreen);
        cardLayout.show(container, "INVENTORY");
        inventoryOpen = true;

        if (hud != null) {
            hud.setObjectivesVisible(false);
        }
    }

    public void closeInventory(String returnTo) {
        inventoryOpen = false;

        if (hud != null && hudEnabled) {
            hud.setObjectivesVisible(true);
        }

        cardLayout.show(container, returnTo);
    }

    public void toggleInventory() {
        if (inventoryOpen) {
            closeInventory(lastScreen);
        } else {
            openInventory();
        }
    }

    public void switchFloor(String floorName) {
        lastScreen = floorName;
        inventoryOpen = false;
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

    private void resetGameState() {
        GameState state = GameState.getInstance();

        state.currentFloor = 0;
        state.clues.clear();
        state.floor1Complete = false;
        state.floor2Complete = false;

        // Add more flags here if your GameState has them, for example:
        // state.lobbyComplete = false;
    }

    public void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
        if (hud != null) {
            hud.setHudEnabled(enabled);
            hud.setVisible(enabled);
        }
    }

    public static MainGame getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGame::new);
    }
}