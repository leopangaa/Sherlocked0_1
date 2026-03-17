import javax.swing.*;
import java.awt.*;

public class DialogueUI extends JPanel {

    private JTextArea dialogueBox;
    private JButton talkBtn, examineBtn;
    private Timer typewriterTimer;
    private String currentFullText = "";
    private int charIndex = 0;
    private String[] currentDialogueQueue;
    private int dialogueQueueIndex = 0;

    public DialogueUI() {
        setLayout(null);
        setBounds(50, 420, 800, 140); // Floating box position
        setBackground(new Color(20, 24, 35, 235)); // Mystery deep blue-grey
        setBorder(BorderFactory.createLineBorder(new Color(100, 110, 130), 2));
        setVisible(false);

        // Text Area
        dialogueBox = new JTextArea();
        dialogueBox.setEditable(false);
        dialogueBox.setLineWrap(true);
        dialogueBox.setWrapStyleWord(true);
        dialogueBox.setFont(new Font("Serif", Font.PLAIN, 18));
        dialogueBox.setBackground(new Color(0, 0, 0, 0));
        dialogueBox.setForeground(Color.WHITE);
        dialogueBox.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(dialogueBox);
        scrollPane.setBounds(20, 15, 500, 110);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane);

        // Buttons
        talkBtn = createMenuButton("TALK", 20);
        examineBtn = createMenuButton("EXAMINE", 75);
        
        talkBtn.setVisible(false);
        examineBtn.setVisible(false);

        add(talkBtn);
        add(examineBtn);

        // Click to advance dialogue
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (typewriterTimer != null && typewriterTimer.isRunning()) {
                    typewriterTimer.stop();
                    dialogueBox.setText(currentFullText);
                } else {
                    showNextDialogue();
                }
            }
        });
    }

    private JButton createMenuButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(540, y, 230, 45);
        btn.setBackground(new Color(45, 55, 75));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Serif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 110, 130)));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(65, 75, 95));
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 55, 75));
                btn.setBorder(BorderFactory.createLineBorder(new Color(100, 110, 130)));
            }
        });
        return btn;
    }

    public void startDialogue(String[] dialogue) {
        setVisible(true);
        currentDialogueQueue = dialogue;
        dialogueQueueIndex = 0;
        showNextDialogue();
    }

    private void showNextDialogue() {
        if (currentDialogueQueue != null && dialogueQueueIndex < currentDialogueQueue.length) {
            typeText(currentDialogueQueue[dialogueQueueIndex]);
            dialogueQueueIndex++;
        } else {
            setVisible(false);
            currentDialogueQueue = null;
        }
    }

    public void typeText(String text) {
        if (!isVisible()) setVisible(true);
        
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
        }

        currentFullText = text;
        charIndex = 0;
        dialogueBox.setText("");

        typewriterTimer = new Timer(25, e -> {
            if (charIndex < currentFullText.length()) {
                dialogueBox.append(String.valueOf(currentFullText.charAt(charIndex)));
                charIndex++;
            } else {
                ((Timer) e.getSource()).stop();
                // If it's a single line (not a queue), start a hide timer
                if (currentDialogueQueue == null) {
                    Timer hideTimer = new Timer(3000, h -> setVisible(false));
                    hideTimer.setRepeats(false);
                    hideTimer.start();
                }
            }
        });
        typewriterTimer.start();
    }

    public void showInteractionMenu(String title, Runnable talkAction, Runnable examineAction) {
        setVisible(true);
        typeText("Interacting with: " + title);
        
        // Reset listeners
        for (java.awt.event.ActionListener al : talkBtn.getActionListeners()) talkBtn.removeActionListener(al);
        for (java.awt.event.ActionListener al : examineBtn.getActionListeners()) examineBtn.removeActionListener(al);

        if (talkAction != null) {
            talkBtn.setVisible(true);
            talkBtn.addActionListener(e -> {
                talkBtn.setVisible(false);
                examineBtn.setVisible(false);
                talkAction.run();
            });
        } else {
            talkBtn.setVisible(false);
        }

        if (examineAction != null) {
            examineBtn.setVisible(true);
            examineBtn.addActionListener(e -> {
                talkBtn.setVisible(false);
                examineBtn.setVisible(false);
                examineAction.run();
            });
        } else {
            examineBtn.setVisible(false);
        }
    }
}
