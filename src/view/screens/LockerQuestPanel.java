package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;

public class LockerQuestPanel extends JPanel {
    private static final Color PANEL_BG = new Color(25, 22, 18);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color CORRECT_COLOR = new Color(60, 120, 60);
    private static final Color WRONG_COLOR = new Color(150, 50, 50);

    private final String CORRECT_CODE = "32142";
    private String currentInput = "";
    private JLabel instructionLabel;
    private JLabel displayLabel;
    private String clueToAward;
    private String returnFloor;
    private boolean solved;

    public LockerQuestPanel() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));
        contentWrapper.setPreferredSize(new Dimension(UiScale.s(800), UiScale.s(600)));

        instructionLabel = new JLabel("Locker Security: Enter the 5-digit authorization code.", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        contentWrapper.add(instructionLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, UiScale.s(20)));
        centerPanel.setOpaque(false);

        displayLabel = new JLabel("_____", SwingConstants.CENTER);
        displayLabel.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(48)));
        displayLabel.setForeground(TEXT_COLOR);
        centerPanel.add(displayLabel, BorderLayout.NORTH);

        JPanel keypadPanel = new JPanel(new GridLayout(4, 3, UiScale.s(10), UiScale.s(10)));
        keypadPanel.setOpaque(false);

        for (int i = 1; i <= 9; i++) {
            keypadPanel.add(createKeypadButton(String.valueOf(i)));
        }
        keypadPanel.add(createKeypadButton("C")); // Clear
        keypadPanel.add(createKeypadButton("0"));
        keypadPanel.add(createKeypadButton("OK"));

        centerPanel.add(keypadPanel, BorderLayout.CENTER);
        contentWrapper.add(centerPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Entry", UiScale.s(220), UiScale.s(45), false);
        backButton.addActionListener(e -> MainGame.getInstance().switchFloor(returnFloor != null ? returnFloor : "FLOOR2"));
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        contentWrapper.add(southPanel, BorderLayout.SOUTH);

        add(contentWrapper);
    }

    private JButton createKeypadButton(String text) {
        JButton btn = createStyledButton(text, UiScale.s(80), UiScale.s(80), true);
        btn.addActionListener(e -> handleKeypadPress(text));
        return btn;
    }

    private void handleKeypadPress(String key) {
        if (solved) return;

        if (key.equals("C")) {
            currentInput = "";
        } else if (key.equals("OK")) {
            verifyCode();
            return;
        } else {
            if (currentInput.length() < 5) {
                currentInput += key;
            }
        }
        updateDisplay();
    }

    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < currentInput.length()) {
                sb.append(currentInput.charAt(i));
            } else {
                sb.append("_");
            }
        }
        displayLabel.setText(sb.toString());
    }

    private void verifyCode() {
        if (currentInput.equals(CORRECT_CODE)) {
            onSolved();
        } else {
            instructionLabel.setText("Invalid Code. Access Denied.");
            instructionLabel.setForeground(WRONG_COLOR);
            displayLabel.setForeground(WRONG_COLOR);
            
            Timer t = new Timer(1000, e -> {
                if (!solved) {
                    instructionLabel.setText("Locker Security: Enter the 5-digit authorization code.");
                    instructionLabel.setForeground(TEXT_COLOR);
                    displayLabel.setForeground(TEXT_COLOR);
                    currentInput = "";
                    updateDisplay();
                }
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void onSolved() {
        solved = true;
        instructionLabel.setText("Access Granted. Locker unlocked.");
        instructionLabel.setForeground(CORRECT_COLOR);
        displayLabel.setForeground(CORRECT_COLOR);
        
        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }
        
        Timer t = new Timer(1500, e -> MainGame.getInstance().switchFloor(returnFloor));
        t.setRepeats(false);
        t.start();
    }

    public void startQuest(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        this.currentInput = "";
        updateDisplay();
        instructionLabel.setText("Locker Security: Enter the 5-digit authorization code.");
        instructionLabel.setForeground(TEXT_COLOR);
        displayLabel.setForeground(TEXT_COLOR);
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
