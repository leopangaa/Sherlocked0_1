package view.screens;

import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LiamQuestPanel extends JPanel {

    private static final Color PANEL_BG = new Color(32, 28, 24);
    private static final Color PANEL_BORDER = new Color(90, 80, 65);
    private static final Color TEXT_COLOR = new Color(245, 235, 220);

    private JLabel sequenceLabel;
    private JButton nextBtn, yesBtn;
    private final String[][] liamSequences = {
            {"2", "4", "5", "1"},
            {"2", "4", "1", "5"},
            {"2", "1", "4", "5"},
            {"1", "2", "4", "5"}
    };
    private int currentSequenceIndex = 0;
    private Consumer<Boolean> onComplete;

    public LiamQuestPanel(Consumer<Boolean> onComplete) {
        this.onComplete = onComplete;
        setLayout(null);
        setBounds(UiScale.x(200), UiScale.y(150), UiScale.w(500), UiScale.h(250));
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createLineBorder(PANEL_BORDER, 3));
        setVisible(false);

        JLabel title = new JLabel("Help Liam Remember", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        title.setForeground(TEXT_COLOR);
        title.setBounds(0, UiScale.y(20), UiScale.w(500), UiScale.h(40));
        add(title);

        sequenceLabel = new JLabel("", SwingConstants.CENTER);
        sequenceLabel.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(32)));
        sequenceLabel.setForeground(Color.WHITE);
        sequenceLabel.setBounds(0, UiScale.y(80), UiScale.w(500), UiScale.h(60));
        add(sequenceLabel);

        nextBtn = createStyledButton("NEXT", UiScale.x(80), UiScale.y(160), UiScale.w(150), UiScale.h(50));
        yesBtn = createStyledButton("YES", UiScale.x(270), UiScale.y(160), UiScale.w(150), UiScale.h(50));

        nextBtn.addActionListener(e -> handleNext());
        yesBtn.addActionListener(e -> handleYes());

        add(nextBtn);
        add(yesBtn);
    }

    public void startQuest() {
        currentSequenceIndex = 0;
        updateSequenceDisplay();
        setVisible(true);
    }

    private void updateSequenceDisplay() {
        String seqStr = "[" + String.join(", ", liamSequences[currentSequenceIndex]) + "]";
        sequenceLabel.setText(seqStr);
    }

    private void handleNext() {
        if (currentSequenceIndex < liamSequences.length - 1) {
            currentSequenceIndex++;
            updateSequenceDisplay();
        } else {
            // Reached the end without picking the correct one
            finish(false);
        }
    }

    private void handleYes() {
        // Correct sequence is the 4th one (index 3)
        finish(currentSequenceIndex == 3);
    }

    private void finish(boolean success) {
        setVisible(false);
        if (onComplete != null) {
            onComplete.accept(success);
        }
    }

    private JButton createStyledButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(new Color(60, 50, 40));
        btn.setBorder(BorderFactory.createLineBorder(PANEL_BORDER, 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(80, 70, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(60, 50, 40));
            }
        });
        return btn;
    }
}
