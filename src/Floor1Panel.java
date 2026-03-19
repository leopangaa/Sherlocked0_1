import javax.swing.*;
import java.awt.*;

public class Floor1Panel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    DialogueUI dialogueUI;
    JPanel inputBlocker;

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
        inputBlocker.addMouseListener(new java.awt.event.MouseAdapter() {});

        dialogueUI.setVisibilityListener(visible -> {
            inputBlocker.setVisible(visible);
            setButtonsEnabled(areaContainer, !visible);
            MainGame.getInstance().setHudEnabled(!visible);
            areaContainer.revalidate();
            areaContainer.repaint();
        });

        JPanel hallway = createHallway();
        JPanel room217 = createRoom217();

        areaContainer.add(hallway, "HALLWAY");
        areaContainer.add(room217, "ROOM217");
        areaContainer.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

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

    private void typeText(String text) {
        dialogueUI.typeText(text);
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon original = new ImageIcon(path);
        Image img = original.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private JPanel createHallway() {
        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyA.jpg"); // Placeholder

        // Ms. Harper
        ImageIcon npcIcon = resizeIcon("src/images/msAngela.png", UiScale.s(200), UiScale.s(300)); // Placeholder
        JLabel msHarper = new JLabel(npcIcon);
        msHarper.setBounds(UiScale.x(200), UiScale.y(120), UiScale.s(200), UiScale.s(300));
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
        enterRoom.setBounds(UiScale.x(650), UiScale.y(250), UiScale.s(150), UiScale.s(50));
        panel.add(enterRoom);
        enterRoom.addActionListener(e -> areaLayout.show(areaContainer, "ROOM217"));

        return panel;
    }

    private JPanel createRoom217() {
        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyB.jpg"); // Placeholder

        // Mr. Doyle
        ImageIcon npcIcon = resizeIcon("src/images/gusion.png", UiScale.s(200), UiScale.s(300)); // Placeholder
        JLabel mrDoyle = new JLabel(npcIcon);
        mrDoyle.setBounds(UiScale.x(400), UiScale.y(120), UiScale.s(200), UiScale.s(300));
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
        backButton.setBounds(UiScale.x(30), UiScale.y(250), UiScale.s(150), UiScale.s(50));
        panel.add(backButton);
        backButton.addActionListener(e -> areaLayout.show(areaContainer, "HALLWAY"));

        return panel;
    }
}
