package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CluePuzzlePanel extends JPanel {
    private static final Color PANEL_BG = new Color(15, 12, 10);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);

    private String clueToAward;
    private String returnFloor;
    private ArrayList<Integer> numbers;
    private JPanel numbersPanel;
    private JLabel instructionLabel;
    private JPanel contentWrapper;
    private boolean solved;

    public CluePuzzlePanel() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(30)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(40), UiScale.s(80), UiScale.s(40), UiScale.s(80)));

        instructionLabel = new JLabel("Sort the numbers in ascending order (Smallest to Largest)", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(34)));
        contentWrapper.add(instructionLabel, BorderLayout.NORTH);

        // Rectangular buttons: 4 columns of 200 + 3 gaps of 15 = 845
        // 3 rows of 110 + 2 gaps of 15 = 360
        int btnW = UiScale.s(200);
        int btnH = UiScale.s(110);
        int gap = UiScale.s(15);
        
        int gridW = (btnW * 4) + (gap * 3);
        int gridH = (btnH * 3) + (gap * 2);

        numbersPanel = new JPanel(new GridLayout(3, 4, gap, gap));
        numbersPanel.setOpaque(false);
        numbersPanel.setPreferredSize(new Dimension(gridW, gridH));
        contentWrapper.add(numbersPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Investigation", UiScale.s(260), UiScale.s(55), false);
        backButton.addActionListener(e -> {
            if (returnFloor != null) {
                MainGame.getInstance().switchFloor(returnFloor);
            } else {
                MainGame.getInstance().switchFloor("LOBBY");
            }
        });
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        contentWrapper.add(southPanel, BorderLayout.SOUTH);

        add(contentWrapper);
    }

    private JButton createStyledButton(String text, int w, int h, boolean isNumber) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(w, h));
        if (isNumber) {
            btn.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(48)));
        } else {
            btn.setFont(new Font("Serif", Font.BOLD, UiScale.font(22)));
        }
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(BUTTON_BG);
        btn.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(BUTTON_HOVER);
                    btn.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 1));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(BUTTON_BG);
                    btn.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER, 1));
                }
            }
        });
        return btn;
    }

    public void startPuzzle(String clue, String returnTo, String difficulty) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        
        int count = 12;
        
        instructionLabel.setText("Sort the numbers in ascending order (Smallest to Largest)");
        
        numbers = new ArrayList<>();
        while (numbers.size() < count) {
            int floor = (int) (Math.random() * 3) + 1;
            int room = (int) (Math.random() * 15) + 1;
            int v = floor * 100 + room;
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
        int btnW = UiScale.s(200);
        int btnH = UiScale.s(110);
        
        for (int i = 0; i < numbers.size(); i++) {
            final int index = i;
            JButton numBtn = createStyledButton(String.valueOf(numbers.get(i)), btnW, btnH, true);
            
            numBtn.addActionListener(e -> {
                if (solved) return;
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
