package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerminalPuzzleQuest extends JPanel {
    private static final Color PANEL_BG = new Color(25, 22, 18);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color CORRECT_COLOR = new Color(60, 120, 60);
    private static final Color WRONG_COLOR = new Color(150, 50, 50);

    private String clueToAward;
    private String returnFloor;
    private JLabel instructionLabel;
    private JLabel displayLabel;
    private JPanel keypadPanel;
    
    private final int TARGET_DIGIT = 7;
    private String fullSequence;
    private int missingIndex;
    private boolean solved;

    public TerminalPuzzleQuest() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));
        contentWrapper.setPreferredSize(new Dimension(UiScale.s(850), UiScale.s(600)));

        instructionLabel = new JLabel("Terminal locked. Complete the authorization sequence.", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        contentWrapper.add(instructionLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, UiScale.s(30)));
        centerPanel.setOpaque(false);

        displayLabel = new JLabel("", SwingConstants.CENTER);
        displayLabel.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(48)));
        displayLabel.setForeground(TEXT_COLOR);
        centerPanel.add(displayLabel, BorderLayout.NORTH);

        keypadPanel = new JPanel(new GridLayout(2, 5, UiScale.s(10), UiScale.s(10)));
        keypadPanel.setOpaque(false);

        for (int i = 0; i <= 9; i++) {
            keypadPanel.add(createKeypadButton(String.valueOf(i)));
        }

        centerPanel.add(keypadPanel, BorderLayout.CENTER);
        contentWrapper.add(centerPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Terminal Access", UiScale.s(250), UiScale.s(50), false);
        backButton.addActionListener(e -> MainGame.getInstance().switchFloor(returnFloor != null ? returnFloor : "FLOOR2"));
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        contentWrapper.add(southPanel, BorderLayout.SOUTH);

        add(contentWrapper);
    }

    private JButton createKeypadButton(String text) {
        JButton btn = createStyledButton(text, UiScale.s(80), UiScale.s(80), true);
        btn.addActionListener(e -> handleInput(Integer.parseInt(text), btn));
        return btn;
    }

    public void startQuest(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        
        generateSequence();
        updateDisplay();
        
        instructionLabel.setText("Terminal locked. Complete the authorization sequence.");
        instructionLabel.setForeground(TEXT_COLOR);
        displayLabel.setForeground(TEXT_COLOR);
        
        for (Component c : keypadPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setBackground(BUTTON_BG);
                c.setEnabled(true);
            }
        }
    }

    private void generateSequence() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        
        // Generate 8 digits, one must be the target digit
        missingIndex = rand.nextInt(8);
        for (int i = 0; i < 8; i++) {
            if (i == missingIndex) {
                sb.append(TARGET_DIGIT);
            } else {
                sb.append(rand.nextInt(10));
            }
        }
        fullSequence = sb.toString();
    }

    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fullSequence.length(); i++) {
            if (i == missingIndex) {
                sb.append("_");
            } else {
                sb.append(fullSequence.charAt(i));
            }
            if (i < fullSequence.length() - 1) sb.append(" ");
        }
        displayLabel.setText(sb.toString());
    }

    private void handleInput(int digit, JButton btn) {
        if (solved) return;

        if (digit == TARGET_DIGIT) {
            solved = true;
            btn.setBackground(CORRECT_COLOR);
            displayLabel.setText(fullSequence.replace("", " ").trim());
            displayLabel.setForeground(CORRECT_COLOR);
            
            instructionLabel.setText("Wait... this is my keycard code?");
            instructionLabel.setForeground(CORRECT_COLOR);
            
            if (clueToAward != null && !clueToAward.isBlank()) {
                GameState.getInstance().addClue(clueToAward);
            }
            
            Timer t = new Timer(2000, e -> MainGame.getInstance().switchFloor(returnFloor));
            t.setRepeats(false);
            t.start();
        } else {
            btn.setBackground(WRONG_COLOR);
            instructionLabel.setText("Sequence incorrect. Unauthorized access attempt.");
            instructionLabel.setForeground(WRONG_COLOR);
            
            Timer t = new Timer(1000, e -> {
                if (!solved) {
                    instructionLabel.setText("Terminal locked. Complete the authorization sequence.");
                    instructionLabel.setForeground(TEXT_COLOR);
                    btn.setBackground(BUTTON_BG);
                }
            });
            t.setRepeats(false);
            t.start();
        }
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

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (btn.isEnabled() && btn.getBackground().equals(BUTTON_BG)) {
                    btn.setBackground(BUTTON_HOVER);
                }
            }
            public void mouseExited(MouseEvent evt) {
                if (btn.isEnabled() && btn.getBackground().equals(BUTTON_HOVER)) {
                    btn.setBackground(BUTTON_BG);
                }
            }
        });
        return btn;
    }
}
