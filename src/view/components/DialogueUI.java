package view.components;

import utils.Assets;
import utils.UiScale;
import utils.TTSManager;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DialogueUI extends JPanel {

    private static final Color PANEL_BG = new Color(25, 30, 44);
    private static final Color PANEL_BORDER = new Color(90, 100, 125);
    private static final String TALK_BTN_PATH = Assets.img("talkButton.png");
    private static final String EXAMINE_BTN_PATH = Assets.img("examineButton.png");

    private JTextArea dialogueBox;
    private JButton talkBtn, examineBtn;
    private Timer typewriterTimer;
    private Timer hideTimer;
    private String currentFullText = "";
    private int charIndex = 0;
    private String[] currentDialogueQueue;
    private int dialogueQueueIndex = 0;
    private Runnable onDialogueComplete;
    private Consumer<Boolean> visibilityListener;
    private boolean isInteractionMenuOpen = false;

    public DialogueUI() {
        setLayout(null);
        setBounds(UiScale.x(50), UiScale.y(420), UiScale.w(800), UiScale.h(140));
        setOpaque(true);
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createLineBorder(PANEL_BORDER, Math.max(1, UiScale.s(2))));
        setDoubleBuffered(true);
        setVisible(false);

        // Text Area
        dialogueBox = new JTextArea();
        dialogueBox.setEditable(false);
        dialogueBox.setLineWrap(true);
        dialogueBox.setWrapStyleWord(true);
        dialogueBox.setFont(new Font("Serif", Font.PLAIN, UiScale.font(18)));
        dialogueBox.setBackground(PANEL_BG);
        dialogueBox.setForeground(Color.WHITE);
        dialogueBox.setOpaque(true);
        dialogueBox.setFocusable(false);
        dialogueBox.setHighlighter(null);
        dialogueBox.setSelectionColor(new Color(0, 0, 0, 0));
        dialogueBox.setCaretColor(new Color(0, 0, 0, 0));
        dialogueBox.setBorder(null);

        dialogueBox.setBounds(UiScale.x(20), UiScale.y(15), UiScale.w(500), UiScale.h(110));
        add(dialogueBox);

        // Buttons (icon-based)
        talkBtn = createIconButton(TALK_BTN_PATH, UiScale.x(540), 20, UiScale.w(230), UiScale.h(45));
        examineBtn = createIconButton(EXAMINE_BTN_PATH, UiScale.x(540), 75, UiScale.w(230), UiScale.h(45));
        
        talkBtn.setVisible(false);
        examineBtn.setVisible(false);

        add(talkBtn);
        add(examineBtn);
        setComponentZOrder(dialogueBox, 2);
        setComponentZOrder(talkBtn, 0);
        setComponentZOrder(examineBtn, 0);

        java.awt.event.MouseAdapter advanceClick = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleAdvanceClick();
            }
        };
        addMouseListener(advanceClick);
        dialogueBox.addMouseListener(advanceClick);
    }

    public void setVisibilityListener(Consumer<Boolean> listener) {
        visibilityListener = listener;
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (!aFlag) {
            TTSManager.stop();
        }
        boolean old = isVisible();
        super.setVisible(aFlag);
        if (visibilityListener != null && old != aFlag) {
            visibilityListener.accept(aFlag);
        }
    }

    private void refreshNow() {
        revalidate();
        repaint();
    }

    private void hideInteractionButtons() {
        TTSManager.stop();
        Rectangle talkBounds = talkBtn.getBounds();
        Rectangle examineBounds = examineBtn.getBounds();
        talkBtn.setVisible(false);
        examineBtn.setVisible(false);
        repaint(talkBounds);
        repaint(examineBounds);
        refreshNow();
    }

    private void handleAdvanceClick() {
        TTSManager.stop();
        if (isInteractionMenuOpen) {
            // If interaction menu is open, clicks on the dialogue box itself should not advance/close it.
            return;
        }

        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
            dialogueBox.setText(currentFullText);
            return;
        }
        showNextDialogue();
    }

    private JButton createIconButton(String path, int x, int y, int w, int h) {
        ImageIcon normal = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        ImageIcon hover = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance((int)(w*1.06), (int)(h*1.06), Image.SCALE_SMOOTH));
        JButton btn = new JButton(normal);
        btn.setBounds(x, UiScale.y(y), normal.getIconWidth(), normal.getIconHeight());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Rectangle orig = btn.getBounds();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.isEnabled()) return;
                btn.setIcon(hover);
                int dx = (hover.getIconWidth() - orig.width)/2;
                int dy = (hover.getIconHeight() - orig.height)/2;
                btn.setBounds(orig.x - dx, orig.y - dy, hover.getIconWidth(), hover.getIconHeight());
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setIcon(normal);
                btn.setBounds(orig);
                btn.repaint();
            }
        });
        return btn;
    }

    public void startDialogue(String[] dialogue) {
        startDialogue(dialogue, null);
    }

    public void startDialogue(String[] dialogue, Runnable onComplete) {
        TTSManager.stop();
        isInteractionMenuOpen = false;
        setVisible(true);
        hideInteractionButtons();
        if (hideTimer != null && hideTimer.isRunning()) {
            hideTimer.stop();
        }
        currentDialogueQueue = dialogue;
        dialogueQueueIndex = 0;
        onDialogueComplete = onComplete;
        showNextDialogue();
    }

    private void showNextDialogue() {
        TTSManager.stop();
        if (currentDialogueQueue != null && dialogueQueueIndex < currentDialogueQueue.length) {
            typeText(currentDialogueQueue[dialogueQueueIndex]);
            dialogueQueueIndex++;
        } else {
            setVisible(false);
            currentDialogueQueue = null;
            hideInteractionButtons();
            Runnable cb = onDialogueComplete;
            onDialogueComplete = null;
            if (cb != null) {
                SwingUtilities.invokeLater(cb);
            }
        }
    }

    public void typeText(String text) {
        typeText(text, true, true);
    }

    public void typeText(String text, boolean autoHide) {
        typeText(text, autoHide, true);
    }

    public void typeText(String text, boolean autoHide, boolean allowSpeech) {
        if (!isVisible()) setVisible(true);
        hideInteractionButtons();
        
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
        }
        if (hideTimer != null && hideTimer.isRunning()) {
            hideTimer.stop();
        }

        currentFullText = text;
        charIndex = 0;
        dialogueBox.setText("");

        if (allowSpeech) {
            String speechText = cleanForSpeech(text);
            TTSManager.speak(speechText);
        }

        typewriterTimer = new Timer(25, e -> {
            if (charIndex < currentFullText.length()) {
                dialogueBox.append(String.valueOf(currentFullText.charAt(charIndex)));
                charIndex++;
            } else {
                ((Timer) e.getSource()).stop();
                if (autoHide && currentDialogueQueue == null) {
                    hideTimer = new Timer(3000, h -> setVisible(false));
                    hideTimer.setRepeats(false);
                    hideTimer.start();
                }
            }
        });
        typewriterTimer.start();
    }

    private String cleanForSpeech(String text) {
        if (text == null) return "";

        String speechText = text;
        // Strip NPC name (everything before the first colon)
        int colonIndex = speechText.indexOf(":");
        if (colonIndex != -1 && colonIndex < 25) { // Threshold to avoid stripping if colon is far in (like a URL or time)
            speechText = speechText.substring(colonIndex + 1).trim();
        }

        return speechText
                .replace("...", ". ")
                .replace("—", ", ")
                .replace("-", " ")
                .trim();
    }

    public void showInteractionMenu(String title, Runnable talkAction, Runnable examineAction) {
        isInteractionMenuOpen = true;
        setVisible(true);
        typeText("Interacting with: " + title, false, false);
        
        // Reset listeners
        for (java.awt.event.ActionListener al : talkBtn.getActionListeners()) talkBtn.removeActionListener(al);
        for (java.awt.event.ActionListener al : examineBtn.getActionListeners()) examineBtn.removeActionListener(al);

        if (talkAction != null) {
            talkBtn.setVisible(true);
            talkBtn.addActionListener(e -> {
                TTSManager.stop();
                isInteractionMenuOpen = false; // Close menu on button click
                talkBtn.setVisible(false);
                examineBtn.setVisible(false);
                talkAction.run();
                refreshNow();
            });
        } else {
            talkBtn.setVisible(false);
        }

        if (examineAction != null) {
            examineBtn.setVisible(true);
            examineBtn.addActionListener(e -> {
                TTSManager.stop();
                isInteractionMenuOpen = false; // Close menu on button click
                talkBtn.setVisible(false);
                examineBtn.setVisible(false);
                examineAction.run();
                refreshNow();
            });
        } else {
            examineBtn.setVisible(false);
        }

        refreshNow();
    }
}
