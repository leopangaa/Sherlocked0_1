package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;

public class WindowPuzzleQuest extends JPanel {
    private static final Color PANEL_BG = new Color(15, 12, 10);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color INPUT_BG = new Color(25, 22, 18);

    private final String TARGET_WORD = "MEMORYLIAR";
    private String clueToAward;
    private String returnFloor;
    private JLabel instructionLabel;
    private JTextField inputField;
    private JButton verifyButton;
    private JPanel distortionPanel;
    private boolean solved;

    public WindowPuzzleQuest() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));

        instructionLabel = new JLabel("Decrypt the distorted message carved into the glass", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(26)));
        contentWrapper.add(instructionLabel, BorderLayout.NORTH);

        // Center Panel for Distortion and Input
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Distortion Panel (The "Captcha" style text)
        distortionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(-5), 0));
        distortionPanel.setOpaque(false);
        distortionPanel.setPreferredSize(new Dimension(UiScale.s(600), UiScale.s(120)));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UiScale.s(40), 0);
        centerPanel.add(distortionPanel, gbc);

        // 2. Input Field
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(UiScale.s(400), UiScale.s(60)));
        inputField.setBackground(INPUT_BG);
        inputField.setForeground(TEXT_COLOR);
        inputField.setCaretColor(TEXT_COLOR);
        inputField.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(32)));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_BORDER, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Add action listener for Enter key
        inputField.addActionListener(e -> verifyInput());
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, UiScale.s(20), 0);
        centerPanel.add(inputField, gbc);

        // 3. Verify Button
        verifyButton = createStyledButton("Verify Sequence", UiScale.s(200), UiScale.s(50), false);
        verifyButton.addActionListener(e -> verifyInput());
        gbc.gridy = 2;
        centerPanel.add(verifyButton, gbc);

        contentWrapper.add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel for Abort
        JButton backButton = createStyledButton("Abort Investigation", UiScale.s(220), UiScale.s(45), false);
        backButton.setFont(new Font("Serif", Font.BOLD, UiScale.font(16)));
        backButton.addActionListener(e -> {
            if (returnFloor != null) {
                MainGame.getInstance().switchFloor(returnFloor);
            } else {
                MainGame.getInstance().switchFloor("FLOOR1");
            }
        });
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        contentWrapper.add(southPanel, BorderLayout.SOUTH);

        add(contentWrapper);
    }

    private void createDistortedText() {
        distortionPanel.removeAll();
        String text = TARGET_WORD;
        
        for (int i = 0; i < text.length(); i++) {
            JLabel letter = new JLabel(String.valueOf(text.charAt(i)));
            
            // Randomize font style slightly for distortion
            int style = (Math.random() > 0.5) ? Font.BOLD : Font.ITALIC;
            if (Math.random() > 0.8) style = Font.BOLD | Font.ITALIC;
            
            // Randomize size slightly
            int size = UiScale.font(45 + (int)(Math.random() * 15));
            letter.setFont(new Font("Serif", style, size));
            
            // Randomize color slightly (shades of text color)
            int r = 245 - (int)(Math.random() * 30);
            int g = 235 - (int)(Math.random() * 30);
            int b = 220 - (int)(Math.random() * 30);
            letter.setForeground(new Color(r, g, b));
            
            // Randomize vertical offset
            int yOffset = (int)(Math.random() * 20) - 10;
            letter.setBorder(BorderFactory.createEmptyBorder(yOffset < 0 ? 0 : yOffset, 0, yOffset > 0 ? 0 : -yOffset, 0));
            
            distortionPanel.add(letter);
        }
        distortionPanel.revalidate();
        distortionPanel.repaint();
    }

    private void verifyInput() {
        if (solved) return;
        
        String input = inputField.getText().trim().toUpperCase();
        if (input.equals(TARGET_WORD)) {
            onSolved();
        } else {
            // Shake effect or feedback
            inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            Timer t = new Timer(500, e -> {
                inputField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BUTTON_BORDER, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            });
            t.setRepeats(false);
            t.start();
            inputField.selectAll();
            inputField.requestFocus();
        }
    }

    private JButton createStyledButton(String text, int w, int h, boolean small) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setFont(new Font("Serif", Font.BOLD, UiScale.font(small ? 16 : 20)));
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

    public void startPuzzle(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.solved = false;
        
        inputField.setText("");
        inputField.setEditable(true);
        verifyButton.setEnabled(true);
        instructionLabel.setText("Decrypt the distorted message carved into the glass");
        
        createDistortedText();
        
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
    }

    private void onSolved() {
        solved = true;
        inputField.setEditable(false);
        verifyButton.setEnabled(false);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GREEN, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }
        instructionLabel.setText("Sequence Verified: " + TARGET_WORD);
        
        Timer t = new Timer(1500, e -> MainGame.getInstance().switchFloor(returnFloor));
        t.setRepeats(false);
        t.start();
    }
}
