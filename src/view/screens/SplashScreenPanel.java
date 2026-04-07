package view.screens;

import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;

public class SplashScreenPanel extends JPanel {
    private JLabel displayLabel;
    private float alpha = 0f;
    private int state = 0; // 0: initial black, 1: INDEV fade in, 2: INDEV visible, 3: INDEV fade out, 4: SHERLOCKED fade in, 5: SHERLOCKED visible
    private Timer animTimer;
    private long stateStartTime;

    public SplashScreenPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        displayLabel = new JLabel("");
        displayLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(48)));
        displayLabel.setForeground(new Color(255, 255, 255, 0));
        add(displayLabel);
    }

    public void start() {
        state = 0;
        stateStartTime = System.currentTimeMillis();
        alpha = 0f;
        displayLabel.setText("");
        displayLabel.setForeground(new Color(255, 255, 255, 0));

        animTimer = new Timer(30, e -> update());
        animTimer.start();
    }

    private void update() {
        long elapsed = System.currentTimeMillis() - stateStartTime;

        switch (state) {
            case 0: // Initial black delay (2 seconds)
                if (elapsed >= 2000) {
                    state = 1;
                    stateStartTime = System.currentTimeMillis();
                    displayLabel.setText("FINAL PROJECT BY INDEV");
                    displayLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(48)));
                }
                break;
            case 1: // INDEV fade in (1 second)
                alpha = Math.min(1.0f, elapsed / 1000.0f);
                if (alpha >= 1.0f) {
                    state = 2;
                    stateStartTime = System.currentTimeMillis();
                }
                break;
            case 2: // INDEV visible (1.5 seconds)
                if (elapsed >= 1500) {
                    state = 3;
                    stateStartTime = System.currentTimeMillis();
                }
                break;
            case 3: // INDEV fade out (1 second)
                alpha = Math.max(0.0f, 1.0f - (elapsed / 1000.0f));
                if (alpha <= 0.0f) {
                    state = 4;
                    stateStartTime = System.currentTimeMillis();
                    displayLabel.setText("SHERLOCKED");
                    displayLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(72)));
                }
                break;
            case 4: // SHERLOCKED fade in (1 second)
                alpha = Math.min(1.0f, elapsed / 1000.0f);
                if (alpha >= 1.0f) {
                    state = 5;
                    stateStartTime = System.currentTimeMillis();
                }
                break;
            case 5: // SHERLOCKED visible (2 seconds)
                if (elapsed >= 2000) {
                    animTimer.stop();
                    MainGame.getInstance().showMenu();
                }
                break;
        }

        displayLabel.setForeground(new Color(255, 255, 255, (int)(alpha * 255)));
        repaint();
    }
}
