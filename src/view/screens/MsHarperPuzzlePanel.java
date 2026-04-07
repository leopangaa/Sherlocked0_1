package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MsHarperPuzzlePanel extends JPanel {
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color BUTTON_CORRECT = new Color(40, 70, 40);

    private String clueToAward;
    private String returnFloor;
    private JPanel numbersGrid;
    private JLabel instructionLabel;
    private List<Integer> allNumbers;
    private Set<Integer> duplicatedNumbers; // The 5 target numbers
    private int totalClickedCorrect; // Should reach 10 (5 pairs)
    private boolean solved;

    public MsHarperPuzzlePanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(22, 18, 15)); // Match the container background

        // Use a fixed-size wrapper to ensure the UI never shifts
        JPanel wrapper = new JPanel(new BorderLayout(0, UiScale.s(25)));
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(UiScale.s(1000), UiScale.s(650)));

        instructionLabel = new JLabel("Focus Vision: Find the 5 pairs of duplicated numbers", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        wrapper.add(instructionLabel, BorderLayout.NORTH);

        // Grid for numbers
        numbersGrid = new JPanel(new GridLayout(5, 8, UiScale.s(10), UiScale.s(10)));
        numbersGrid.setOpaque(false);
        wrapper.add(numbersGrid, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Investigation", UiScale.s(200), UiScale.s(40), false);
        backButton.addActionListener(e -> MainGame.getInstance().switchFloor(returnFloor != null ? returnFloor : "FLOOR1"));
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        wrapper.add(southPanel, BorderLayout.SOUTH);

        add(wrapper);
    }

    private JButton createStyledButton(String text, int w, int h, boolean isNumber) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setFont(new Font(isNumber ? "Monospaced" : "Serif", Font.BOLD, UiScale.font(isNumber ? 24 : 16)));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(BUTTON_BG);
        btn.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled() && btn.getBackground().equals(BUTTON_BG)) {
                    btn.setBackground(BUTTON_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled() && btn.getBackground().equals(BUTTON_HOVER)) {
                    btn.setBackground(BUTTON_BG);
                }
            }
        });
        return btn;
    }

    public void startPuzzle(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        this.totalClickedCorrect = 0;
        
        generateNumbers();
        refreshUI();
    }

    private void generateNumbers() {
        allNumbers = new ArrayList<>();
        duplicatedNumbers = new HashSet<>();
        
        // 1. Pick 5 unique numbers for duplication
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i <= 100; i++) pool.add(i);
        Collections.shuffle(pool);
        
        for (int i = 0; i < 5; i++) {
            int num = pool.remove(0);
            duplicatedNumbers.add(num);
            allNumbers.add(num);
            allNumbers.add(num); // Add twice
        }
        
        // 2. Fill remaining 30 slots with unique numbers
        for (int i = 0; i < 30; i++) {
            allNumbers.add(pool.remove(0));
        }
        
        Collections.shuffle(allNumbers);
    }

    private void refreshUI() {
        numbersGrid.removeAll();
        for (int num : allNumbers) {
            JButton btn = createStyledButton(String.valueOf(num), UiScale.s(80), UiScale.s(80), true);
            btn.addActionListener(e -> {
                if (solved) return;
                
                if (duplicatedNumbers.contains(num)) {
                    btn.setBackground(BUTTON_CORRECT);
                    btn.setEnabled(false);
                    totalClickedCorrect++;
                    
                    if (totalClickedCorrect >= 10) {
                        onSolved();
                    } else {
                        // Use a fixed length string to prevent label resizing
                        instructionLabel.setText(String.format("Focus Vision: %d/10 numbers identified", totalClickedCorrect));
                    }
                } else {
                    // Penalty or feedback
                    btn.setBackground(Color.RED);
                    Timer t = new Timer(300, ev -> btn.setBackground(BUTTON_BG));
                    t.setRepeats(false);
                    t.start();
                }
            });
            numbersGrid.add(btn);
        }
        // Force layout update once
        numbersGrid.revalidate();
        numbersGrid.repaint();
    }

    private void onSolved() {
        solved = true;
        instructionLabel.setText("Vision clear. Ms. Harper remembers everything.");
        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }
        
        Timer t = new Timer(1500, e -> MainGame.getInstance().switchFloor(returnFloor));
        t.setRepeats(false);
        t.start();
    }
}
