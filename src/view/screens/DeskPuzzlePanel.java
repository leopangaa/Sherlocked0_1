package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DeskPuzzlePanel extends JPanel {
    private static final Color PANEL_BG = new Color(25, 22, 18);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color CORRECT_COLOR = new Color(60, 120, 60);
    private static final Color WRONG_COLOR = new Color(150, 50, 50);

    private JLabel instructionLabel;
    private JPanel numbersPanel;
    private String clueToAward;
    private String returnFloor;
    private List<Integer> currentNumbers;
    private int smallestNumber;
    private boolean solved;

    public DeskPuzzlePanel() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));
        contentWrapper.setPreferredSize(new Dimension(UiScale.s(900), UiScale.s(500)));

        instructionLabel = new JLabel("The desk is locked with a numerical security system. Identify the smallest value to unlock.", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(22)));
        contentWrapper.add(instructionLabel, BorderLayout.NORTH);

        numbersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(15), UiScale.s(15)));
        numbersPanel.setOpaque(false);
        contentWrapper.add(numbersPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Search", UiScale.s(220), UiScale.s(45), false);
        backButton.addActionListener(e -> MainGame.getInstance().switchFloor(returnFloor != null ? returnFloor : "FLOOR2"));
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        contentWrapper.add(southPanel, BorderLayout.SOUTH);

        add(contentWrapper);
    }

    public void startPuzzle(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        
        generateNumbers();
        refreshUI();
    }

    private void generateNumbers() {
        currentNumbers = new ArrayList<>();
        Random rand = new Random();
        while (currentNumbers.size() < 8) {
            int n = rand.nextInt(900) + 100; // 100-999
            if (!currentNumbers.contains(n)) {
                currentNumbers.add(n);
            }
        }
        
        smallestNumber = Collections.min(currentNumbers);
        Collections.shuffle(currentNumbers);
    }

    private void refreshUI() {
        numbersPanel.removeAll();
        for (int num : currentNumbers) {
            JButton btn = createStyledButton(String.valueOf(num), UiScale.s(120), UiScale.s(80), true);
            btn.addActionListener(e -> handleSelection(num, btn));
            numbersPanel.add(btn);
        }
        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private void handleSelection(int selected, JButton btn) {
        if (solved) return;

        if (selected == smallestNumber) {
            solved = true;
            btn.setBackground(CORRECT_COLOR);
            instructionLabel.setText("Lock Disengaged. The desk drawer slides open.");
            instructionLabel.setForeground(CORRECT_COLOR);
            
            if (clueToAward != null && !clueToAward.isBlank()) {
                GameState.getInstance().addClue(clueToAward);
            }
            
            Timer t = new Timer(1500, e -> MainGame.getInstance().switchFloor(returnFloor));
            t.setRepeats(false);
            t.start();
        } else {
            btn.setBackground(WRONG_COLOR);
            instructionLabel.setText("Access Denied. That is not the smallest value.");
            instructionLabel.setForeground(WRONG_COLOR);
            
            Timer t = new Timer(1000, e -> {
                instructionLabel.setText("The desk is locked with a numerical security system. Identify the smallest value to unlock.");
                instructionLabel.setForeground(TEXT_COLOR);
                btn.setBackground(BUTTON_BG);
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private JButton createStyledButton(String text, int w, int h, boolean isNumber) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setFont(new Font(isNumber ? "Monospaced" : "Serif", Font.BOLD, UiScale.font(isNumber ? 28 : 18)));
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
}
