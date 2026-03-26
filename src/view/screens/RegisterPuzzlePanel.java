package view.screens;

import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class RegisterPuzzlePanel extends JPanel {

    private static final Color PANEL_BG = new Color(28, 32, 40);
    private static final Color PANEL_BORDER = new Color(70, 85, 110);
    private static final Color TEXT_COLOR = new Color(220, 230, 245);

    private final int[] sequence = {7, 12, 3, 19, 5, 14, 8, 2, 10, 6};
    private final int TARGET_NUMBER = 19;
    private Consumer<Boolean> onComplete;

    public RegisterPuzzlePanel(Consumer<Boolean> onComplete) {
        this.onComplete = onComplete;
        setLayout(null);
        setBounds(UiScale.x(150), UiScale.y(100), UiScale.w(600), UiScale.h(400));
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createLineBorder(PANEL_BORDER, 3));
        setVisible(false);

        JLabel title = new JLabel("Click the Largest Number", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, UiScale.font(26)));
        title.setForeground(TEXT_COLOR);
        title.setBounds(0, UiScale.y(30), UiScale.w(600), UiScale.h(40));
        add(title);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, UiScale.s(15), UiScale.s(15)));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(UiScale.x(50), UiScale.y(100), UiScale.w(500), UiScale.h(200));

        for (int num : sequence) {
            JButton btn = createNumberButton(num);
            buttonPanel.add(btn);
        }

        add(buttonPanel);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(UiScale.x(250), UiScale.y(330), UiScale.w(100), UiScale.h(40));
        closeBtn.setFont(new Font("Serif", Font.BOLD, UiScale.font(16)));
        closeBtn.addActionListener(e -> setVisible(false));
        add(closeBtn);
    }

    private JButton createNumberButton(int num) {
        JButton btn = new JButton(String.valueOf(num));
        btn.setFont(new Font("Monospaced", Font.BOLD, UiScale.font(24)));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 55, 75));
        btn.setBorder(BorderFactory.createLineBorder(PANEL_BORDER, 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            setVisible(false);
            if (onComplete != null) {
                onComplete.accept(num == TARGET_NUMBER);
            }
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(65, 80, 110));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 55, 75));
            }
        });
        return btn;
    }

    public void startPuzzle() {
        setVisible(true);
    }
}
