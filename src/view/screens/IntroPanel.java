package view.screens;

import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IntroPanel extends JPanel {

    private final String[] story = {
            "It started with a call.",
            "Another case.",
            "Another body.",
            "Another night that refuses to end.",
            "The hotel stands quiet now.",
            "Too quiet.",
            "They said it was just a routine investigation.",
            "A man found dead in Room 217.",
            "No signs of forced entry.",
            "No witnesses.",
            "No clear motive.",
            "Just... silence.",
            "I’ve handled cases like this before.",
            "But something feels different.",
            "Like I’ve been here already.",
            "Like I know this place...",
            "better than I should.",
            "The moment I stepped inside—",
            "I felt it.",
            "This place...",
            "is trying to tell me something."
    };

    private int currentLine = 0;
    private int charIndex = 0;
    private boolean isTyping = false;
    private final Timer typewriterTimer;
    private String displayedText = "";
    private boolean introFinished = false;

    private boolean skipConfirming = false;
    private final Timer skipResetTimer;
    private final Rectangle skipBounds;

    public IntroPanel() {
        setLayout(null);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT));
        setFocusable(true);

        skipBounds = new Rectangle(
                UiScale.GAME_WIDTH - UiScale.w(150),
                UiScale.GAME_HEIGHT - UiScale.h(60),
                UiScale.w(120),
                UiScale.h(40)
        );

        skipResetTimer = new Timer(3000, e -> {
            skipConfirming = false;
            repaint();
        });
        skipResetTimer.setRepeats(false);

        typewriterTimer = new Timer(40, e -> {
            if (charIndex < story[currentLine].length()) {
                displayedText += story[currentLine].charAt(charIndex);
                charIndex++;
                repaint();
            } else {
                stopTyping();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (skipBounds.contains(e.getPoint())) {
                    handleSkip();
                } else {
                    handleInput();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput();
            }
        });
    }

    public void startIntro() {
        currentLine = 0;
        charIndex = 0;
        introFinished = false;
        
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
        displayedText = story[currentLine];
        repaint();
    }

    private void handleSkip() {
        if (introFinished) return;

        if (!skipConfirming) {
            skipConfirming = true;
            skipResetTimer.restart();
            repaint();
        } else {
            skipResetTimer.stop();
            skipConfirming = false;
            MainGame.getInstance().switchFloor("LOBBY");
        }
    }

    private void handleInput() {
        if (introFinished) {
            MainGame.getInstance().switchFloor("LOBBY");
            return;
        }

        if (isTyping) {
            stopTyping();
        } else {
            currentLine++;
            if (currentLine < story.length) {
                startTyping();
            } else {
                introFinished = true;
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
        g2d.setFont(new Font("Serif", Font.PLAIN, UiScale.font(24)));

        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(displayedText)) / 2;
        int y = getHeight() / 2 - UiScale.y(20);

        g2d.drawString(displayedText, x, y);

        if (introFinished) {
            g2d.setFont(new Font("Serif", Font.ITALIC, UiScale.font(16)));
            String continueText = "[Press any key to continue...]";
            int cx = (getWidth() - g2d.getFontMetrics().stringWidth(continueText)) / 2;
            int cy = y + UiScale.y(60);
            g2d.drawString(continueText, cx, cy);
        } else {
            // SKIP BUTTON
            g2d.setFont(new Font("Serif", Font.PLAIN, UiScale.font(18)));
            String skipText = skipConfirming ? "Are you sure?" : "Skip >>";
            g2d.setColor(skipConfirming ? Color.RED : new Color(245, 235, 220));
            
            FontMetrics sfm = g2d.getFontMetrics();
            int sx = skipBounds.x + (skipBounds.width - sfm.stringWidth(skipText)) / 2;
            int sy = skipBounds.y + (skipBounds.height - sfm.getHeight()) / 2 + sfm.getAscent();
            
            g2d.drawString(skipText, sx, sy);
            
            // Optional: border for click area debug/visual
            // g2d.drawRect(skipBounds.x, skipBounds.y, skipBounds.width, skipBounds.height);
        }
    }
}
