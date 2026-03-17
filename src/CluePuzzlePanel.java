import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CluePuzzlePanel extends JPanel {
    private String clueToAward;
    private String returnFloor;
    private ArrayList<Integer> numbers;
    private JPanel numbersPanel;
    private JLabel instructionLabel;

    public CluePuzzlePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 45));

        instructionLabel = new JLabel("Sort the numbers in ascending order (Smallest to Largest)", SwingConstants.CENTER);
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(instructionLabel, BorderLayout.NORTH);

        numbersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        numbersPanel.setOpaque(false);
        add(numbersPanel, BorderLayout.CENTER);

        JButton checkButton = new JButton("Verify Sequence");
        checkButton.setFont(new Font("Serif", Font.BOLD, 18));
        checkButton.addActionListener(e -> verifySort());
        
        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        southPanel.add(checkButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    public void startPuzzle(String clue, String returnTo) {
        this.clueToAward = clue;
        this.returnFloor = returnTo;
        
        // Generate random numbers
        numbers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            numbers.add((int) (Math.random() * 100));
        }
        
        refreshUI();
    }

    private void refreshUI() {
        numbersPanel.removeAll();
        for (int i = 0; i < numbers.size(); i++) {
            final int index = i;
            JButton numBtn = new JButton(String.valueOf(numbers.get(i)));
            numBtn.setPreferredSize(new Dimension(80, 80));
            numBtn.setFont(new Font("Monospaced", Font.BOLD, 24));
            
            // Simple swap logic for the dummy game
            numBtn.addActionListener(e -> {
                if (index < numbers.size() - 1) {
                    Collections.swap(numbers, index, index + 1);
                    refreshUI();
                } else {
                    Collections.swap(numbers, index, 0);
                    refreshUI();
                }
            });
            
            numbersPanel.add(numBtn);
        }
        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private void verifySort() {
        boolean sorted = true;
        for (int i = 0; i < numbers.size() - 1; i++) {
            if (numbers.get(i) > numbers.get(i + 1)) {
                sorted = false;
                break;
            }
        }

        if (sorted) {
            JOptionPane.showMessageDialog(this, "Correct! You've uncovered the clue: " + clueToAward);
            GameState.getInstance().addClue(clueToAward);
            MainGame.getInstance().switchFloor(returnFloor);
        } else {
            JOptionPane.showMessageDialog(this, "The sequence is still out of order. Try swapping numbers!");
        }
    }
}
