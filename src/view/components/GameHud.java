package view.components;

import core.GameState;
import main.MainGame;
import utils.Assets;
import utils.UiScale;

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

        ImageIcon invIcon = new ImageIcon(Assets.img("inventoryIcon.png"));
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

        int panelW = UiScale.s(280);
        int panelH = UiScale.s(190);
        int panelX = UiScale.GAME_WIDTH - UiScale.x(20) - panelW;
        int panelY = UiScale.y(10);

        ImageIcon objectivesIcon = new ImageIcon(Assets.img("objectives.png"));
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
        objectivesBox.setFont(new Font("Serif", Font.PLAIN, UiScale.font(12)));
        objectivesBox.setBackground(new Color(0, 0, 0, 0));
        objectivesBox.setOpaque(false);
        objectivesBox.setForeground(new Color(30, 20, 10));
        objectivesBox.setBorder(null);
        objectivesBox.setMargin(new Insets(UiScale.s(1), UiScale.s(4), UiScale.s(1), UiScale.s(1)));

        int insetX = (int) Math.round(panelW * 0.19);
        int insetY = (int) Math.round(panelH * 0.35);
        int boxW = panelW - (int) Math.round(panelW * 0.38);
        int boxH = panelH - (int) Math.round(panelH * 0.54);
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
        String text = "";
        
        // Use a consistent header format
        if (gs.currentFloor == 0) {
            boolean a = gs.hasClue("Dr. Kells photo");
            boolean b = gs.hasClue("Mysterious note");
            boolean c = gs.hasClue("Guest Register entry");
            boolean d = gs.hasClue("Mirror reflection hint");
            text = "Lobby Objectives\n" +
                checkbox(a) + " Obtain Dr. Kells photo\n" +
                checkbox(b) + " Obtain Mysterious note\n" +
                checkbox(c) + " Read guest register\n" +
                checkbox(d) + " Examine mirror\n\n" +
                (gs.lobbyComplete ? "Next: Use elevator to Floor 1" : "Next: Gather all clues");
        } else if (gs.currentFloor == 1) {
            boolean harper = gs.hasClue("Harper Testimony");
            boolean doyle = gs.hasClue("Doyle Statement");
            boolean struggle = gs.hasClue("Signs of Struggle");
            boolean anomaly = gs.hasClue("CCTV Footage Anomaly");

            String objective;
            if (!harper || !doyle) {
                objective = "Talk to the guests and investigate Room 217";
            } else if (!struggle || !anomaly) {
                objective = "Investigate Room 217 for inconsistencies";
            } else {
                objective = "Find out what really happened";
            }

            text = "Floor 1 Objectives\n" +
                checkbox(harper) + " Harper Testimony\n" +
                checkbox(doyle) + " Doyle Statement\n" +
                checkbox(struggle) + " Signs of Struggle\n" +
                checkbox(anomaly) + " CCTV Anomaly\n\n" +
                "Next: " + objective;
        } else if (gs.currentFloor == 2) {
            boolean rina = gs.hasClue("Rina Testimony");
            boolean jared = gs.hasClue("Jared Statement");
            boolean journal = gs.hasClue("Hidden Journal");
            boolean anomaly = gs.hasClue("CCTV Footage Anomaly");
            boolean record = gs.hasClue("Patient Record");

            String objective;
            if (!rina || !jared) {
                objective = "Talk to the staff in the hallway";
            } else if (!journal || !anomaly || !record) {
                objective = "Search the staff quarters and security room";
            } else {
                objective = "Understand your role in this mystery";
            }

            text = "Floor 2 Objectives\n" +
                checkbox(rina) + " Rina Testimony\n" +
                checkbox(jared) + " Jared Statement\n" +
                checkbox(journal) + " Hidden Journal\n" +
                checkbox(anomaly) + " CCTV Anomaly\n" +
                checkbox(record) + " Patient Record\n\n" +
                "Next: " + objective;
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
