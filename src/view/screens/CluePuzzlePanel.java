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
        setBackground(new Color(24, 22, 20)); // Match the dark theme

        instructionLabel = new JLabel("Sorting Algorithm: Arrange the sequence in ascending order", SwingConstants.CENTER);
        instructionLabel.setForeground(new Color(240, 230, 210));
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(22)));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(UiScale.s(30), UiScale.s(10), UiScale.s(30), UiScale.s(10)));
        add(instructionLabel, BorderLayout.NORTH);

        numbersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(15), UiScale.s(50)));
        numbersPanel.setOpaque(false);
        add(numbersPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(20), UiScale.s(30)));
        southPanel.setOpaque(false);

        JButton backButton = new JButton("Abort Investigation");
        backButton.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        backButton.setBackground(new Color(60, 40, 30));
        backButton.setForeground(new Color(220, 210, 190));
        backButton.setFocusPainted(false);
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

    public void startPuzzle(String clue, String returnTo, String difficulty) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        
        String diffText = "MEDIUM";
        int count = 5; 
        if ("HARD".equalsIgnoreCase(difficulty)) {
            count = 8;
            diffText = "HARD";
        } else if ("EASY".equalsIgnoreCase(difficulty)) {
            count = 3;
            diffText = "EASY";
        }
        
        instructionLabel.setText("Sorting Protocol [" + diffText + "]: Reconstruct the sequence");
        
        numbers = new ArrayList<>();
        while (numbers.size() < count) {
            int v = (int) (Math.random() * 90) + 10; // 2-digit numbers look better
            if (!numbers.contains(v)) numbers.add(v);
        }

        int guard = 0;
        while (isSorted() && guard < 50) {
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
            numBtn.setPreferredSize(new Dimension(UiScale.s(75), UiScale.s(75)));
            numBtn.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(26)));
            numBtn.setBackground(new Color(200, 190, 170)); // Match inventory button style
            numBtn.setForeground(new Color(30, 25, 20));
            numBtn.setFocusPainted(false);
            numBtn.setBorder(BorderFactory.createLineBorder(new Color(60, 40, 30), 2));
            
            // Selection Sort / Bubble Sort inspired interaction: Swap with next
            numBtn.addActionListener(e -> {
                if (solved) return;
                // Swap current with next (circular)
                int nextIndex = (index + 1) % numbers.size();
                Collections.swap(numbers, index, nextIndex);
                refreshUI();
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
