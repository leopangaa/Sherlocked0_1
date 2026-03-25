package view.screens;

import core.GameState;
import main.MainGame;
import utils.Assets;
import utils.UiScale;
import view.components.BackgroundPanel;
import view.components.DialogueUI;

import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    DialogueUI dialogueUI;
    JPanel inputBlocker;

    public LobbyPanel() {

        setLayout(null);

        // Dialogue UI (Reusable and floating)
        dialogueUI = new DialogueUI();

        areaLayout = new CardLayout();
        areaContainer = new JPanel(areaLayout);

        inputBlocker = new JPanel();
        inputBlocker.setLayout(null);
        inputBlocker.setOpaque(false);
        inputBlocker.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);
        inputBlocker.setVisible(false);
        inputBlocker.addMouseListener(new java.awt.event.MouseAdapter() {});

        dialogueUI.setVisibilityListener(visible -> {
            inputBlocker.setVisible(visible);
            setButtonsEnabled(areaContainer, !visible);
            MainGame.getInstance().setHudEnabled(!visible);
            areaContainer.revalidate();
            areaContainer.repaint();
        });

        JPanel lobbyPartA = createLobbyPartA();
        JPanel lobbyPartB = createLobbyPartB();
        JPanel lobbyPartC = createLobbyPartC();
        JPanel lobbyPartD = createLobbyPartD();

        areaContainer.add(lobbyPartA, "A");
        areaContainer.add(lobbyPartB, "B");
        areaContainer.add(lobbyPartC, "C");
        areaContainer.add(lobbyPartD, "D");
        areaContainer.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        // Add components: dialogueUI is on top (index 0)
        add(dialogueUI, 0);
        add(inputBlocker, 1);
        add(areaContainer, 2);
    }

    private void setButtonsEnabled(Container root, boolean enabled) {
        for (Component c : root.getComponents()) {
            if (c instanceof AbstractButton) {
                c.setEnabled(enabled);
            }
            if (c instanceof Container) {
                setButtonsEnabled((Container) c, enabled);
            }
        }
    }

    private void startDialogue(String[] dialogue) {
        dialogueUI.startDialogue(dialogue);
    }

    private void startDialogue(String[] dialogue, Runnable onComplete) {
        dialogueUI.startDialogue(dialogue, onComplete);
    }

    private void showInteractionMenu(String title, Runnable talkAction, Runnable examineAction) {
        dialogueUI.showInteractionMenu(title, talkAction, examineAction);
    }

    private void typeText(String text) {
        dialogueUI.typeText(text);
    }

    private JLabel createScaledIconLabel(String imagePath, int baseX, int baseY, int baseW, int baseH) {
        ImageIcon icon = resizeIcon(imagePath, UiScale.s(baseW), UiScale.s(baseH));
        JLabel label = new JLabel(icon);
        label.setBounds(UiScale.x(baseX), UiScale.y(baseY), icon.getIconWidth(), icon.getIconHeight());
        return label;
    }

    private JButton createArrowButton(String arrowImagePath, int baseX, int baseY, int baseSize) {
        ImageIcon normalIcon = resizeIcon(arrowImagePath, UiScale.s(baseSize), UiScale.s(baseSize));
        ImageIcon hoverIcon = resizeIcon(arrowImagePath, UiScale.s(baseSize + 8), UiScale.s(baseSize + 8));
        JButton btn = new JButton(normalIcon);
        btn.setBounds(UiScale.x(baseX), UiScale.y(baseY), normalIcon.getIconWidth(), normalIcon.getIconHeight());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("normalIcon", normalIcon);
        btn.putClientProperty("hoverIcon", hoverIcon);
        btn.putClientProperty("origBounds", btn.getBounds());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.isEnabled()) return;
                Rectangle orig = (Rectangle) btn.getClientProperty("origBounds");
                ImageIcon hi = (ImageIcon) btn.getClientProperty("hoverIcon");
                btn.setIcon(hi);
                int dx = (hi.getIconWidth() - orig.width) / 2;
                int dy = (hi.getIconHeight() - orig.height) / 2;
                btn.setBounds(orig.x - dx, orig.y - dy, hi.getIconWidth(), hi.getIconHeight());
                btn.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                Rectangle orig = (Rectangle) btn.getClientProperty("origBounds");
                ImageIcon ni = (ImageIcon) btn.getClientProperty("normalIcon");
                btn.setIcon(ni);
                btn.setBounds(orig);
                btn.repaint();
            }
        });
        return btn;
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon original = new ImageIcon(path);
        Image img = original.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private JPanel createLobbyPartA() {

        BackgroundPanel panel = new BackgroundPanel(Assets.img("lobbyA.jpg"));

        // Mrs. Vale (Front Desk Clerk)
        JLabel mrsVale = createScaledIconLabel(Assets.img("receptionist.png"), 380, 160, 100, 130);
        panel.add(mrsVale);

        mrsVale.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showInteractionMenu("MRS. VALE", 
                    () -> { // Talk Action
                        if (!GameState.getInstance().hasClue("Dr. Kells photo")) {
                            String[] dialogue = {
                                "Mrs. Vale: Welcome to the hotel, detective. It's a tragedy about Dr. Kells.",
                                "Mrs. Vale: He was such a regular guest here. Always in Room 217.",
                                "Mrs. Vale: I found some photos of him, but they are all mixed up.",
                                "Mrs. Vale: If you can help me sort these files, I'll let you keep the photos."
                            };
                            startDialogue(dialogue, () -> MainGame.getInstance().openPuzzle("Dr. Kells photo", "LOBBY"));
                        } else {
                            startDialogue(new String[]{"Mrs. Vale: I hope those photos help with your investigation, detective."});
                        }
                    },
                    () -> { // Examine Action
                        startDialogue(new String[]{"Mrs. Vale looks exhausted. Her eyes are bloodshot, as if she hasn't slept in days."});
                    }
                );
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mrsVale.setCursor(new Cursor(Cursor.HAND_CURSOR));
                mrsVale.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                mrsVale.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                mrsVale.setBorder(null);
            }
        });

        JButton rightArrow = createArrowButton(Assets.img("rightArrow.png"), 820, 250, 50);
        panel.add(rightArrow);

        JButton downArrow = createArrowButton(Assets.img("downArrow.png"), 425, 520, 50);
        panel.add(downArrow);

        JButton upArrow = createArrowButton(Assets.img("upArrow.png"), 425, 20, 50);
        panel.add(upArrow);

        // Mirror Interaction
        JLabel mirror = new JLabel();
        mirror.setBounds(UiScale.x(100), UiScale.y(100), UiScale.s(100), UiScale.s(200));
        mirror.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        panel.add(mirror);
        mirror.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showInteractionMenu("MIRROR", null, () -> {
                    String[] dialogue = {
                        "You look into the mirror...",
                        "Your reflection seems... delayed.",
                        "Wait, is that someone standing behind you?",
                        "You turn around, but no one is there.",
                        "Looking back at the mirror, a faint message is scrawled on the glass: 'YOU ARE NOT ALONE'"
                    };
                    startDialogue(dialogue);
                    GameState.getInstance().addClue("Mirror reflection hint");
                });
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mirror.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        rightArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "B");
        });

        downArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "C");
        });

        upArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "D");
        });

        return panel;
    }

    private JPanel createLobbyPartC() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("lobbyC.jpg"));

        // Broken Clock (invisible hotspot)
        JButton clock = new JButton();
        clock.setBounds(UiScale.x(650), UiScale.y(25), UiScale.s(90), UiScale.s(90));
        clock.setBorderPainted(false);
        clock.setContentAreaFilled(false);
        clock.setFocusPainted(false);
        clock.setOpaque(false);
        panel.add(clock);

        clock.addActionListener(e -> showInteractionMenu("BROKEN CLOCK", null, () -> {
            String[] dialogue = {
                "The clock is frozen at 11:45.",
                "Wait, the second hand is moving backwards.",
                "Tick... Tock... Tick... Tock...",
                "A chilling whisper fills your ears: 'TIME IS RUNNING OUT, DETECTIVE.'"
            };
            startDialogue(dialogue);
            GameState.getInstance().addClue("Frozen Clock hint");
        }));

        clock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clock.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clock.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        panel.setComponentZOrder(clock, 0);

        // Forgotten Suitcase
        JLabel suitcase = createScaledIconLabel(Assets.img("suitcase.png"), 208, 358, 145, 75);
        panel.add(suitcase);
        suitcase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showInteractionMenu("SUITCASE", null, () -> {
                    String[] dialogue = {
                        "An old, worn-out suitcase left on a chair.",
                        "The tag says 'R. Blackwood'.",
                        "It's locked, but you see a strange symbol carved into the leather.",
                        "It's the same symbol you saw in the mirror..."
                    };
                    startDialogue(dialogue);
                    GameState.getInstance().addClue("Blackwood Suitcase hint");
                });
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                suitcase.setCursor(new Cursor(Cursor.HAND_CURSOR));
                suitcase.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                suitcase.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                suitcase.setBorder(null);
            }
        });

        JButton upArrow = createArrowButton(Assets.img("upArrow.png"), 425, 20, 50);
        panel.add(upArrow);
        upArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "A");
        });

        return panel;
    }

    private JPanel createLobbyPartD() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("partd-background.jpg"));

        // Guest Register
        ImageIcon registerIcon = resizeIcon(Assets.img("guest-register.png"), UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);
        JLabel register = new JLabel(registerIcon);
        register.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);
        panel.add(register);
        register.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showInteractionMenu("GUEST REGISTER", null, () -> {
                    String[] dialogue = {
                        "The guest register is open to today's date.",
                        "You see Dr. Kells' name, but it's crossed out in red ink.",
                        "Next to it, someone has written a single word: 'LIAR'.",
                        "There's another entry from someone named 'E. Vane' in Room 305."
                    };
                    startDialogue(dialogue);
                    GameState.getInstance().addClue("Guest Register entry");
                });
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                register.setCursor(new Cursor(Cursor.HAND_CURSOR));
                register.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                register.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                register.setBorder(null);
            }
        });

        JButton downArrow = createArrowButton(Assets.img("downArrow.png"), 425, 520, 50);
        panel.add(downArrow);
        panel.setComponentZOrder(downArrow, 0);
        downArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "A");
        });

        return panel;
    }

    private JPanel createLobbyPartB() {

        BackgroundPanel panel = new BackgroundPanel(Assets.img("lobbyB.jpg"));

        // Liam (Bellboy)
        JLabel liam = createScaledIconLabel(Assets.img("liam.png"), 130, 230, 170, 280);
        panel.add(liam);

        liam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showInteractionMenu("LIAM", 
                    () -> { // Talk Action
                        String[] dialogue;
                        if (!GameState.getInstance().hasClue("Mysterious note")) {
                            dialogue = new String[]{
                                "Liam: I saw someone near Room 217 last night... They looked... familiar.",
                                "Liam: They dropped this note. I don't know what to make of it.",
                                "Liam: [CLUE FOUND: Mysterious note]"
                            };
                            GameState.getInstance().addClue("Mysterious note");
                        } else {
                            dialogue = new String[]{
                                "Liam: I already gave you that note, detective. Please be careful."
                            };
                        }
                        startDialogue(dialogue);
                    },
                    () -> { // Examine Action
                        startDialogue(new String[]{"Liam is fidgeting with his bell. He seems anxious, constantly looking over his shoulder."});
                    }
                );
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                liam.setCursor(new Cursor(Cursor.HAND_CURSOR));
                liam.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                liam.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                liam.setBorder(null);
            }
        });

        // Elevator Button
        JButton elevatorButton = new JButton("Elevator");
        elevatorButton.setBounds(UiScale.x(650), UiScale.y(250), UiScale.s(100), UiScale.s(50));
        panel.add(elevatorButton);
        elevatorButton.addActionListener(e -> {
             if (GameState.getInstance().lobbyComplete) {
                 MainGame.getInstance().switchFloor("FLOOR1");
             } else {
                 typeText("The elevator is locked. I should talk to everyone and gather clues first.");
             }
         });

        JButton leftArrow = createArrowButton(Assets.img("leftArrow.png"), 30, 250, 50);
        panel.add(leftArrow);

        leftArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "A");
        });

        return panel;
    }
}
