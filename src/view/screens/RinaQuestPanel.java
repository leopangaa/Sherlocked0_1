package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RinaQuestPanel extends JPanel {
    private static final Color PANEL_BG = new Color(25, 22, 18);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color CORRECT_COLOR = new Color(60, 120, 60);
    private static final Color WRONG_COLOR = new Color(150, 50, 50);

    private JLabel instructionLabel;
    private JLabel progressLabel;
    private JPanel buttonsPanel;
    private String clueToAward;
    private String returnFloor;
    
    private int currentRound = 0;
    private final int TOTAL_ROUNDS = 6;
    private final int NUMBERS_PER_ROUND = 5;
    private final int TARGET_ROOM = 217;
    
    private List<Integer> currentSequence;
    private boolean solved;

    public RinaQuestPanel() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));
        contentWrapper.setPreferredSize(new Dimension(UiScale.s(900), UiScale.s(600)));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, UiScale.s(10)));
        headerPanel.setOpaque(false);

        instructionLabel = new JLabel("Help Rina: Select the room number closest to 217", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(28)));
        headerPanel.add(instructionLabel);

        progressLabel = new JLabel("Sequence 1 of 6", SwingConstants.CENTER);
        progressLabel.setForeground(new Color(180, 170, 150));
        progressLabel.setFont(new Font("Serif", Font.ITALIC, UiScale.font(20)));
        headerPanel.add(progressLabel);

        contentWrapper.add(headerPanel, BorderLayout.NORTH);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(20), UiScale.s(20)));
        buttonsPanel.setOpaque(false);
        contentWrapper.add(buttonsPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Help", UiScale.s(220), UiScale.s(50), false);
        backButton.setFont(new Font("Serif", Font.BOLD, UiScale.font(16)));
        backButton.addActionListener(e -> {
            if (returnFloor != null) {
                MainGame.getInstance().switchFloor(returnFloor);
            } else {
                MainGame.getInstance().switchFloor("FLOOR2");
            }
        });
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        contentWrapper.add(southPanel, BorderLayout.SOUTH);

        add(contentWrapper);
    }

    public void startQuest(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.currentRound = 0;
        this.solved = false;
        instructionLabel.setText("Help Rina: Select the room number closest to 217");
        instructionLabel.setForeground(TEXT_COLOR);
        generateRound();
    }

    private void generateRound() {
        currentSequence = new ArrayList<>();
        Random rand = new Random();
        
        // Generate 5 numbers, one will be the closest
        while (currentSequence.size() < NUMBERS_PER_ROUND) {
            int room;
            int floor = rand.nextInt(3) + 1; // 1-3
            int r = rand.nextInt(50) + 1; // 1-50
            room = floor * 100 + r;
            
            if (!currentSequence.contains(room) && room != TARGET_ROOM) {
                currentSequence.add(room);
            }
        }
        
        refreshUI();
    }

    private void refreshUI() {
        buttonsPanel.removeAll();
        progressLabel.setText("Sequence " + (currentRound + 1) + " of " + TOTAL_ROUNDS);
        
        for (int roomNum : currentSequence) {
            JButton btn = createStyledButton(String.valueOf(roomNum), UiScale.s(140), UiScale.s(100), true);
            btn.addActionListener(e -> handleSelection(roomNum, btn));
            buttonsPanel.add(btn);
        }
        
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    private void handleSelection(int selectedRoom, JButton sourceBtn) {
        if (solved) return;

        int closest = currentSequence.get(0);
        int minDiff = Math.abs(closest - TARGET_ROOM);

        for (int room : currentSequence) {
            int diff = Math.abs(room - TARGET_ROOM);
            if (diff < minDiff) {
                minDiff = diff;
                closest = room;
            }
        }

        if (selectedRoom == closest) {
            sourceBtn.setBackground(CORRECT_COLOR);
            Timer t = new Timer(600, e -> {
                currentRound++;
                if (currentRound >= TOTAL_ROUNDS) {
                    onSolved();
                } else {
                    generateRound();
                }
            });
            t.setRepeats(false);
            t.start();
        } else {
            sourceBtn.setBackground(WRONG_COLOR);
            instructionLabel.setText("That doesn't seem right. Try again!");
            instructionLabel.setForeground(WRONG_COLOR);
            
            Timer t = new Timer(1000, e -> {
                instructionLabel.setText("Help Rina: Select the room number closest to 217");
                instructionLabel.setForeground(TEXT_COLOR);
                generateRound(); // Restart round
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void onSolved() {
        solved = true;
        instructionLabel.setText("Rina: I remember now! Thank you, detective.");
        instructionLabel.setForeground(CORRECT_COLOR);
        progressLabel.setText("Memory Restored");

        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }

        Timer t = new Timer(2000, e -> MainGame.getInstance().switchFloor(returnFloor));
        t.setRepeats(false);
        t.start();
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
}
