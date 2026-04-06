package view.screens;

import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OutroPanel extends JPanel {

    private final String[] narration = {
            "So it wasn’t just a case...",
            "These records... they all point to me.",
            "I’ve been here before.",
            "...haven’t I?",
            "I thought I was searching for the truth.",
            "But every step I take...",
            "...feels like I’m getting closer to something I don’t want to remember.",
            "The doors are opening...",
            "And I’m not sure if I’m ready to see what’s inside.",
            "To be continued..."
    };

    private int currentLine = 0;
    private int charIndex = 0;
    private boolean isTyping = false;
    private final Timer typewriterTimer;
    private String displayedText = "";
    private boolean outroFinished = false;

    public OutroPanel() {
        setLayout(null);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT));
        setFocusable(true);

        typewriterTimer = new Timer(50, e -> {
            if (charIndex < narration[currentLine].length()) {
                displayedText += narration[currentLine].charAt(charIndex);
                charIndex++;
                repaint();
            } else {
                stopTyping();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleInput();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput();
            }
        });
    }

    public void startOutro() {
        currentLine = 0;
        charIndex = 0;
        outroFinished = false;
        startTyping();
        requestFocusInWindow();
    }

    private void startTyping() {
        displayedText = "";
        charIndex = 0;
        isTyping = true;
        typewriterTimer.start();
    }

    private void stopTyping() {
        typewriterTimer.stop();
        isTyping = false;
        displayedText = narration[currentLine];
        repaint();
    }

    private void handleInput() {
        if (outroFinished) {
            MainGame.getInstance().showMenu();
            return;
        }

        if (isTyping) {
            stopTyping();
        } else {
            currentLine++;
            if (currentLine < narration.length) {
                startTyping();
            } else {
                outroFinished = true;
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(new Color(245, 235, 220)); // Off-white/light beige
        g2d.setFont(new Font("Serif", Font.ITALIC, UiScale.font(26)));

        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(displayedText)) / 2;
        int y = getHeight() / 2 - UiScale.y(20);

        g2d.drawString(displayedText, x, y);

        if (outroFinished) {
            g2d.setFont(new Font("Serif", Font.PLAIN, UiScale.font(16)));
            String continueText = "[Press any key to return to menu...]";
            int cx = (getWidth() - g2d.getFontMetrics().stringWidth(continueText)) / 2;
            int cy = y + UiScale.y(80);
            g2d.drawString(continueText, cx, cy);
        }
    }
}
