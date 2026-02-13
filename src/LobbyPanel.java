import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    JTextArea dialogueBox;

    public LobbyPanel() {

        setLayout(null);

        areaLayout = new CardLayout();
        areaContainer = new JPanel(areaLayout);

        JPanel lobbyPartA = createLobbyPartA();
        JPanel lobbyPartB = createLobbyPartB();

        areaContainer.add(lobbyPartA, "A");
        areaContainer.add(lobbyPartB, "B");
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

        add(areaContainer);
        add(dialoguePanel);
    }

    private JPanel createLobbyPartA() {

        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyA.jpg");

        ImageIcon npcIcon = new ImageIcon("src/images/msAngela.png");
        JLabel npcLabel = new JLabel(npcIcon);
        npcLabel.setBounds(350, 120, 200, 300);
        panel.add(npcLabel);

        npcLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dialogueBox.setText("Mrs. Angela: Welcome detective. We've been expecting you.");
            }
        });

        JButton rightArrow = new JButton(">");
        rightArrow.setBounds(820, 250, 50, 50);
        panel.add(rightArrow);

        rightArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "B");
            dialogueBox.setText("You walk toward the lounge area...");
        });

        return panel;
    }

    private JPanel createLobbyPartB() {

        BackgroundPanel panel = new BackgroundPanel("src/images/lobbyB.jpg");

        ImageIcon npcIcon = new ImageIcon("src/images/gusion.png");
        JLabel npcLabel = new JLabel(npcIcon);
        npcLabel.setBounds(350, 120, 200, 300);
        panel.add(npcLabel);

        npcLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dialogueBox.setText("Gusion: I saw someone near Room 217 last night...");
            }
        });

        JButton leftArrow = new JButton("<");
        leftArrow.setBounds(30, 250, 50, 50);
        panel.add(leftArrow);

        leftArrow.addActionListener(e -> {
            areaLayout.show(areaContainer, "A");
            dialogueBox.setText("You return to the reception area.");
        });

        return panel;
    }
}