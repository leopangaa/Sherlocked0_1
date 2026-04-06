package view.screens;

import core.GameState;
import main.MainGame;
import utils.Assets;
import utils.UiScale;
import view.components.BackgroundPanel;
import view.components.DialogueUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LobbyPanel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    DialogueUI dialogueUI;
    LiamQuestPanel liamQuestPanel;
    RegisterPuzzlePanel registerPuzzlePanel;
    JPanel inputBlocker;

    private static final Border GLOW_BORDER =
            BorderFactory.createLineBorder(new Color(255, 255, 255, 110), 2);

    public LobbyPanel() {

        setLayout(null);

        dialogueUI = new DialogueUI();

        liamQuestPanel = new LiamQuestPanel(success -> {
            inputBlocker.setVisible(false);
            setButtonsEnabled(areaContainer, true);
            MainGame.getInstance().setHudEnabled(true);

            if (success) {
                String[] successDialogue = {
                        "Liam: Yes! That's it! I remember now.",
                        "Liam: They dropped this note. I don't know what to make of it.",
                        "Liam: [CLUE FOUND: Mysterious note]"
                };
                startDialogue(successDialogue, () -> GameState.getInstance().addClue("Mysterious note"));
            } else {
                startDialogue(new String[]{"Liam: Sorry! It seems like you can't help me too."});
            }
        });

        registerPuzzlePanel = new RegisterPuzzlePanel(success -> {
            inputBlocker.setVisible(false);
            setButtonsEnabled(areaContainer, true);
            MainGame.getInstance().setHudEnabled(true);

            if (success) {
                String[] dialogue = {
                        "The guest register is open to today's date.",
                        "You see Dr. Kells' name, but it's crossed out in red ink.",
                        "Next to it, someone has written a single word: 'LIAR'.",
                        "There's another entry from someone named 'E. Vane' in Room 305."
                };
                startDialogue(dialogue, () -> GameState.getInstance().addClue("Guest Register entry"));
            } else {
                startDialogue(new String[]{"The lock on the register clicks back into place. That wasn't the right choice."});
            }
        });

        areaLayout = new CardLayout();
        areaContainer = new JPanel(areaLayout);

        inputBlocker = new JPanel();
        inputBlocker.setLayout(null);
        inputBlocker.setOpaque(false);
        inputBlocker.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);
        inputBlocker.setVisible(false);
        inputBlocker.addMouseListener(new MouseAdapter() {});

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

        add(liamQuestPanel, 0);
        add(registerPuzzlePanel, 1);
        add(dialogueUI, 2);
        add(inputBlocker, 3);
        add(areaContainer, 4);
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

    private JLabel createClickableHotspotLabel(int baseX, int baseY, int baseW, int baseH) {
        JLabel label = new JLabel();
        label.setBounds(UiScale.x(baseX), UiScale.y(baseY), UiScale.s(baseW), UiScale.s(baseH));
        label.setOpaque(false);
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

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!btn.isEnabled()) return;
                Rectangle orig = (Rectangle) btn.getClientProperty("origBounds");
                ImageIcon hi = (ImageIcon) btn.getClientProperty("hoverIcon");
                btn.setIcon(hi);
                int dx = (hi.getIconWidth() - orig.width) / 2;
                int dy = (hi.getIconHeight() - orig.height) / 2;
                btn.setBounds(orig.x - dx, orig.y - dy, hi.getIconWidth(), hi.getIconHeight());
                btn.repaint();
            }

            public void mouseExited(MouseEvent evt) {
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

    private void applyHoverEffectToLabel(JLabel label) {
        final Rectangle originalBounds = label.getBounds();

        label.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));

                int newWidth = (int) Math.round(originalBounds.width * 1.08);
                int newHeight = (int) Math.round(originalBounds.height * 1.08);

                int dx = (newWidth - originalBounds.width) / 2;
                int dy = (newHeight - originalBounds.height) / 2;

                label.setBounds(
                        originalBounds.x - dx,
                        originalBounds.y - dy,
                        newWidth,
                        newHeight
                );

                label.setBorder(GLOW_BORDER);
                label.repaint();
            }

            public void mouseExited(MouseEvent evt) {
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                label.setBounds(originalBounds);
                label.setBorder(null);
                label.repaint();
            }
        });
    }

    private JPanel createLobbyPartA() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("lobbyA.jpg"));

        // Mrs. Vale Hotspot (RECEPTIONIST IMAGE)
        JLabel mrsVale = createScaledIconLabel(Assets.img("receptionist.png"), 380, 160, 100, 130);
        panel.add(mrsVale);
        applyHoverEffectToLabel(mrsVale);

        mrsVale.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!mrsVale.isEnabled()) return;
                showInteractionMenu("MRS. VALE",
                        () -> {
                            if (!GameState.getInstance().hasClue("Dr. Kells photo")) {
                                String[] dialogue = {
                                        "Mrs. Vale: Welcome to the hotel, detective. It's a tragedy about Dr. Kells.",
                                        "Mrs. Vale: He was such a regular guest here. Always in Room 217.",
                                        "Mrs. Vale: I found some photos of him, but they are all mixed up.",
                                        "Mrs. Vale: If you can help me sort these files, I'll let you keep the photos."
                                };
                                startDialogue(dialogue, () -> MainGame.getInstance().openPuzzle("Dr. Kells photo", "LOBBY", "EASY"));
                            } else {
                                startDialogue(new String[]{"Mrs. Vale: I hope those photos help with your investigation, detective."});
                            }
                        },
                        () -> startDialogue(new String[]{
                                "Mrs. Vale looks exhausted. Her eyes are bloodshot, as if she hasn't slept in days."
                        })
                );
            }
        });

        // Mirror Hotspot
        JLabel mirror = createClickableHotspotLabel(210, 110, 80, 140);
        panel.add(mirror);
        applyHoverEffectToLabel(mirror);
        mirror.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!mirror.isEnabled()) return;
                showInteractionMenu("ORNATE MIRROR",
                        null,
                        () -> {
                            String[] dialogue = {
                                    "The mirror is covered in a thin layer of dust.",
                                    "As you look closer, you notice something faint in the reflection.",
                                    "It's a series of numbers written on the opposite wall, only visible from this angle.",
                                    "[CLUE FOUND: Mirror reflection hint]"
                            };
                            startDialogue(dialogue, () -> {
                                MainGame.getInstance().openPuzzle("Mirror reflection hint", "LOBBY", "EASY");
                            });
                        }
                );
            }
        });

        // Guest Register Hotspot
        JLabel register = createClickableHotspotLabel(470, 275, 70, 40);
        panel.add(register);
        applyHoverEffectToLabel(register);
        register.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!register.isEnabled()) return;
                showInteractionMenu("GUEST REGISTER",
                        null,
                        () -> {
                            inputBlocker.setVisible(true);
                            setButtonsEnabled(areaContainer, false);
                            MainGame.getInstance().setHudEnabled(false);
                            registerPuzzlePanel.startPuzzle();
                        }
                );
            }
        });

        // Elevator Hotspot
        JLabel elevator = createClickableHotspotLabel(650, 120, 100, 200);
        panel.add(elevator);
        applyHoverEffectToLabel(elevator);
        elevator.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!elevator.isEnabled()) return;
                if (GameState.getInstance().lobbyComplete) {
                    GameState.getInstance().setCurrentFloor(1);
                    MainGame.getInstance().switchFloor("FLOOR1");
                } else {
                    startDialogue(new String[]{"The elevator is locked. You should finish your investigation here first."});
                }
            }
        });

        JButton rightArrow = createArrowButton(Assets.img("rightArrow.png"), 820, 250, 50);
        panel.add(rightArrow);

        JButton downArrow = createArrowButton(Assets.img("downArrow.png"), 425, 520, 50);
        panel.add(downArrow);

        JButton upArrow = createArrowButton(Assets.img("upArrow.png"), 425, 20, 50);
        panel.add(upArrow);

        rightArrow.addActionListener(e -> areaLayout.show(areaContainer, "B"));
        downArrow.addActionListener(e -> areaLayout.show(areaContainer, "C"));
        upArrow.addActionListener(e -> areaLayout.show(areaContainer, "D"));

        return panel;
    }

    private JPanel createLobbyPartC() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("lobbyC.jpg"));

        JLabel clock = createClickableHotspotLabel(627, 50, 50, 50);
        panel.add(clock);
        applyHoverEffectToLabel(clock);

        clock.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                showInteractionMenu("BROKEN CLOCK", null, () -> {
                    String[] dialogue = {
                            "The clock is frozen at 11:45.",
                            "Wait, the second hand is moving backwards.",
                            "Tick... Tock... Tick... Tock...",
                            "A chilling whisper fills your ears: 'TIME IS RUNNING OUT, DETECTIVE.'"
                    };
                    startDialogue(dialogue);
                    GameState.getInstance().addClue("Frozen Clock hint");
                });
            }
        });

        JLabel suitcase = createScaledIconLabel(Assets.img("suitcase.png"), 208, 358, 145, 75);
        panel.add(suitcase);
        applyHoverEffectToLabel(suitcase);

        suitcase.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
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
        });

        JButton upArrow = createArrowButton(Assets.img("upArrow.png"), 425, 20, 50);
        panel.add(upArrow);
        upArrow.addActionListener(e -> areaLayout.show(areaContainer, "A"));

        return panel;
    }

    private JPanel createLobbyPartD() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("lobbyD.jpg"));

        JLabel register = createClickableHotspotLabel(647, 300, 280, 280);
        panel.add(register);
        applyHoverEffectToLabel(register);

        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                showInteractionMenu("GUEST REGISTER", null, () -> {
                    if (!GameState.getInstance().hasClue("Guest Register entry")) {
                        String[] preDialogue = {
                                "The guest register is locked with a numerical security plate.",
                                "A small display shows several numbers. You need to identify the correct sequence trigger."
                        };
                        startDialogue(preDialogue, () -> {
                            inputBlocker.setVisible(true);
                            setButtonsEnabled(areaContainer, false);
                            MainGame.getInstance().setHudEnabled(false);
                            registerPuzzlePanel.startPuzzle();
                        });
                    } else {
                        String[] dialogue = {
                                "The guest register is open to today's date.",
                                "You see Dr. Kells' name, but it's crossed out in red ink.",
                                "Next to it, someone has written a single word: 'LIAR'.",
                                "There's another entry from someone named 'E. Vane' in Room 305."
                        };
                        startDialogue(dialogue);
                    }
                });
            }
        });

        JLabel mirror = createClickableHotspotLabel(380, 140, 115, 175);
        panel.add(mirror);
        applyHoverEffectToLabel(mirror);

        mirror.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
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
        });

        JButton downArrow = createArrowButton(Assets.img("downArrow.png"), 425, 520, 50);
        panel.add(downArrow);
        panel.setComponentZOrder(downArrow, 0);
        downArrow.addActionListener(e -> areaLayout.show(areaContainer, "A"));

        return panel;
    }

    private JPanel createLobbyPartB() {

        BackgroundPanel panel = new BackgroundPanel(Assets.img("lobbyB.jpg"));

        JLabel liam = createScaledIconLabel(Assets.img("liam.png"), 130, 230, 170, 280);
        panel.add(liam);
        applyHoverEffectToLabel(liam);

        liam.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                showInteractionMenu("LIAM",
                        () -> {
                            if (!GameState.getInstance().hasClue("Mysterious note")) {
                                String[] initialDialogue = {
                                        "Liam: I saw someone near Room 217 last night... They looked... familiar.",
                                        "Liam: They dropped a note, but I can't quite remember the order of the numbers on it.",
                                        "Liam: Can you help me pick the right one? If we get it wrong, I might just forget it entirely."
                                };
                                startDialogue(initialDialogue, () -> {
                                     inputBlocker.setVisible(true);
                                     setButtonsEnabled(areaContainer, false);
                                     MainGame.getInstance().setHudEnabled(false);
                                     liamQuestPanel.startQuest();
                                 });
                            } else {
                                startDialogue(new String[]{
                                        "Liam: I already gave you that note, detective. Please be careful."
                                });
                            }
                        },
                        () -> startDialogue(new String[]{
                                "Liam is fidgeting with his bell. He seems anxious, constantly looking over his shoulder."
                        })
                );
            }
        });

        JLabel elevatorLabel = createClickableHotspotLabel(395, 200, 100, 50);
        panel.add(elevatorLabel);
        applyHoverEffectToLabel(elevatorLabel);

        elevatorLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (GameState.getInstance().lobbyComplete) {
                    MainGame.getInstance().switchFloor("FLOOR1");
                } else {
                    typeText("The elevator is locked. I should talk to everyone and gather clues first.");
                }
            }
        });

        JButton leftArrow = createArrowButton(Assets.img("leftArrow.png"), 30, 250, 50);
        panel.add(leftArrow);
        leftArrow.addActionListener(e -> areaLayout.show(areaContainer, "A"));

        return panel;
    }
}
