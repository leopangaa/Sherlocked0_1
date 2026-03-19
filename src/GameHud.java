import javax.swing.*;
import java.awt.*;

public class GameHud extends JPanel {

    private final JButton inventoryButton;
    private final JPanel objectivesPanel;
    private final JTextArea objectivesBox;

    public GameHud() {
        setLayout(null);
        setOpaque(false);
        setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        ImageIcon invIcon = new ImageIcon("src/images/inventoryIcon.png");
        Image invImg = invIcon.getImage().getScaledInstance(UiScale.s(52), UiScale.s(52), Image.SCALE_SMOOTH);
        inventoryButton = new JButton(new ImageIcon(invImg));
        inventoryButton.setBounds(UiScale.x(20), UiScale.y(20), UiScale.s(52), UiScale.s(52));
        inventoryButton.setBorderPainted(false);
        inventoryButton.setContentAreaFilled(false);
        inventoryButton.setFocusPainted(false);
        inventoryButton.setOpaque(false);
        inventoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inventoryButton.addActionListener(e -> MainGame.getInstance().openInventory());
        add(inventoryButton);

        int panelW = UiScale.s(360);
        int panelH = UiScale.s(240);
        int panelX = UiScale.GAME_WIDTH - UiScale.x(20) - panelW;
        int panelY = UiScale.y(10);

        ImageIcon objectivesIcon = new ImageIcon("src/images/objectives.png");
        Image objectivesImg = objectivesIcon.getImage().getScaledInstance(panelW, panelH, Image.SCALE_SMOOTH);
        JLabel objectivesBg = new JLabel(new ImageIcon(objectivesImg));
        objectivesBg.setBounds(0, 0, panelW, panelH);

        objectivesPanel = new JPanel(null);
        objectivesPanel.setOpaque(false);
        objectivesPanel.setBounds(panelX, panelY, panelW, panelH);
        objectivesPanel.add(objectivesBg);
        add(objectivesPanel);

        objectivesBox = new JTextArea();
        objectivesBox.setEditable(false);
        objectivesBox.setLineWrap(true);
        objectivesBox.setWrapStyleWord(true);
        objectivesBox.setFont(new Font("Serif", Font.PLAIN, UiScale.font(14)));
        objectivesBox.setBackground(new Color(0, 0, 0, 0));
        objectivesBox.setOpaque(false);
        objectivesBox.setForeground(new Color(30, 20, 10));
        objectivesBox.setBorder(null);
        objectivesBox.setMargin(new Insets(UiScale.s(2), UiScale.s(6), UiScale.s(2), UiScale.s(2)));

        int insetX = UiScale.s(68);
        int insetY = UiScale.s(84);
        int boxW = panelW - UiScale.s(136);
        int boxH = panelH - UiScale.s(130);
        objectivesBox.setBounds(insetX, insetY, boxW, boxH);
        objectivesPanel.add(objectivesBox);
        objectivesPanel.setComponentZOrder(objectivesBox, 0);

        GameState.getInstance().addListener(this::refresh);
        refresh();
    }

    public void setHudEnabled(boolean enabled) {
        inventoryButton.setEnabled(enabled);
    }

    public void setObjectivesVisible(boolean visible) {
        objectivesPanel.setVisible(visible);
    }

    public void refresh() {
        GameState gs = GameState.getInstance();
        String text;
        if (gs.currentFloor == 0) {
            boolean a = gs.hasClue("Dr. Kells photo");
            boolean b = gs.hasClue("Mysterious note");
            boolean c = gs.hasClue("Guest Register entry");
            boolean d = gs.hasClue("Mirror reflection hint");
            text = "Lobby\n" +
                checkbox(a) + " Obtain Dr. Kells photo\n" +
                checkbox(b) + " Obtain Mysterious note\n" +
                checkbox(c) + " Read guest register\n" +
                checkbox(d) + " Examine mirror\n\n" +
                (gs.lobbyComplete ? "Next: Use elevator to Floor 1" : "Next: Gather all clues");
        } else if (gs.currentFloor == 1) {
            text = "Floor 1\n" +
                checkbox(gs.floor1Complete) + " Investigate the guest rooms\n\n" +
                (gs.floor1Complete ? "Next: Return to elevator" : "Next: Explore Room 217");
        } else {
            text = "Objectives\n" +
                "Progress your investigation.";
        }
        objectivesBox.setText(text);
        objectivesBox.setCaretPosition(0);
    }

    private String checkbox(boolean done) {
        return done ? "[x]" : "[ ]";
    }
}
