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
import java.util.Random;

public class JaredPuzzleQuest extends JPanel {
    private static final Color PANEL_BG = new Color(25, 22, 18);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);
    private static final Color BUTTON_BG = new Color(35, 30, 25);
    private static final Color BUTTON_HOVER = new Color(50, 45, 40);
    private static final Color BUTTON_BORDER = new Color(100, 90, 80);
    private static final Color CORRECT_COLOR = new Color(60, 120, 60);
    private static final Color DRAG_COLOR = new Color(70, 60, 50);

    private String clueToAward;
    private String returnFloor;
    private JLabel instructionLabel;
    private JLabel ascendingInstructionLabel;
    private JLabel roundLabel;
    private JPanel numbersPanel;
    
    private List<Integer> currentNumbers;
    private int currentRound = 1; // 1 to 4
    private boolean solved;

    // Drag and drop state
    private int dragSourceIndex = -1;
    private JLabel ghostLabel = null;
    private Point dragOffset = null;

    public JaredPuzzleQuest() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));
        contentWrapper.setPreferredSize(new Dimension(UiScale.s(1000), UiScale.s(600)));

        JPanel northPanel = new JPanel(new GridLayout(3, 1, 0, UiScale.s(10)));
        northPanel.setOpaque(false);

        instructionLabel = new JLabel("", SwingConstants.CENTER);
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        northPanel.add(instructionLabel);

        ascendingInstructionLabel = new JLabel("Arrange the numbers in ascending order (smallest to largest)", SwingConstants.CENTER);
        ascendingInstructionLabel.setForeground(new Color(200, 180, 150));
        ascendingInstructionLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(20)));
        northPanel.add(ascendingInstructionLabel);

        roundLabel = new JLabel("", SwingConstants.CENTER);
        roundLabel.setForeground(new Color(150, 140, 130));
        roundLabel.setFont(new Font("Serif", Font.ITALIC, UiScale.font(18)));
        northPanel.add(roundLabel);

        contentWrapper.add(northPanel, BorderLayout.NORTH);

        numbersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(15), UiScale.s(15)));
        numbersPanel.setOpaque(false);
        contentWrapper.add(numbersPanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Abort Questioning", UiScale.s(250), UiScale.s(50), false);
        backButton.addActionListener(e -> MainGame.getInstance().switchFloor(returnFloor != null ? returnFloor : "FLOOR2"));
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);
        southPanel.add(backButton);
        contentWrapper.add(southPanel, BorderLayout.SOUTH);

        add(contentWrapper);
    }

    public void startQuest(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        this.currentRound = 1;
        this.solved = false;
        this.dragSourceIndex = -1;
        ascendingInstructionLabel.setVisible(true);
        
        startRound();
    }

    private void startRound() {
        int count = 0;
        switch(currentRound) {
            case 1: count = 3; break;
            case 2: count = 4; break;
            case 3: count = 5; break;
            case 4: count = 6; break;
        }

        generateNumbers(count);
        updateDialogue();
        refreshUI();
    }

    private void generateNumbers(int count) {
        currentNumbers = new ArrayList<>();
        Random rand = new Random();
        while (currentNumbers.size() < count) {
            int n = rand.nextInt(90) + 10;
            if (!currentNumbers.contains(n)) currentNumbers.add(n);
        }
        
        // Ensure not already sorted ascending
        List<Integer> sorted = new ArrayList<>(currentNumbers);
        Collections.sort(sorted);
        if (currentNumbers.equals(sorted)) {
            Collections.shuffle(currentNumbers);
        }
    }

    private void updateDialogue() {
        roundLabel.setText("Round " + currentRound + " of 4");
        if (currentRound < 4) {
            instructionLabel.setText("Jared: I'm telling the truth. I surely am not saying anything wrong.");
        } else {
            instructionLabel.setText("Detective: So you are really sure?");
        }
        instructionLabel.setForeground(TEXT_COLOR);
    }

    private void refreshUI() {
        numbersPanel.removeAll();
        for (int i = 0; i < currentNumbers.size(); i++) {
            final int index = i;
            JLabel numLabel = new JLabel(String.valueOf(currentNumbers.get(i)), SwingConstants.CENTER);
            numLabel.setPreferredSize(new Dimension(UiScale.s(100), UiScale.s(100)));
            numLabel.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(36)));
            numLabel.setForeground(TEXT_COLOR);
            numLabel.setOpaque(true);
            numLabel.setBackground(index == dragSourceIndex ? DRAG_COLOR : BUTTON_BG);
            numLabel.setBorder(BorderFactory.createLineBorder(index == dragSourceIndex ? Color.WHITE : BUTTON_BORDER, 2));
            numLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (solved) return;
                    dragSourceIndex = index;
                    
                    ghostLabel = new JLabel(numLabel.getText(), SwingConstants.CENTER);
                    ghostLabel.setSize(numLabel.getSize());
                    ghostLabel.setFont(numLabel.getFont());
                    ghostLabel.setForeground(TEXT_COLOR);
                    ghostLabel.setOpaque(true);
                    ghostLabel.setBackground(DRAG_COLOR);
                    ghostLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                    
                    Point p = SwingUtilities.convertPoint(numLabel, new Point(0, 0), MainGame.getInstance().getRoot());
                    ghostLabel.setLocation(p);
                    MainGame.getInstance().getRoot().add(ghostLabel, JLayeredPane.DRAG_LAYER);
                    
                    dragOffset = e.getPoint();
                    numLabel.setVisible(false);
                    MainGame.getInstance().getRoot().repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (solved || ghostLabel == null) return;
                    Point p = SwingUtilities.convertPoint(numLabel, e.getPoint(), MainGame.getInstance().getRoot());
                    ghostLabel.setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
                    MainGame.getInstance().getRoot().repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (solved || dragSourceIndex == -1) return;
                    
                    MainGame.getInstance().getRoot().remove(ghostLabel);
                    ghostLabel = null;
                    
                    Point p = SwingUtilities.convertPoint(numLabel, e.getPoint(), numbersPanel);
                    Component target = numbersPanel.getComponentAt(p);
                    
                    if (target instanceof JLabel && target != numLabel) {
                        int targetIndex = -1;
                        for (int j = 0; j < numbersPanel.getComponentCount(); j++) {
                            if (numbersPanel.getComponent(j) == target) {
                                targetIndex = j;
                                break;
                            }
                        }
                        if (targetIndex != -1) {
                            Collections.swap(currentNumbers, dragSourceIndex, targetIndex);
                        }
                    }
                    
                    dragSourceIndex = -1;
                    refreshUI();
                    checkSolved();
                    MainGame.getInstance().getRoot().repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!solved && dragSourceIndex == -1) numLabel.setBackground(BUTTON_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!solved && dragSourceIndex == -1) numLabel.setBackground(BUTTON_BG);
                }
            };
            
            numLabel.addMouseListener(ma);
            numLabel.addMouseMotionListener(ma);
            numbersPanel.add(numLabel);
        }
        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private void checkSolved() {
        List<Integer> sorted = new ArrayList<>(currentNumbers);
        Collections.sort(sorted);
        if (currentNumbers.equals(sorted)) {
            if (currentRound < 4) {
                currentRound++;
                Timer t = new Timer(600, e -> startRound());
                t.setRepeats(false);
                t.start();
            } else {
                onSolved();
            }
        }
    }

    private void onSolved() {
        solved = true;
        instructionLabel.setText("Jared: ...Fine. I saw you. But please, don't tell them.");
        instructionLabel.setForeground(CORRECT_COLOR);
        ascendingInstructionLabel.setVisible(false);
        
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
        return btn;
    }
}
