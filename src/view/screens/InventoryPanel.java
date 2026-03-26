package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryPanel extends JPanel {

    private final DefaultListModel<String> listModel;
    private final JList<String> clueList;
    private final JTextArea clueDetailsArea;
    private String returnTo;

    // Temporary clue descriptions while clues are still stored as String
    private final Map<String, String> clueDescriptions = new HashMap<>();

    public InventoryPanel() {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180));

        seedClueDescriptions();

        int panelW = UiScale.w(900);
        int panelH = UiScale.h(520);
        int panelX = (UiScale.GAME_WIDTH - panelW) / 2;
        int panelY = (UiScale.GAME_HEIGHT - panelH) / 2;

        JPanel bookPanel = new JPanel(null);
        bookPanel.setBounds(panelX, panelY, panelW, panelH);
        bookPanel.setBackground(new Color(20, 18, 16));
        bookPanel.setBorder(new LineBorder(new Color(120, 105, 80), 3));
        add(bookPanel);

        JLabel title = new JLabel("Inventory / Clues");
        title.setForeground(new Color(245, 235, 220));
        title.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        title.setBounds(UiScale.x(25), UiScale.y(20), UiScale.w(300), UiScale.h(35));
        bookPanel.add(title);

        JLabel subtitle = new JLabel("Collected evidence and observations");
        subtitle.setForeground(new Color(180, 170, 155));
        subtitle.setFont(new Font("Serif", Font.ITALIC, UiScale.font(12)));
        subtitle.setBounds(UiScale.x(28), UiScale.y(50), UiScale.w(300), UiScale.h(20));
        bookPanel.add(subtitle);

        JButton back = new JButton("Close");
        back.setFont(new Font("Serif", Font.BOLD, UiScale.font(15)));
        back.setFocusPainted(false);
        back.setBackground(new Color(200, 190, 170));
        back.setForeground(new Color(30, 25, 20));
        back.setBounds(panelW - UiScale.w(140), UiScale.y(18), UiScale.w(105), UiScale.h(35));
        back.addActionListener(e -> MainGame.getInstance().closeInventory(returnTo));
        bookPanel.add(back);

        JPanel leftPanel = new JPanel(null);
        leftPanel.setBounds(UiScale.x(25), UiScale.y(85), UiScale.w(260), UiScale.h(400));
        leftPanel.setBackground(new Color(32, 28, 24));
        leftPanel.setBorder(new LineBorder(new Color(90, 80, 65), 2));
        bookPanel.add(leftPanel);

        JLabel cluesLabel = new JLabel("Clues");
        cluesLabel.setForeground(new Color(235, 225, 205));
        cluesLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        cluesLabel.setBounds(UiScale.x(15), UiScale.y(10), UiScale.w(100), UiScale.h(25));
        leftPanel.add(cluesLabel);

        listModel = new DefaultListModel<>();
        clueList = new JList<>(listModel);
        clueList.setFont(new Font("Serif", Font.PLAIN, UiScale.font(16)));
        clueList.setBackground(new Color(40, 34, 30));
        clueList.setForeground(new Color(245, 235, 220));
        clueList.setSelectionBackground(new Color(90, 76, 58));
        clueList.setSelectionForeground(Color.WHITE);
        clueList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        clueList.setFixedCellHeight(UiScale.h(32));

        JScrollPane listScroll = new JScrollPane(clueList);
        listScroll.setBounds(UiScale.x(10), UiScale.y(45), UiScale.w(240), UiScale.h(340));
        listScroll.setBorder(new LineBorder(new Color(90, 80, 65), 1));
        leftPanel.add(listScroll);

        JPanel rightPanel = new JPanel(null);
        rightPanel.setBounds(UiScale.x(305), UiScale.y(85), UiScale.w(570), UiScale.h(400));
        rightPanel.setBackground(new Color(28, 24, 22));
        rightPanel.setBorder(new LineBorder(new Color(90, 80, 65), 2));
        bookPanel.add(rightPanel);

        JLabel detailsLabel = new JLabel("Details");
        detailsLabel.setForeground(new Color(235, 225, 205));
        detailsLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        detailsLabel.setBounds(UiScale.x(15), UiScale.y(10), UiScale.w(100), UiScale.h(25));
        rightPanel.add(detailsLabel);

        clueDetailsArea = new JTextArea();
        clueDetailsArea.setEditable(false);
        clueDetailsArea.setLineWrap(true);
        clueDetailsArea.setWrapStyleWord(true);
        clueDetailsArea.setFont(new Font("Serif", Font.PLAIN, UiScale.font(16)));
        clueDetailsArea.setForeground(new Color(240, 232, 220));
        clueDetailsArea.setBackground(new Color(36, 31, 28));
        clueDetailsArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        clueDetailsArea.setText("Select a clue to inspect it.");

        JScrollPane detailsScroll = new JScrollPane(clueDetailsArea);
        detailsScroll.setBounds(UiScale.x(10), UiScale.y(45), UiScale.w(550), UiScale.h(340));
        detailsScroll.setBorder(new LineBorder(new Color(90, 80, 65), 1));
        rightPanel.add(detailsScroll);

        clueList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = clueList.getSelectedValue();
                if (selected != null) {
                    clueDetailsArea.setText(getClueDescription(selected));
                }
            }
        });

        GameState.getInstance().addListener(this::refresh);
    }

    public void open(String returnTo) {
        this.returnTo = returnTo;
        refresh();

        if (!listModel.isEmpty() && !listModel.get(0).equals("No clues collected yet.")) {
            clueList.setSelectedIndex(0);
        } else {
            clueDetailsArea.setText("You have not collected any clues yet.");
        }
    }

    public void refresh() {
        listModel.clear();
        ArrayList<String> clues = GameState.getInstance().clues;

        if (clues.isEmpty()) {
            listModel.addElement("No clues collected yet.");
            clueDetailsArea.setText("You have not collected any clues yet.");
        } else {
            for (String clue : clues) {
                listModel.addElement("• " + clue);
            }
        }
    }

    private String getClueDescription(String selectedValue) {
        String clean = selectedValue.replaceFirst("^•\\s*", "");
        return clueDescriptions.getOrDefault(
                clean,
                clean + "\n\nA clue collected during the investigation. Its meaning is still unclear."
        );
    }

    private void seedClueDescriptions() {
        clueDescriptions.put("Guest Register entry",
                "Guest Register Entry\n\n" +
                "The front desk register contains today's hotel entries.\n\n" +
                "- Dr. Kells' name is crossed out in red ink.\n" +
                "- Someone wrote the word 'LIAR' beside it.\n" +
                "- Another entry appears under the name 'E. Vane' in Room 305.\n\n" +
                "This may suggest someone tampered with the hotel's official records.");

        clueDescriptions.put("Mirror reflection hint",
                "Mirror Reflection Hint\n\n" +
                "A strange message appeared on the mirror: 'YOU ARE NOT ALONE.'\n\n" +
                "The reflection also seemed delayed, as if something in the room was out of sync.\n\n" +
                "This could be psychological... or something worse.");

        clueDescriptions.put("Frozen Clock hint",
                "Frozen Clock Hint\n\n" +
                "The clock is stuck at 11:45, but its second hand appears to move backwards.\n\n" +
                "Time distortion or symbolic clue?\n\n" +
                "It may connect to the moment of the incident.");

        clueDescriptions.put("Blackwood Suitcase hint",
                "Blackwood Suitcase Hint\n\n" +
                "An old suitcase tagged 'R. Blackwood' was found in the lobby lounge.\n\n" +
                "A strange symbol was carved into the leather.\n" +
                "The same symbol appeared elsewhere in the hotel.\n\n" +
                "The owner may be tied to the hotel mystery.");

        clueDescriptions.put("Mysterious note",
                "Mysterious Note\n\n" +
                "Liam handed over a note dropped near Room 217.\n\n" +
                "He said the person who dropped it looked familiar.\n\n" +
                "This clue may connect directly to the detective's investigation.");
    }
}