package main;

import controller.MusicPlayer;
import core.GameState;
import utils.Assets;
import utils.UiScale;
import utils.TTSManager;
import view.components.GameDialogPanel;
import view.components.GameHud;
import view.screens.CluePuzzlePanel;
import view.screens.Floor1Panel;
import view.screens.Floor2Panel;
import view.screens.IntroPanel;
import view.screens.OutroPanel;
import view.screens.SplashScreenPanel;
import view.screens.InventoryPanel;
import view.screens.LobbyPanel;
import view.screens.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import view.screens.WindowPuzzleQuest;
import view.screens.MsHarperPuzzlePanel;
import view.screens.CctvAnomalyPuzzlePanel;
import view.screens.RinaQuestPanel;
import view.screens.CleaningCartPanel;
import view.screens.DeskPuzzlePanel;
import view.screens.LockerQuestPanel;
import view.screens.CabinetPuzzlePanel;
import view.screens.TerminalPuzzleQuest;
import view.screens.JaredPuzzleQuest;

public class MainGame {

    private static MainGame instance;

    JFrame frame;
    CardLayout cardLayout;
    JPanel container;
    CluePuzzlePanel puzzlePanel;
    WindowPuzzleQuest windowPuzzlePanel;
    MsHarperPuzzlePanel harperPuzzlePanel;
    CctvAnomalyPuzzlePanel cctvPuzzlePanel;
    RinaQuestPanel rinaQuestPanel;
    CleaningCartPanel cleaningCartPanel;
    DeskPuzzlePanel deskPuzzlePanel;
    LockerQuestPanel lockerQuestPanel;
    CabinetPuzzlePanel cabinetPuzzlePanel;
    TerminalPuzzleQuest terminalPuzzleQuest;
    JaredPuzzleQuest jaredPuzzleQuest;
    IntroPanel introPanel;
    OutroPanel outroPanel;
    SplashScreenPanel splashPanel;
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

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        frame.setBounds(bounds);

        int screenW = bounds.width;
        int screenH = bounds.height;

        root = new JLayeredPane();
        root.setPreferredSize(new Dimension(screenW, screenH));
        root.setBackground(Color.BLACK);
        root.setOpaque(true);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setOpaque(false);

        int offsetX = (screenW - UiScale.GAME_WIDTH) / 2;
        int offsetY = (screenH - UiScale.GAME_HEIGHT) / 2;
        if (offsetX < 0) offsetX = 0;
        if (offsetY < 0) offsetY = 0;

        container.setBounds(offsetX, offsetY, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        MainMenuPanel menu = new MainMenuPanel();
        LobbyPanel lobby = new LobbyPanel();
        Floor1Panel floor1 = new Floor1Panel();
        Floor2Panel floor2 = new Floor2Panel();
        puzzlePanel = new CluePuzzlePanel();
        windowPuzzlePanel = new WindowPuzzleQuest();
        harperPuzzlePanel = new MsHarperPuzzlePanel();
        cctvPuzzlePanel = new CctvAnomalyPuzzlePanel();
        rinaQuestPanel = new RinaQuestPanel();
        cleaningCartPanel = new CleaningCartPanel();
        deskPuzzlePanel = new DeskPuzzlePanel();
        lockerQuestPanel = new LockerQuestPanel();
        cabinetPuzzlePanel = new CabinetPuzzlePanel();
        terminalPuzzleQuest = new TerminalPuzzleQuest();
        jaredPuzzleQuest = new JaredPuzzleQuest();
        introPanel = new IntroPanel();
        outroPanel = new OutroPanel();
        splashPanel = new SplashScreenPanel();
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
        container.add(floor2, "FLOOR2");
        container.add(puzzlePanel, "PUZZLE");
        container.add(windowPuzzlePanel, "WINDOW_PUZZLE");
        container.add(harperPuzzlePanel, "HARPER_PUZZLE");
        container.add(cctvPuzzlePanel, "CCTV_PUZZLE");
        container.add(rinaQuestPanel, "RINA_QUEST");
        container.add(cleaningCartPanel, "CLEANING_CART_QUEST");
        container.add(deskPuzzlePanel, "DESK_PUZZLE");
        container.add(lockerQuestPanel, "LOCKER_QUEST");
        container.add(cabinetPuzzlePanel, "CABINET_PUZZLE");
        container.add(terminalPuzzleQuest, "TERMINAL_PUZZLE");
        container.add(jaredPuzzleQuest, "JARED_QUEST");
        container.add(introPanel, "INTRO");
        container.add(outroPanel, "OUTRO");
        container.add(splashPanel, "SPLASH");
        container.add(inventoryPanel, "INVENTORY");

        root.add(container, JLayeredPane.DEFAULT_LAYER);
        root.add(hud, JLayeredPane.PALETTE_LAYER);
        root.add(dialogPanel, JLayeredPane.DRAG_LAYER);

        frame.setContentPane(root);
        frame.setVisible(true);

        startSplash();
        installGlobalKeybinds();
    }

    private void startSplash() {
        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }
        cardLayout.show(container, "SPLASH");
        musicPlayer.playLoop(Assets.audio("intro.wav"));
        musicPlayer.setVolume(1f);
        splashPanel.start();
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
                    return;
                openSettings();
            }
        });
    }

    public void showMenu() {
        TTSManager.stop();
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

    public void triggerChapterEnd() {
        TTSManager.stop();
        musicPlayer.playLoop(Assets.audio("intro.wav")); // Reuse eerie track
        cardLayout.show(container, "OUTRO");
        outroPanel.startOutro();
        setHudEnabled(false);
    }

    public void startNewGame() {
        TTSManager.stop();
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
        dialogPanel.setPanelSize(UiScale.s(500), UiScale.s(400));
        JPanel settingsMenu = new JPanel(null);
        settingsMenu.setOpaque(false);

        int largeBtnW = UiScale.s(250);
        int smallBtnW = UiScale.s(150);
        int btnH = UiScale.s(30);
        int startY = UiScale.s(40);
        int gap = UiScale.s(25);

        JButton optionsBtn = createMenuButton("Game Options", 0, startY, largeBtnW, btnH);
        JButton saveExitBtn = createMenuButton("Save and Exit", 0, startY + (btnH + gap), largeBtnW, btnH);
        JButton backBtn = createMenuButton("Back", 0, startY + 2 * (btnH + gap) + UiScale.s(10), smallBtnW, btnH);

        settingsMenu.add(optionsBtn);
        settingsMenu.add(saveExitBtn);
        settingsMenu.add(backBtn);

        
        int panelWidth = UiScale.s(500) - UiScale.s(120); 
        optionsBtn.setLocation((panelWidth - largeBtnW) / 2, optionsBtn.getY());
        saveExitBtn.setLocation((panelWidth - largeBtnW) / 2, saveExitBtn.getY());
        backBtn.setLocation((panelWidth - smallBtnW) / 2, backBtn.getY());

        optionsBtn.addActionListener(e -> openOptions());
        saveExitBtn.addActionListener(e -> {

            dialogPanel.showMessage("Save", "Game saved. Returning to menu...", () -> showMenu());
        });
        backBtn.addActionListener(e -> dialogPanel.setVisible(false));

        dialogPanel.showCustom("Settings", settingsMenu, Arrays.asList(), choice -> {});
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

    public void openPuzzle(String clue, String returnTo, String difficulty) {
        lastScreen = returnTo;
        puzzlePanel.startPuzzle(clue, returnTo, difficulty);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "PUZZLE");
    }

    public void openPuzzle(String clue, String returnTo) {
        openPuzzle(clue, returnTo, "MEDIUM");
    }

    public void openWindowPuzzle(String clue, String returnTo) {
        lastScreen = returnTo;
        windowPuzzlePanel.startPuzzle(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "WINDOW_PUZZLE");
    }

    public void openHarperPuzzle(String clue, String returnTo) {
        lastScreen = returnTo;
        harperPuzzlePanel.startPuzzle(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "HARPER_PUZZLE");
    }

    public void openCctvPuzzle(String clue, String returnTo) {
        lastScreen = returnTo;
        cctvPuzzlePanel.startPuzzle(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "CCTV_PUZZLE");
    }

    public void openRinaQuest(String clue, String returnTo) {
        lastScreen = returnTo;
        rinaQuestPanel.startQuest(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "RINA_QUEST");
    }

    public void openCleaningCartQuest(String clue, String returnTo) {
        lastScreen = returnTo;
        cleaningCartPanel.startQuest(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "CLEANING_CART_QUEST");
    }

    public void openDeskPuzzle(String clue, String returnTo) {
        lastScreen = returnTo;
        deskPuzzlePanel.startPuzzle(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "DESK_PUZZLE");
    }

    public void openLockerQuest(String clue, String returnTo) {
        lastScreen = returnTo;
        lockerQuestPanel.startQuest(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "LOCKER_QUEST");
    }

    public void openCabinetPuzzle(String clue, String returnTo) {
        lastScreen = returnTo;
        cabinetPuzzlePanel.startPuzzle(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "CABINET_PUZZLE");
    }

    public void openTerminalPuzzle(String clue, String returnTo) {
        lastScreen = returnTo;
        terminalPuzzleQuest.startQuest(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "TERMINAL_PUZZLE");
    }

    public void openJaredQuest(String clue, String returnTo) {
        lastScreen = returnTo;
        jaredPuzzleQuest.startQuest(clue, returnTo);

        if (hud != null) {
            hud.setVisible(false);
            hud.setObjectivesVisible(false);
        }

        setHudEnabled(false);
        inventoryOpen = false;
        cardLayout.show(container, "JARED_QUEST");
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
        TTSManager.stop();
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
            musicPlayer.playLoop(Assets.audio("lobby.wav"));
        } else if ("FLOOR2".equals(floorName)) {
            GameState.getInstance().setCurrentFloor(2);
            musicPlayer.playLoop(Assets.audio("lobby.wav"));
        }
    }

    private void resetGameState() {
        GameState state = GameState.getInstance();

        state.currentFloor = 0;
        state.clues.clear();
        state.floor1Complete = false;
        state.floor2Complete = false;

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

    public GameDialogPanel getDialogPanel() {
        return dialogPanel;
    }

    public JLayeredPane getRoot() {
        return root;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGame::new);
    }
}