package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CleaningCartPanel extends JPanel {
    private static final Color PANEL_BG = new Color(25, 22, 18);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color CORRECT_COLOR = new Color(150, 50, 50); // Blood red
    private static final Color DRAG_COLOR = new Color(70, 60, 50);

    private final String TARGET_WORD = "BLOODYMESS";
    private String clueToAward;
    private String returnFloor;
    private JLabel instructionLabel;
    private JLabel hintLabel;
    private JPanel lettersPanel;
    private List<Character> currentLetters;
    private boolean solved;

    // Drag and drop state
    private int dragSourceIndex = -1;
    private JLabel ghostLabel = null;
    private Point dragOffset = null;

    public CleaningCartPanel() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));
        contentWrapper.setPreferredSize(new Dimension(UiScale.s(1000), UiScale.s(600)));

        JPanel northPanel = new JPanel(new GridLayout(2, 1, 0, UiScale.s(10)));
        northPanel.setOpaque(false);

        instructionLabel = new JLabel("There seems to be something here, I need to untangle them to see it clearly.", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        northPanel.add(instructionLabel);

        hintLabel = new JLabel("Hint: bldms", SwingConstants.CENTER);
        hintLabel.setForeground(new Color(150, 140, 130));
        hintLabel.setFont(new Font("Serif", Font.ITALIC, UiScale.font(18)));
        northPanel.add(hintLabel);

        contentWrapper.add(northPanel, BorderLayout.NORTH);

        lettersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(10), UiScale.s(10)));
        lettersPanel.setOpaque(false);
        contentWrapper.add(lettersPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Search", UiScale.s(220), UiScale.s(50), false);
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
        this.solved = false;
        this.dragSourceIndex = -1;
        
        instructionLabel.setText("There seems to be something here, I need to untangle them to see it clearly.");
        instructionLabel.setForeground(TEXT_COLOR);
        hintLabel.setVisible(true);

        // Jumble the letters
        currentLetters = new ArrayList<>();
        for (char c : TARGET_WORD.toCharArray()) {
            currentLetters.add(c);
        }
        
        int guard = 0;
        while (isCorrect() && guard < 50) {
            Collections.shuffle(currentLetters);
            guard++;
        }

        refreshUI();
    }

    private void refreshUI() {
        lettersPanel.removeAll();
        for (int i = 0; i < currentLetters.size(); i++) {
            final int index = i;
            JLabel letterLabel = new JLabel(String.valueOf(currentLetters.get(i)), SwingConstants.CENTER);
            letterLabel.setPreferredSize(new Dimension(UiScale.s(80), UiScale.s(80)));
            letterLabel.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(40)));
            letterLabel.setForeground(TEXT_COLOR);
            letterLabel.setOpaque(true);
            letterLabel.setBackground(index == dragSourceIndex ? DRAG_COLOR : BUTTON_BG);
            letterLabel.setBorder(BorderFactory.createLineBorder(index == dragSourceIndex ? Color.WHITE : BUTTON_BORDER, 2));
            letterLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (solved) return;
                    dragSourceIndex = index;
                    
                    // Create ghost label
                    ghostLabel = new JLabel(letterLabel.getText(), SwingConstants.CENTER);
                    ghostLabel.setSize(letterLabel.getSize());
                    ghostLabel.setFont(letterLabel.getFont());
                    ghostLabel.setForeground(TEXT_COLOR);
                    ghostLabel.setOpaque(true);
                    ghostLabel.setBackground(DRAG_COLOR);
                    ghostLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                    
                    // Add to root layered pane to follow mouse anywhere
                    Point p = SwingUtilities.convertPoint(letterLabel, new Point(0, 0), MainGame.getInstance().getRoot());
                    ghostLabel.setLocation(p);
                    MainGame.getInstance().getRoot().add(ghostLabel, JLayeredPane.DRAG_LAYER);
                    
                    dragOffset = e.getPoint();
                    letterLabel.setVisible(false); // Hide original
                    
                    MainGame.getInstance().getRoot().repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (solved || ghostLabel == null) return;
                    
                    // Update ghost label position
                    Point p = SwingUtilities.convertPoint(letterLabel, e.getPoint(), MainGame.getInstance().getRoot());
                    ghostLabel.setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
                    
                    // Provide feedback on potential target
                    Point panelP = SwingUtilities.convertPoint(MainGame.getInstance().getRoot(), ghostLabel.getLocation(), lettersPanel);
                    Component target = lettersPanel.getComponentAt(panelP);
                    if (target instanceof JLabel && target != letterLabel) {
                        target.setBackground(BUTTON_HOVER);
                    } else {
                        // Reset other backgrounds
                        for (Component c : lettersPanel.getComponents()) {
                            if (c != letterLabel && c.getBackground().equals(BUTTON_HOVER)) {
                                c.setBackground(BUTTON_BG);
                            }
                        }
                    }
                    
                    MainGame.getInstance().getRoot().repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (solved || dragSourceIndex == -1) return;
                    
                    // Clean up ghost label
                    MainGame.getInstance().getRoot().remove(ghostLabel);
                    ghostLabel = null;
                    
                    // Check where it was dropped
                    Point p = SwingUtilities.convertPoint(letterLabel, e.getPoint(), lettersPanel);
                    Component target = lettersPanel.getComponentAt(p);
                    
                    if (target instanceof JLabel && target != letterLabel) {
                        int targetIndex = -1;
                        for (int j = 0; j < lettersPanel.getComponentCount(); j++) {
                            if (lettersPanel.getComponent(j) == target) {
                                targetIndex = j;
                                break;
                            }
                        }
                        
                        if (targetIndex != -1) {
                            Collections.swap(currentLetters, dragSourceIndex, targetIndex);
                        }
                    }
                    
                    dragSourceIndex = -1;
                    refreshUI();
                    checkSolved();
                    
                    MainGame.getInstance().getRoot().repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!solved && dragSourceIndex == -1) letterLabel.setBackground(BUTTON_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!solved && dragSourceIndex == -1) letterLabel.setBackground(BUTTON_BG);
                }
            };
            
            letterLabel.addMouseListener(ma);
            letterLabel.addMouseMotionListener(ma);

            lettersPanel.add(letterLabel);
        }
        lettersPanel.revalidate();
        lettersPanel.repaint();
    }

    private boolean isCorrect() {
        StringBuilder sb = new StringBuilder();
        for (char c : currentLetters) {
            sb.append(c);
        }
        return sb.toString().equals(TARGET_WORD);
    }

    private void checkSolved() {
        if (isCorrect()) {
            onSolved();
        }
    }

    private void onSolved() {
        solved = true;
        instructionLabel.setText("Its dried blood used to clean something!");
        instructionLabel.setForeground(CORRECT_COLOR);
        hintLabel.setVisible(false);

        for (Component c : lettersPanel.getComponents()) {
            if (c instanceof JLabel) {
                c.setVisible(true); // Ensure visible
                c.setForeground(CORRECT_COLOR);
                c.setBackground(new Color(40, 0, 0));
            }
        }

        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }

        Timer t = new Timer(2500, e -> MainGame.getInstance().switchFloor(returnFloor));
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
