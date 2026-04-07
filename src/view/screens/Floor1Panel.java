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

public class Floor1Panel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    DialogueUI dialogueUI;
    JPanel inputBlocker;

    private static final Border GLOW_BORDER =
            BorderFactory.createLineBorder(new Color(255, 255, 255, 110), 2);

    public Floor1Panel() {

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

        JPanel part1A = createPart1A();
        JPanel part1B = createPart1B();
        JPanel part1C = createPart1C();

        areaContainer.add(part1A, "PART1A");
        areaContainer.add(part1B, "PART1B");
        areaContainer.add(part1C, "PART1C");
        areaContainer.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        GameState.getInstance().addListener(this::checkFloorCompletion);

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
        if (gs.hasClue("Harper Testimony") &&
            gs.hasClue("Doyle Statement") &&
            gs.hasClue("Signs of Struggle") &&
            gs.hasClue("CCTV Footage Anomaly") &&
            gs.hasClue("Sealed Window")) {
            gs.floor1Complete = true;
        }
    }

    private JPanel createPart1A() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part1A.jpg"));

        // Ms. Harper Hotspot
        JLabel msHarper = createClickableHotspotLabel(550, 180, 120, 180);
        panel.add(msHarper);
        applyHoverEffectToLabel(msHarper);
        msHarper.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!msHarper.isEnabled()) return;
                showInteractionMenu("MS. HARPER",
                        () -> {
                            if (!GameState.getInstance().hasClue("Harper Testimony")) {
                                startDialogue(new String[]{
                                        "Ms. Harper: Oh, detective... I'm so sorry. I saw someone leave that room, but my eyes... they're playing tricks on me.",
                                        "Ms. Harper: Everything is a blur. I can't quite distinguish the shapes anymore. It's like a headache that won't go away.",
                                        "Ms. Harper: Could you help me focus? Maybe if I can just clear my vision, I'll remember clearly."
                                }, () -> MainGame.getInstance().openHarperPuzzle("Harper Testimony", "FLOOR1"));
                            } else {
                                startDialogue(new String[]{
                                        "Ms. Harper: Thank you so much for helping me focus, detective. It's like a fog has lifted from my mind.",
                                        "Ms. Harper: Now I am sure of it! The person I saw was carrying a heavy bag and heading towards the utility room."
                                });
                            }
                        },
                        () -> startDialogue(new String[]{"Ms. Harper looks nervous, clutching her shawl as if she's afraid of her own shadow."})
                );
            }
        });

        // Mr. Doyle Hotspot
        JLabel mrDoyle = createClickableHotspotLabel(670, 180, 100, 220);
        panel.add(mrDoyle);
        applyHoverEffectToLabel(mrDoyle);
        mrDoyle.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!mrDoyle.isEnabled()) return;
                showInteractionMenu("MR. DOYLE",
                        () -> startDialogue(new String[]{
                                "Mr. Doyle: No one came in or out.",
                                "Mr. Doyle: I’ve been here all night.",
                                "Mr. Doyle: You should reconsider your assumptions, detective.",
                                "[CLUE FOUND: Doyle Statement]"
                        }, () -> {
                            GameState.getInstance().addClue("Doyle Statement");
                        }),
                        () -> startDialogue(new String[]{"Mr. Doyle has a stoic expression. He seems like the kind of man who doesn't miss much."})
                );
            }
        });

        // Room 217 Door
        JLabel door217 = createClickableHotspotLabel(440, 160, 100, 250);
        panel.add(door217);
        applyHoverEffectToLabel(door217);
        door217.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!door217.isEnabled()) return;
                showInteractionMenu("ROOM 217",
                        () -> {
                            if (GameState.getInstance().hasClue("Room 217 Key")) {
                                startDialogue(new String[]{
                                        "I use the key I found on the cleaning cart.",
                                        "The lock clicks. The door creaks as it opens..."
                                }, () -> areaLayout.show(areaContainer, "PART1B"));
                            } else {
                                startDialogue(new String[]{
                                        "The door is locked tight.",
                                        "I'll need to find a way to open this. Maybe the staff has a spare key somewhere?"
                                });
                            }
                        },
                        () -> startDialogue(new String[]{"The brass numbers on the door are polished. Room 217. The air feels colder here."})
                );
            }
        });

        // Floor Papers / Stain
        JLabel floorStain = createClickableHotspotLabel(145, 450, 80, 40);
        panel.add(floorStain);
        applyHoverEffectToLabel(floorStain);
        floorStain.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!floorStain.isEnabled()) return;
                showInteractionMenu("FLOOR STAIN",
                        null,
                        () -> startDialogue(new String[]{
                                "There's a strange stain on the carpet... almost like something was dragged.",
                                "[CLUE FOUND: Strange Stain]"
                        }, () -> {
                            GameState.getInstance().addClue("Strange Stain");
                            checkFloorCompletion();
                        })
                );
            }
        });

        // Elevator Hotspot
        JLabel elevator = createClickableHotspotLabel(175, 140, 230, 230);
        panel.add(elevator);
        applyHoverEffectToLabel(elevator);
        elevator.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!elevator.isEnabled()) return;
                if (!GameState.getInstance().floor1Complete) {
                    typeText("I should investigate everything first.");
                } else {
                    MainGame.getInstance().switchFloor("FLOOR2");
                }
            }
        });

        // Navigation
        JButton toUtility = createArrowButton(Assets.img("leftArrow.png"), 20, 250, 60);
        panel.add(toUtility);
        toUtility.addActionListener(e -> areaLayout.show(areaContainer, "PART1C"));

        return panel;
    }

    private JPanel createPart1B() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part1B.jpg"));

        // Bed Hotspot
        JLabel bed = createClickableHotspotLabel(110, 370, 560, 55);
        panel.add(bed);
        applyHoverEffectToLabel(bed);
        bed.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!bed.isEnabled()) return;
                showInteractionMenu("BED",
                        null,
                        () -> startDialogue(new String[]{
                                "The sheets are rumpled and tossed aside.",
                                "Signs of struggle... but who was the victim?",
                                "[CLUE FOUND: Signs of Struggle]"
                        }, () -> {
                            GameState.getInstance().addClue("Signs of Struggle");
                            checkFloorCompletion();
                        })
                );
            }
        });

        // Mirror Hotspot
        JLabel mirror = createClickableHotspotLabel(730, 100, 100, 100);
        panel.add(mirror);
        applyHoverEffectToLabel(mirror);
        mirror.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!mirror.isEnabled()) return;
                showInteractionMenu("MIRROR",
                        null,
                        () -> startDialogue(new String[]{
                                "The glass is old and slightly warped.",
                                "My reflection feels... delayed. Almost like it's watching me.",
                                "[CLUE FOUND: Distorted Reflection]"
                        }, () -> GameState.getInstance().addClue("Distorted Reflection"))
                );
            }
        });

        // Desk Hotspot
        JLabel desk = createClickableHotspotLabel(620, 300, 300, 40);
        panel.add(desk);
        applyHoverEffectToLabel(desk);
        desk.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!desk.isEnabled()) return;
                showInteractionMenu("DESK",
                        null,
                        () -> startDialogue(new String[]{
                                "A scattered pile of papers sits on the desk.",
                                "It's a fragment of therapy notes. 'Subject shows signs of severe dissociation...'",
                                "[CLUE FOUND: Therapy Notes Fragment]"
                        }, () -> GameState.getInstance().addClue("Therapy Notes Fragment"))
                );
            }
        });

        // Window Hotspot
        JLabel window = createClickableHotspotLabel(480, 80, 170, 170);
        panel.add(window);
        applyHoverEffectToLabel(window);
        window.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!window.isEnabled()) return;
                showInteractionMenu("WINDOW",
                        null,
                        () -> {
                            if (!GameState.getInstance().hasClue("Sealed Window")) {
                                startDialogue(new String[]{
                                        "The window is sealed shut from the inside.",
                                        "Wait... there are faint marks on the glass. Letters?",
                                        "They're all jumbled up. I need to decipher this."
                                }, () -> MainGame.getInstance().openWindowPuzzle("Sealed Window", "FLOOR1"));
                            } else {
                                startDialogue(new String[]{
                                        "The window is sealed shut from the inside. There's no way out this way.",
                                        "The decrypted message 'MEMORYLIAR' still haunts the glass."
                                });
                            }
                        }
                );
            }
        });

        // Navigation back to Hallway
        JButton toHallway = createArrowButton(Assets.img("downArrow.png"), 425, 500, 60);
        panel.add(toHallway);
        toHallway.addActionListener(e -> areaLayout.show(areaContainer, "PART1A"));

        return panel;
    }

    private JPanel createPart1C() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part1C.jpg"));

        // Cleaning Cart Hotspot
        JLabel cart = createClickableHotspotLabel(60, 300, 250, 170);
        panel.add(cart);
        applyHoverEffectToLabel(cart);
        cart.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!cart.isEnabled()) return;
                showInteractionMenu("CLEANING CART",
                        null,
                        () -> {
                            if (!GameState.getInstance().hasClue("Room 217 Key")) {
                                startDialogue(new String[]{
                                        "A cleaning cart left in the utility room.",
                                        "Wait... there's something shiny hanging from a small hook on the side.",
                                        "It's a key! There's a small tag on it: '217'.",
                                        "[ITEM FOUND: Room 217 Key]"
                                }, () -> GameState.getInstance().addClue("Room 217 Key"));
                            } else {
                                startDialogue(new String[]{
                                        "A cleaning cart left in the utility room.",
                                        "A heavy-duty bleach bottle is missing. The slot for it is empty.",
                                        "[CLUE FOUND: Missing Cleaning Tool]"
                                }, () -> GameState.getInstance().addClue("Missing Cleaning Tool"));
                            }
                        }
                );
            }
        });

        // Trash Bin Hotspot
        JLabel trash = createClickableHotspotLabel(673, 450, 70, 70);
        panel.add(trash);
        applyHoverEffectToLabel(trash);
        trash.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!trash.isEnabled()) return;
                showInteractionMenu("TRASH BIN",
                        null,
                        () -> startDialogue(new String[]{
                                "The bin is mostly empty, except for a scrap of paper.",
                                "It's a destroyed note. Only a few words are legible: '...don't look back...'",
                                "[CLUE FOUND: Destroyed Note]"
                        }, () -> GameState.getInstance().addClue("Destroyed Note"))
                );
            }
        });

        // CCTV Monitor Hotspot
        JLabel monitor = createClickableHotspotLabel(600, 130, 150, 80);
        panel.add(monitor);
        applyHoverEffectToLabel(monitor);
        monitor.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!monitor.isEnabled()) return;
                
                if (GameState.getInstance().hasClue("CCTV Footage Anomaly")) {
                    startDialogue(new String[]{
                            "The system is restored and I can now open the footage.",
                            "However, there is still a five-minute block missing from the timestamp.",
                            "The anomaly is definitely intentional."
                    });
                } else {
                    showInteractionMenu("CCTV MONITOR",
                            null,
                            () -> startDialogue(new String[]{
                                    "The monitor flickers with static. It's unreadable.",
                                    "The system is reporting a timestamp synchronization error.",
                                    "I need to fix it by aligning the timestamps from largest to smallest.",
                                    "[PUZZLE: REPAIR CCTV SYSTEM]"
                            }, () -> {
                                MainGame.getInstance().openCctvPuzzle("CCTV Footage Anomaly", "FLOOR1");
                            })
                    );
                }
            }
        });

        // Navigation back to Hallway
        JButton toHallway = createArrowButton(Assets.img("rightArrow.png"), 820, 250, 60);
        panel.add(toHallway);
        toHallway.addActionListener(e -> areaLayout.show(areaContainer, "PART1A"));

        return panel;
    }
}
