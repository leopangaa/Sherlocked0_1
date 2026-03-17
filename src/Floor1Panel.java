import javax.swing.*;
import java.awt.*;

public class Floor1Panel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    JTextArea dialogueBox;
    Timer typewriterTimer;
    String currentFullText = "";
    int charIndex = 0;
    
    String[] currentDialogueQueue;
    int dialogueQueueIndex = 0;

    public Floor1Panel() {

        setLayout(null);

        areaLayout = new CardLayout();
        areaContainer = new JPanel(areaLayout);

        JPanel hallway = createHallway();
        JPanel room217 = createRoom217();

        areaContainer.add(hallway, "HALLWAY");
        areaContainer.add(room217, "ROOM217");
        areaContainer.setBounds(0, 0, 900, 600);

        dialogueBox = new JTextArea(4, 20);
        dialogueBox.setEditable(false);
        dialogueBox.setLineWrap(true);
        dialogueBox.setWrapStyleWord(true);
        dialogueBox.setFont(new Font("Serif", Font.PLAIN, 16));

        JPanel dialoguePanel = new JPanel();
        dialoguePanel.setLayout(new BorderLayout());
        dialoguePanel.setBounds(100, 450, 700, 120);
        dialoguePanel.setBackground(new Color(0, 0, 0, 180));
        dialoguePanel.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(dialogueBox);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        dialogueBox.setOpaque(false);
        dialogueBox.setForeground(Color.WHITE);

        dialoguePanel.add(scrollPane, BorderLayout.CENTER);

        dialoguePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (typewriterTimer != null && typewriterTimer.isRunning()) {
                    typewriterTimer.stop();
                    dialogueBox.setText(currentFullText);
                } else {
                    showNextDialogue();
                }
            }
        });

        add(areaContainer);
        add(dialoguePanel);
        
        typeText("Floor 1: Guest Rooms. The air feels heavy here.");
    }

    private void typeText(String text) {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
            dialogueBox.setText(currentFullText);
            return;
        }

        currentFullText = text;
        charIndex = 0;
        dialogueBox.setText("");

        typewriterTimer = new Timer(30, e -> {
            if (charIndex < currentFullText.length()) {
                dialogueBox.append(String.valueOf(currentFullText.charAt(charIndex)));
                charIndex++;
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        typewriterTimer.start();
    }

    private void startDialogue(String[] dialogue) {
        currentDialogueQueue = dialogue;
        dialogueQueueIndex = 0;
        showNextDialogue();
    }

    private void showNextDialogue() {
        if (currentDialogueQueue != null && dialogueQueueIndex < currentDialogueQueue.length) {
            typeText(currentDialogueQueue[dialogueQueueIndex]);
            dialogueQueueIndex++;
        }
    }

    private JPanel createHallway() {
        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyA.jpg"); // Placeholder

        // Ms. Harper
        ImageIcon npcIcon = new ImageIcon("src/images/msAngela.png"); // Placeholder
        JLabel msHarper = new JLabel(npcIcon);
        msHarper.setBounds(200, 120, 200, 300);
        panel.add(msHarper);

        msHarper.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startDialogue(new String[]{
                    "Ms. Harper: I saw a masked figure running toward the stairs last night.",
                    "Ms. Harper: They looked... frantic."
                });
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                msHarper.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        JButton enterRoom = new JButton("Enter Room 217");
        enterRoom.setBounds(650, 250, 150, 50);
        panel.add(enterRoom);
        enterRoom.addActionListener(e -> areaLayout.show(areaContainer, "ROOM217"));

        return panel;
    }

    private JPanel createRoom217() {
        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyB.jpg"); // Placeholder

        // Mr. Doyle
        ImageIcon npcIcon = new ImageIcon("src/images/gusion.png"); // Placeholder
        JLabel mrDoyle = new JLabel(npcIcon);
        mrDoyle.setBounds(400, 120, 200, 300);
        panel.add(mrDoyle);

        mrDoyle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startDialogue(new String[]{
                    "Mr. Doyle: Dr. Kells? I thought he was alone in there.",
                    "Mr. Doyle: I didn't hear any shouting."
                });
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mrDoyle.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        JButton backButton = new JButton("Back to Hallway");
        backButton.setBounds(30, 250, 150, 50);
        panel.add(backButton);
        backButton.addActionListener(e -> areaLayout.show(areaContainer, "HALLWAY"));

        return panel;
    }
}
