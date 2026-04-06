package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CctvAnomalyPuzzlePanel extends JPanel {
    private static final Color PANEL_BG = new Color(22, 18, 15);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);

    private String clueToAward;
    private String returnFloor;
    private JPanel numbersPanel;
    private JLabel instructionLabel;
    private List<Integer> numbers;
    private boolean solved;

    public CctvAnomalyPuzzlePanel() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel wrapper = new JPanel(new BorderLayout(0, UiScale.s(30)));
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(UiScale.s(1000), UiScale.s(600)));

        instructionLabel = new JLabel("CCTV System Error: Align timestamps in descending order (Largest to Smallest)", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        wrapper.add(instructionLabel, BorderLayout.NORTH);

        numbersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(15), UiScale.s(15)));
        numbersPanel.setOpaque(false);
        wrapper.add(numbersPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Repair", UiScale.s(220), UiScale.s(45), false);
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
        btn.setFont(new Font(isNumber ? "Monospaced" : "Serif", Font.BOLD, UiScale.font(isNumber ? 32 : 18)));
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
        
        numbers = new ArrayList<>();
        while (numbers.size() < 7) {
            int n = (int) (Math.random() * 90) + 10; // 10-99
            if (!numbers.contains(n)) numbers.add(n);
        }

        // Shuffle until not sorted descending
        int guard = 0;
        while (isSortedDescending() && guard < 50) {
            Collections.shuffle(numbers);
            guard++;
        }
        
        refreshUI();
    }

    private void refreshUI() {
        numbersPanel.removeAll();
        for (int i = 0; i < numbers.size(); i++) {
            final int index = i;
            JButton btn = createStyledButton(String.valueOf(numbers.get(i)), UiScale.s(110), UiScale.s(110), true);
            
            btn.addActionListener(e -> {
                if (solved) return;
                // Swap with next (circular)
                int nextIndex = (index + 1) % numbers.size();
                Collections.swap(numbers, index, nextIndex);
                refreshUI();
            });
            
            numbersPanel.add(btn);
        }
        numbersPanel.revalidate();
        numbersPanel.repaint();

        if (!solved && isSortedDescending()) {
            onSolved();
        }
    }

    private boolean isSortedDescending() {
        for (int i = 0; i < numbers.size() - 1; i++) {
            if (numbers.get(i) < numbers.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    private void onSolved() {
        solved = true;
        instructionLabel.setText("System Restored. Footage anomaly identified.");
        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }
        
        Timer t = new Timer(1500, e -> MainGame.getInstance().switchFloor(returnFloor));
        t.setRepeats(false);
        t.start();
    }
}
