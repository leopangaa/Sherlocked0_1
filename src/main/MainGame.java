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
import view.screens.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainGame {

    private static MainGame instance;

    JFrame frame;
    CardLayout cardLayout;
    JPanel container;
    CluePuzzlePanel puzzlePanel;
    InventoryPanel inventoryPanel;
    GameHud hud;
    JLayeredPane root;
    String lastScreen = "MENU";
    boolean inventoryOpen = false;
    boolean hudEnabled = false;
    MusicPlayer musicPlayer;

    public MainGame() {
        instance = this;

        frame = new JFrame("Sherlocked");
        root = new JLayeredPane();
        root.setPreferredSize(new Dimension(UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        MainMenuPanel menu = new MainMenuPanel();
        LobbyPanel lobby = new LobbyPanel();
        Floor1Panel floor1 = new Floor1Panel();
        puzzlePanel = new CluePuzzlePanel();
        inventoryPanel = new InventoryPanel();
        hud = new GameHud();
        hud.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        musicPlayer = new MusicPlayer();

        container.add(menu, "MENU");
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

        showMenu();
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
            musicPlayer.playLoop(Assets.audio("menu.wav"));
            musicPlayer.setVolume(1f);
        }
    }

    public void startNewGame() {
        resetGameState();
        switchFloor("LOBBY");
    }

    public void continueGame() {
        JOptionPane.showMessageDialog(
                frame,
                "No saved game yet.",
                "Continue",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void openOptions() {
        JDialog dialog = new JDialog(frame, "Options", true);
        dialog.setSize(UiScale.w(420), UiScale.h(320));
        dialog.setLayout(null);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(new Color(24, 22, 20));

        JLabel title = new JLabel("Game Options");
        title.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        title.setForeground(new Color(240, 230, 210));
        title.setBounds(UiScale.x(120), UiScale.y(20), UiScale.w(200), UiScale.h(30));
        dialog.add(title);

        JLabel volumeLabel = new JLabel("Master Volume");
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setFont(new Font("Serif", Font.PLAIN, UiScale.font(16)));
        volumeLabel.setBounds(UiScale.x(40), UiScale.y(70), UiScale.w(120), UiScale.h(25));
        dialog.add(volumeLabel);

        JSlider volumeSlider = new JSlider(0, 100, 100);
        volumeSlider.setBounds(UiScale.x(170), UiScale.y(70), UiScale.w(180), UiScale.h(40));
        dialog.add(volumeSlider);

        JLabel textSpeedLabel = new JLabel("Text Speed");
        textSpeedLabel.setForeground(Color.WHITE);
        textSpeedLabel.setFont(new Font("Serif", Font.PLAIN, UiScale.font(16)));
        textSpeedLabel.setBounds(UiScale.x(40), UiScale.y(120), UiScale.w(120), UiScale.h(25));
        dialog.add(textSpeedLabel);

        String[] textSpeeds = {"Slow", "Normal", "Fast"};
        JComboBox<String> textSpeedBox = new JComboBox<>(textSpeeds);
        textSpeedBox.setSelectedIndex(1);
        textSpeedBox.setBounds(UiScale.x(170), UiScale.y(120), UiScale.w(120), UiScale.h(28));
        dialog.add(textSpeedBox);

        JCheckBox fullscreenBox = new JCheckBox("Fullscreen");
        fullscreenBox.setForeground(Color.WHITE);
        fullscreenBox.setBackground(new Color(24, 22, 20));
        fullscreenBox.setFont(new Font("Serif", Font.PLAIN, UiScale.font(15)));
        fullscreenBox.setBounds(UiScale.x(40), UiScale.y(170), UiScale.w(120), UiScale.h(25));
        dialog.add(fullscreenBox);

        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Serif", Font.BOLD, UiScale.font(15)));
        saveButton.setBounds(UiScale.x(90), UiScale.y(230), UiScale.w(100), UiScale.h(35));
        dialog.add(saveButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Serif", Font.BOLD, UiScale.font(15)));
        backButton.setBounds(UiScale.x(220), UiScale.y(230), UiScale.w(100), UiScale.h(35));
        dialog.add(backButton);

        saveButton.addActionListener(e -> {
            float volume = volumeSlider.getValue() / 100f;
            musicPlayer.setVolume(volume);

            JOptionPane.showMessageDialog(
                    dialog,
                    "Options saved.",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dialog.dispose();
        });

        backButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    public void quitGame() {
        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to quit?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
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