package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CluePuzzlePanel extends JPanel {
    private String clueToAward;
    private String returnFloor;
    private ArrayList<Integer> numbers;
    private JPanel numbersPanel;
    private JLabel instructionLabel;
    private boolean solved;

    public CluePuzzlePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 45));

        instructionLabel = new JLabel("Sort the numbers in ascending order (Smallest to Largest)", SwingConstants.CENTER);
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(20)));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(UiScale.s(20), UiScale.s(10), UiScale.s(20), UiScale.s(10)));
        add(instructionLabel, BorderLayout.NORTH);

        numbersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(20), UiScale.s(50)));
        numbersPanel.setOpaque(false);
        add(numbersPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        backButton.addActionListener(e -> {
            if (returnFloor != null) {
                MainGame.getInstance().switchFloor(returnFloor);
            } else {
                MainGame.getInstance().switchFloor("LOBBY");
            }
        });
        southPanel.add(backButton);

        add(southPanel, BorderLayout.SOUTH);
    }

    public void startPuzzle(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        instructionLabel.setText("Sort the numbers in ascending order (Smallest to Largest)");
        
        // Generate random numbers
        numbers = new ArrayList<>();
        while (numbers.size() < 5) {
            int v = (int) (Math.random() * 100);
            if (!numbers.contains(v)) numbers.add(v);
        }

        int guard = 0;
        while (isSorted() && guard < 25) {
            Collections.shuffle(numbers);
            guard++;
        }
        
        refreshUI();
    }

    private void refreshUI() {
        numbersPanel.removeAll();
        for (int i = 0; i < numbers.size(); i++) {
            final int index = i;
            JButton numBtn = new JButton(String.valueOf(numbers.get(i)));
            numBtn.setPreferredSize(new Dimension(UiScale.s(80), UiScale.s(80)));
            numBtn.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(24)));
            
            // Simple swap logic for the dummy game
            numBtn.addActionListener(e -> {
                if (solved) return;
                if (index < numbers.size() - 1) {
                    Collections.swap(numbers, index, index + 1);
                    refreshUI();
                } else {
                    Collections.swap(numbers, index, 0);
                    refreshUI();
                }
            });
            
            numbersPanel.add(numBtn);
        }
        numbersPanel.revalidate();
        numbersPanel.repaint();

        if (!solved && isSorted()) {
            onSolved();
        }
    }

    private boolean isSorted() {
        for (int i = 0; i < numbers.size() - 1; i++) {
            if (numbers.get(i) > numbers.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    private void onSolved() {
        solved = true;
        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }
        instructionLabel.setText("Correct! Clue added. Returning...");
        for (Component c : numbersPanel.getComponents()) {
            if (c instanceof JButton) ((JButton) c).setEnabled(false);
        }
        Timer t = new Timer(900, e -> MainGame.getInstance().switchFloor(returnFloor));
        t.setRepeats(false);
        t.start();
    }
}
