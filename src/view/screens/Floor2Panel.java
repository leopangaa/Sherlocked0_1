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

public class Floor2Panel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    DialogueUI dialogueUI;
    JPanel inputBlocker;

    private static final Border GLOW_BORDER =
            BorderFactory.createLineBorder(new Color(255, 255, 255, 110), 2);

    public Floor2Panel() {

        setLayout(null);

        dialogueUI = new DialogueUI();

        areaLayout = new CardLayout();
        areaContainer = new JPanel(areaLayout);

        inputBlocker = new JPanel();
        inputBlocker.setLayout(null);
        inputBlocker.setOpaque(false);
        inputBlocker.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);
        inputBlocker.setVisible(false);
        inputBlocker.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dialogueUI.isVisible() && !dialogueUI.getBounds().contains(e.getPoint())) {
                    dialogueUI.setVisible(false);
                    inputBlocker.setVisible(false);
                    setButtonsEnabled(areaContainer, true);
                    MainGame.getInstance().setHudEnabled(true);
                }
            }
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });

        dialogueUI.setVisibilityListener(visible -> {
            inputBlocker.setVisible(visible);
            setButtonsEnabled(areaContainer, !visible);
            MainGame.getInstance().setHudEnabled(!visible);
            areaContainer.revalidate();
            areaContainer.repaint();
        });

        JPanel part2A = createPart2A();
        JPanel part2B = createPart2B();
        JPanel part2C = createPart2C();

        areaContainer.add(part2A, "PART2A");
        areaContainer.add(part2B, "PART2B");
        areaContainer.add(part2C, "PART2C");
        areaContainer.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        add(dialogueUI, 0);
        add(inputBlocker, 1);
        add(areaContainer, 2);
    }

    private void setButtonsEnabled(Container root, boolean enabled) {
        for (Component c : root.getComponents()) {
            if (c instanceof AbstractButton || c instanceof JLabel) {
                c.setEnabled(enabled);
                if (!enabled) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        label.setBorder(null);
                        Rectangle orig = (Rectangle) label.getClientProperty("originalBounds");
                        if (orig != null) label.setBounds(orig);
                    }
                    if (c instanceof JButton) {
                        JButton btn = (JButton) c;
                        ImageIcon normal = (ImageIcon) btn.getClientProperty("normalIcon");
                        Rectangle orig = (Rectangle) btn.getClientProperty("origBounds");
                        if (normal != null) btn.setIcon(normal);
                        if (orig != null) btn.setBounds(orig);
                    }
                }
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

    private JLabel createClickableHotspotLabel(int baseX, int baseY, int baseW, int baseH) {
        JLabel label = new JLabel();
        label.setBounds(UiScale.x(baseX), UiScale.y(baseY), UiScale.s(baseW), UiScale.s(baseH));
        label.setOpaque(false);
        return label;
    }

    private void applyHoverEffectToLabel(JLabel label) {
        label.putClientProperty("originalBounds", label.getBounds());

        label.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!label.isEnabled()) return;
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));

                Rectangle originalBounds = (Rectangle) label.getClientProperty("originalBounds");
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
                if (!label.isEnabled()) return;
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Rectangle originalBounds = (Rectangle) label.getClientProperty("originalBounds");
                label.setBounds(originalBounds);
                label.setBorder(null);
                label.repaint();
            }
        });
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

    private void checkFloorCompletion() {
        GameState gs = GameState.getInstance();
        if (gs.hasClue("Rina Testimony") &&
            gs.hasClue("Jared Statement") &&
            gs.hasClue("Hidden Journal") &&
            gs.hasClue("CCTV Footage Anomaly") &&
            gs.hasClue("Patient Record")) {
            gs.floor2Complete = true;
        }
    }

    private void triggerChapterEnding() {
        MainGame.getInstance().triggerChapterEnd();
    }

    private JPanel createPart2A() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part2A.jpg"));

        // Rina Hotspot
        JLabel rina = createClickableHotspotLabel(200, 200, 150, 200);
        panel.add(rina);
        applyHoverEffectToLabel(rina);
        rina.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!rina.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("Rina Testimony")) {
                    startDialogue(new String[]{"Rina: Thank you for helping me remember, detective. I'm much calmer now."});
                } else {
                    showInteractionMenu("RINA",
                            () -> startDialogue(new String[]{
                                    "Rina: Oh, Detective! I'm so confused... I remember cleaning the rooms near 217, but the room numbers are all jumbled in my head.",
                                    "Rina: If you could help me identify which rooms I was in, I might remember what I saw there.",
                                    "Detective: Don't worry, Rina. Just tell me the sequences you remember, and I'll help you pick the right ones.",
                                    "[QUEST: HELP RINA REMEMBER]"
                            }, () -> {
                                MainGame.getInstance().openRinaQuest("Rina Testimony", "FLOOR2");
                            }),
                            () -> startDialogue(new String[]{"Rina is avoiding eye contact. Her hands are shaking slightly as she adjusts her apron."})
                    );
                }
            }
        });

        // Jared Hotspot
        JLabel jared = createClickableHotspotLabel(600, 180, 100, 220);
        panel.add(jared);
        applyHoverEffectToLabel(jared);
        jared.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!jared.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("Jared Statement")) {
                    startDialogue(new String[]{"Jared: I've told you what I saw, Detective. Please, just leave me be."});
                } else {
                    showInteractionMenu("JARED",
                            () -> startDialogue(new String[]{
                                    "Jared: Area is secure, detective. Everything is under control.",
                                    "Jared: I've been on duty all night. No unauthorized access.",
                                    "Detective: You seem adamant, Jared... but your eyes are wavering.",
                                    "Detective: Let me check your confirmation again. Are you absolutely sure?",
                                    "[QUEST: QUESTION JARED]"
                            }, () -> {
                                MainGame.getInstance().openJaredQuest("Jared Statement", "FLOOR2");
                            }),
                            () -> startDialogue(new String[]{"Jared stands perfectly still. His responses feel rehearsed, almost robotic."})
                    );
                }
            }
        });

        // Cleaning Cart Hotspot
        JLabel cart = createClickableHotspotLabel(320, 370, 170, 140);
        panel.add(cart);
        applyHoverEffectToLabel(cart);
        cart.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!cart.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("Abandoned Cleaning Cart")) {
                    startDialogue(new String[]{"The cart is stained with dried blood. Someone was cleaning something they shouldn't have."});
                } else {
                    showInteractionMenu("CLEANING CART",
                            null,
                            () -> startDialogue(new String[]{
                                    "An abandoned cleaning cart. It looks like someone left in a hurry.",
                                    "Wait... there's something jumbled under these cleaning supplies.",
                                    "[QUEST: INVESTIGATE CLEANING CART]"
                            }, () -> {
                                MainGame.getInstance().openCleaningCartQuest("Abandoned Cleaning Cart", "FLOOR2");
                            })
                    );
                }
            }
        });

        // Staff Notice Board
        JLabel noticeBoard = createClickableHotspotLabel(750, 150, 150, 200);
        panel.add(noticeBoard);
        applyHoverEffectToLabel(noticeBoard);
        noticeBoard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!noticeBoard.isEnabled()) return;
                showInteractionMenu("NOTICE BOARD",
                        null,
                        () -> startDialogue(new String[]{
                                "A notice for the staff: 'REPORT ANY UNUSUAL BEHAVIOR IMMEDIATELY'.",
                                "[CLUE FOUND: Staff Warning Notice]"
                        }, () -> GameState.getInstance().addClue("Staff Warning Notice"))
                );
            }
        });

        // Elevator Hotspot
        JLabel elevator = createClickableHotspotLabel(530, 220, 110, 170);
        panel.add(elevator);
        applyHoverEffectToLabel(elevator);
        elevator.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!elevator.isEnabled()) return;
                if (!GameState.getInstance().floor2Complete) {
                    typeText("I still need to understand what happened here.");
                } else {
                    triggerChapterEnding();
                }
            }
        });

        // Navigation to Staff Room
        JButton toStaffRoom = createArrowButton(Assets.img("upArrow.png"), 425, 20, 60);
        panel.add(toStaffRoom);
        toStaffRoom.addActionListener(e -> areaLayout.show(areaContainer, "PART2B"));

        // Navigation to Security Room
        JButton toSecurity = createArrowButton(Assets.img("leftArrow.png"), 20, 250, 60);
        panel.add(toSecurity);
        toSecurity.addActionListener(e -> areaLayout.show(areaContainer, "PART2C"));

        return panel;
    }

    private JPanel createPart2B() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part2B.jpg"));

        // Bed Hotspot
        JLabel bed = createClickableHotspotLabel(50, 380, 350, 80);
        panel.add(bed);
        applyHoverEffectToLabel(bed);
        bed.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!bed.isEnabled()) return;
                showInteractionMenu("BED",
                        null,
                        () -> startDialogue(new String[]{
                                "The bed is made, but the pillows are indented as if someone was sitting here recently.",
                                "[CLUE FOUND: Used Bedding]"
                        }, () -> GameState.getInstance().addClue("Used Bedding"))
                );
            }
        });

        // Locker Hotspot
        JLabel locker = createClickableHotspotLabel(450, 150, 70, 350);
        panel.add(locker);
        applyHoverEffectToLabel(locker);
        locker.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!locker.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("Hidden Journal")) {
                    startDialogue(new String[]{"The locker is open. I've already retrieved the journal from inside."});
                } else {
                    showInteractionMenu("LOCKER",
                            null,
                            () -> startDialogue(new String[]{
                                    "A metal staff locker. It's locked with a 5-digit electronic keypad.",
                                    "I need the correct sequence to open it.",
                                    "[PUZZLE: UNLOCK LOCKER]"
                            }, () -> {
                                MainGame.getInstance().openLockerQuest("Hidden Journal", "FLOOR2");
                            })
                    );
                }
            }
        });

        // Mirror Hotspot
        JLabel mirror = createClickableHotspotLabel(700, 100, 200, 250);
        panel.add(mirror);
        applyHoverEffectToLabel(mirror);
        mirror.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!mirror.isEnabled()) return;
                showInteractionMenu("MIRROR",
                        null,
                        () -> startDialogue(new String[]{
                                "The reflection in the mirror... it doesn't look like me.",
                                "The features are blurry, as if my own mind is refusing to recognize my face.",
                                "[CLUE FOUND: Distorted Self Image]"
                        }, () -> GameState.getInstance().addClue("Distorted Self Image"))
                );
            }
        });

        // Desk Hotspot
        JLabel desk = createClickableHotspotLabel(630, 400, 270, 100);
        panel.add(desk);
        applyHoverEffectToLabel(desk);
        desk.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!desk.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("Personal Notes")) {
                    startDialogue(new String[]{"The desk is open. I've already collected the notes from inside."});
                } else {
                    showInteractionMenu("DESK",
                            null,
                            () -> startDialogue(new String[]{
                                    "A sturdy wooden desk. It's locked with a small electronic keypad.",
                                    "I need to crack the code to see what's inside.",
                                    "[PUZZLE: UNLOCK DESK]"
                            }, () -> {
                                MainGame.getInstance().openDeskPuzzle("Personal Notes", "FLOOR2");
                            })
                    );
                }
            }
        });

        // Navigation back to Hallway
        JButton toHallway = createArrowButton(Assets.img("downArrow.png"), 425, 500, 60);
        panel.add(toHallway);
        toHallway.addActionListener(e -> areaLayout.show(areaContainer, "PART2A"));

        return panel;
    }

    private JPanel createPart2C() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part2C.jpg"));

        // CCTV Monitors Hotspot
        JLabel monitor = createClickableHotspotLabel(120, 50, 350, 250);
        panel.add(monitor);
        applyHoverEffectToLabel(monitor);
        monitor.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!monitor.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("CCTV Footage Anomaly")) {
                    startDialogue(new String[]{
                            "The monitors are showing a steady feed now.",
                            "Wait... I still see myself in that footage, standing outside Room 217 at the time of the incident.",
                            "The anomaly is definitely real."
                    });
                } else {
                    // CCTV Flicker Effect
                    Timer flicker = new Timer(50, null);
                    final int[] count = {0};
                    flicker.addActionListener(e -> {
                        count[0]++;
                        monitor.setVisible(count[0] % 2 == 0);
                        if (count[0] > 6) {
                            flicker.stop();
                            monitor.setVisible(true);
                            
                            showInteractionMenu("CCTV MONITORS",
                                    null,
                                    () -> startDialogue(new String[]{
                                            "The monitor flickers violently with static.",
                                            "A sudden glitch... I see myself in the footage, standing outside Room 217.",
                                            "But that's impossible. I wasn't there...",
                                            "[CLUE FOUND: CCTV Footage Anomaly]"
                                    }, () -> {
                                        GameState.getInstance().addClue("CCTV Footage Anomaly");
                                        checkFloorCompletion();
                                    })
                            );
                        }
                    });
                    flicker.start();
                }
            }
        });

        // Computer Terminal Hotspot
        JLabel terminal = createClickableHotspotLabel(475, 270, 130, 100);
        panel.add(terminal);
        applyHoverEffectToLabel(terminal);
        terminal.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!terminal.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("Access Logs")) {
                    startDialogue(new String[]{
                            "The terminal is unlocked. The access logs confirm Room 217 was accessed using my master keycard.",
                            "The code used for the bypass... it's ingrained in my memory."
                    });
                } else {
                    showInteractionMenu("TERMINAL",
                            null,
                            () -> startDialogue(new String[]{
                                    "The nursing station terminal. It's locked behind a security bypass screen.",
                                    "It's requesting a missing digit for an authorization sequence.",
                                    "[PUZZLE: TERMINAL BYPASS]"
                            }, () -> {
                                MainGame.getInstance().openTerminalPuzzle("Access Logs", "FLOOR2");
                            })
                    );
                }
            }
        });

        // Files / Binder Hotspot
        JLabel binder = createClickableHotspotLabel(100, 400, 150, 100);
        panel.add(binder);
        applyHoverEffectToLabel(binder);
        binder.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!binder.isEnabled()) return;
                showInteractionMenu("PATIENT RECORDS",
                        null,
                        () -> startDialogue(new String[]{
                                "A thick binder labeled 'Confidential'.",
                                "I find a patient record with my own name and photo.",
                                "Diagnosis: 'Severe trauma-induced dissociative identity disorder'.",
                                "Scrawled in the margin of the last page is a number: 32142.",
                                "I'm not sure what it means yet.",
                                "[CLUE FOUND: Patient Record]"
                        }, () -> {
                            GameState.getInstance().addClue("Patient Record");
                            checkFloorCompletion();
                        })
                );
            }
        });

        // Filing Cabinet Hotspot
        JLabel cabinet = createClickableHotspotLabel(750, 300, 140, 80);
        panel.add(cabinet);
        applyHoverEffectToLabel(cabinet);
        cabinet.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!cabinet.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("Archived Reports")) {
                    startDialogue(new String[]{"The cabinet is open. I've already collected the reports from inside."});
                } else {
                    showInteractionMenu("CABINET",
                            null,
                            () -> startDialogue(new String[]{
                                    "Archived reports from years ago. The drawer is jammed tight.",
                                    "I need to apply some force to the correct mechanism to pull it open.",
                                    "[PUZZLE: OPEN FILING CABINET]"
                            }, () -> {
                                MainGame.getInstance().openCabinetPuzzle("Archived Reports", "FLOOR2");
                            })
                    );
                }
            }
        });

        // Navigation back to Hallway
        JButton toHallway = createArrowButton(Assets.img("rightArrow.png"), 820, 250, 60);
        panel.add(toHallway);
        toHallway.addActionListener(e -> areaLayout.show(areaContainer, "PART2A"));

        return panel;
    }
}
