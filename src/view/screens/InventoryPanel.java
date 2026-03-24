package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InventoryPanel extends JPanel {

    private final DefaultListModel<String> listModel;
    private final JList<String> list;
    private String returnTo;

    public InventoryPanel() {
        setLayout(null);
        setBackground(new Color(15, 16, 20));

        JLabel title = new JLabel("Inventory / Clues");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, UiScale.font(28)));
        title.setBounds(UiScale.x(40), UiScale.y(30), UiScale.w(600), UiScale.h(40));
        add(title);

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setFont(new Font("Serif", Font.PLAIN, UiScale.font(18)));
        list.setBackground(new Color(25, 28, 40));
        list.setForeground(Color.WHITE);
        list.setSelectionBackground(new Color(65, 75, 95));
        list.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBounds(UiScale.x(40), UiScale.y(90), UiScale.w(820), UiScale.h(520));
        add(scrollPane);

        JButton back = new JButton("Back");
        back.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        back.setBounds(UiScale.GAME_WIDTH - UiScale.x(40) - UiScale.s(160), UiScale.y(30), UiScale.s(160), UiScale.s(45));
        back.addActionListener(e -> MainGame.getInstance().closeInventory(returnTo));
        add(back);

        GameState.getInstance().addListener(this::refresh);
    }

    public void open(String returnTo) {
        this.returnTo = returnTo;
        refresh();
    }

    public void refresh() {
        listModel.clear();
        ArrayList<String> clues = GameState.getInstance().clues;
        if (clues.isEmpty()) {
            listModel.addElement("No clues collected yet.");
        } else {
            for (String clue : clues) {
                listModel.addElement(clue);
            }
        }
    }
}

