import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    DialogueUI dialogueUI;

    public LobbyPanel() {

        setLayout(null);

        // Dialogue UI (Reusable and floating)
        dialogueUI = new DialogueUI();

        areaLayout = new CardLayout();
        areaContainer = new JPanel(areaLayout);

        JPanel lobbyPartA = createLobbyPartA();
        JPanel lobbyPartB = createLobbyPartB();
        JPanel lobbyPartC = createLobbyPartC();
        JPanel lobbyPartD = createLobbyPartD();

        areaContainer.add(lobbyPartA, "A");
        areaContainer.add(lobbyPartB, "B");
        areaContainer.add(lobbyPartC, "C");
        areaContainer.add(lobbyPartD, "D");
        areaContainer.setBounds(0, 0, 900, 600); // Back to full height

        // Add components: dialogueUI is on top (index 0)
        add(dialogueUI, 0);
        add(areaContainer, 1);
    }

    private void startDialogue(String[] dialogue) {
        dialogueUI.startDialogue(dialogue);
    }

    private void showInteractionMenu(String title, Runnable talkAction, Runnable examineAction) {
        dialogueUI.showInteractionMenu(title, talkAction, examineAction);
    }

    private void typeText(String text) {
        dialogueUI.typeText(text);
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon original = new ImageIcon(path);
        Image img = original.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private JPanel createLobbyPartA() {

        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyA.jpg");

        // Mrs. Vale (Front Desk Clerk)
        ImageIcon npcIcon = resizeIcon("src/images/msAngela.png", 200, 300);
        JLabel mrsVale = new JLabel(npcIcon);
        mrsVale.setBounds(350, 120, 200, 300);
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
                            startDialogue(dialogue);
                            
                            Timer puzzleTimer = new Timer(3000, e -> {
                                MainGame.getInstance().openPuzzle("Dr. Kells photo", "LOBBY");
                            });
                            puzzleTimer.setRepeats(false);
                            puzzleTimer.start();
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

        JButton rightArrow = new JButton(">");
        rightArrow.setBounds(820, 250, 50, 50);
        panel.add(rightArrow);

        JButton downArrow = new JButton("v");
        downArrow.setBounds(425, 520, 50, 50);
        panel.add(downArrow);

        JButton upArrow = new JButton("^");
        upArrow.setBounds(425, 20, 50, 50);
        panel.add(upArrow);

        // Mirror Interaction
        JLabel mirror = new JLabel();
        mirror.setBounds(100, 100, 100, 200);
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
        BackgroundPanel panel = new BackgroundPanel("src/images/partc-background.jpg");

        // Broken Clock
        ImageIcon clockIcon = resizeIcon("src/images/clock.png", 100, 100);
        JLabel clock = new JLabel(clockIcon);
        clock.setBounds(200, 50, 100, 100);
        panel.add(clock);
        clock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clock.setCursor(new Cursor(Cursor.HAND_CURSOR));
                clock.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clock.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                clock.setBorder(null);
            }
        });

        // Forgotten Suitcase
        ImageIcon suitcaseIcon = resizeIcon("src/images/suitcase.png", 150, 100);
        JLabel suitcase = new JLabel(suitcaseIcon);
        suitcase.setBounds(600, 400, 150, 100);
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

        // Portrait of Mr. Blackwood
        ImageIcon portraitIcon = resizeIcon("src/images/portrait.png", 100, 150);
        JLabel portrait = new JLabel(portraitIcon);
        portrait.setBounds(400, 100, 100, 150);
        panel.add(portrait);
        portrait.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showInteractionMenu("PORTRAIT", null, () -> {
                    String[] dialogue = {
                        "A portrait of the hotel's owner, Mr. Blackwood.",
                        "His eyes seem to follow you across the room.",
                        "The plaque underneath reads: 'THE TRUTH IS HIDDEN BENEATH THE SURFACE.'",
                        "You notice a faint scratch on the frame."
                    };
                    startDialogue(dialogue);
                    GameState.getInstance().addClue("Owner Portrait hint");
                });
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                portrait.setCursor(new Cursor(Cursor.HAND_CURSOR));
                portrait.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                portrait.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                portrait.setBorder(null);
            }
        });

        JButton upArrow = new JButton("^");
        upArrow.setBounds(425, 20, 50, 50);
        panel.add(upArrow);
        upArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "A");
        });

        return panel;
    }

    private JPanel createLobbyPartD() {
        BackgroundPanel panel = new BackgroundPanel("src/images/partd-background.jpg");

        // Guest Register
        ImageIcon registerIcon = resizeIcon("src/images/guest-register.png", 200, 100);
        JLabel register = new JLabel(registerIcon);
        register.setBounds(350, 300, 200, 100);
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

        JButton downArrow = new JButton("v");
        downArrow.setBounds(425, 520, 50, 50);
        panel.add(downArrow);
        downArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "A");
        });

        return panel;
    }

    private JPanel createLobbyPartB() {

        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyB.jpg");

        // Liam (Bellboy)
        ImageIcon npcIcon = resizeIcon("src/images/gusion.png", 200, 300);
        JLabel liam = new JLabel(npcIcon);
        liam.setBounds(350, 120, 200, 300);
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
        elevatorButton.setBounds(650, 250, 100, 50);
        panel.add(elevatorButton);
        elevatorButton.addActionListener(e -> {
             if (GameState.getInstance().lobbyComplete) {
                 MainGame.getInstance().switchFloor("FLOOR1");
             } else {
                 typeText("The elevator is locked. I should talk to everyone and gather clues first.");
             }
         });

        JButton leftArrow = new JButton("<");
        leftArrow.setBounds(30, 250, 50, 50);
        panel.add(leftArrow);

        leftArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "A");
        });

        return panel;
    }
}