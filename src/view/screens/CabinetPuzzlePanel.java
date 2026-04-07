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

public class CabinetPuzzlePanel extends JPanel {
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
    private JPanel numbersPanel;
    private List<Integer> currentNumbers;
    private int targetNumber;
    private boolean solved;

    // Drag and drop state
    private int dragSourceIndex = -1;
    private JLabel ghostLabel = null;
    private Point dragOffset = null;

    public CabinetPuzzlePanel() {
        setLayout(new GridBagLayout());
        setBackground(PANEL_BG);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, UiScale.s(40)));
        contentWrapper.setOpaque(true);
        contentWrapper.setBackground(new Color(22, 18, 15));
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(UiScale.s(50), UiScale.s(80), UiScale.s(50), UiScale.s(80)));
        contentWrapper.setPreferredSize(new Dimension(UiScale.s(1000), UiScale.s(600)));

        instructionLabel = new JLabel("Identify the middlemost number of the sequence and pull it to unlock the cabinet.", SwingConstants.CENTER);
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
        this.dragSourceIndex = -1;
        
        generateNumbers();
        refreshUI();
        
        instructionLabel.setText("Identify the middlemost number of the sequence and pull it to unlock the cabinet.");
        instructionLabel.setForeground(TEXT_COLOR);
    }

    private void generateNumbers() {
        currentNumbers = new ArrayList<>();
        Random rand = new Random();
        while (currentNumbers.size() < 10) {
            int n = rand.nextInt(900) + 100; // 100-999
            if (!currentNumbers.contains(n)) {
                currentNumbers.add(n);
            }
        }
        
        List<Integer> sorted = new ArrayList<>(currentNumbers);
        Collections.sort(sorted);
        // Middlemost of 10 is index 4 or 5. Let's pick 5 (the 6th number).
        targetNumber = sorted.get(5);
        
        Collections.shuffle(currentNumbers);
    }

    private void refreshUI() {
        numbersPanel.removeAll();
        for (int i = 0; i < currentNumbers.size(); i++) {
            final int index = i;
            final int num = currentNumbers.get(i);
            
            JLabel numLabel = new JLabel(String.valueOf(num), SwingConstants.CENTER);
            numLabel.setPreferredSize(new Dimension(UiScale.s(120), UiScale.s(80)));
            numLabel.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(28)));
            numLabel.setForeground(TEXT_COLOR);
            numLabel.setOpaque(true);
            numLabel.setBackground(BUTTON_BG);
            numLabel.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER, 2));
            numLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (solved) return;
                    if (num != targetNumber) {
                        numLabel.setBackground(new Color(150, 50, 50));
                        instructionLabel.setText("That is not the middlemost value. Try again.");
                        instructionLabel.setForeground(new Color(150, 50, 50));
                        
                        Timer t = new Timer(1000, ev -> {
                            numLabel.setBackground(BUTTON_BG);
                            instructionLabel.setText("Identify the middlemost number of the sequence and pull it to unlock the cabinet.");
                            instructionLabel.setForeground(TEXT_COLOR);
                        });
                        t.setRepeats(false);
                        t.start();
                        return;
                    }
                    
                    dragSourceIndex = index;
                    
                    // Create ghost label
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
                    if (solved || ghostLabel == null) return;
                    
                    MainGame.getInstance().getRoot().remove(ghostLabel);
                    ghostLabel = null;
                    
                    // User said "drag it anywhere" - so if they released it, it's completed
                    onSolved();
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

    private void onSolved() {
        solved = true;
        instructionLabel.setText("Middlemost number identified. Cabinet unlocked.");
        instructionLabel.setForeground(CORRECT_COLOR);
        
        if (clueToAward != null && !clueToAward.isBlank()) {
            GameState.getInstance().addClue(clueToAward);
        }
        
        Timer t = new Timer(1500, e -> MainGame.getInstance().switchFloor(returnFloor));
        t.setRepeats(false);
        t.start();
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
        return btn;
    }
}
