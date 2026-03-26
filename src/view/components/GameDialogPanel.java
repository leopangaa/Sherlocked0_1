package view.components;

import utils.Assets;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameDialogPanel extends JPanel {

    private JLabel backgroundLabel;
    private JLabel titleLabel;
    private JTextArea messageArea;
    private JPanel buttonPanel;
    private JPanel contentPanel;
    private final List<JButton> buttons = new ArrayList<>();
    
    private int lastOffsetX, lastOffsetY;
    private int currentPanelW = UiScale.s(500);
    private int currentPanelH = UiScale.s(400);

    public GameDialogPanel() {
        setLayout(null);
        setOpaque(false);
        setVisible(false);

        // Block mouse events to layers below
        addMouseListener(new java.awt.event.MouseAdapter() {});
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw the semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void setDialogOffset(int offsetX, int offsetY) {
        this.lastOffsetX = offsetX;
        this.lastOffsetY = offsetY;
        rebuildPanel();
    }

    public void setPanelSize(int w, int h) {
        this.currentPanelW = w;
        this.currentPanelH = h;
        rebuildPanel();
    }

    private void rebuildPanel() {
        removeAll();

        int panelX = lastOffsetX + (UiScale.GAME_WIDTH - currentPanelW) / 2;
        int panelY = lastOffsetY + (UiScale.GAME_HEIGHT - currentPanelH) / 2;

        contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        contentPanel.setBounds(panelX, panelY, currentPanelW, currentPanelH);
        add(contentPanel);

        // Background
        ImageIcon bgIcon = new ImageIcon(Assets.img("main-dialog.png"));
        Image bgImg = bgIcon.getImage().getScaledInstance(currentPanelW, currentPanelH, Image.SCALE_SMOOTH);
        backgroundLabel = new JLabel(new ImageIcon(bgImg));
        backgroundLabel.setBounds(0, 0, currentPanelW, currentPanelH);
        contentPanel.add(backgroundLabel);

        // Title - Black Color
        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(UiScale.s(50), UiScale.s(40), currentPanelW - UiScale.s(100), UiScale.s(40));
        contentPanel.add(titleLabel);
        contentPanel.setComponentZOrder(titleLabel, 0);

        // Message - Black Color
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);
        messageArea.setFont(new Font("Serif", Font.PLAIN, UiScale.font(18)));
        messageArea.setForeground(Color.BLACK);
        messageArea.setBounds(UiScale.s(60), UiScale.s(100), currentPanelW - UiScale.s(120), currentPanelH - UiScale.s(200));
        contentPanel.add(messageArea);
        contentPanel.setComponentZOrder(messageArea, 0);

        // Buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UiScale.s(20), 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(UiScale.s(50), currentPanelH - UiScale.s(80), currentPanelW - UiScale.s(100), UiScale.s(50));
        contentPanel.add(buttonPanel);
        contentPanel.setComponentZOrder(buttonPanel, 0);

        revalidate();
        repaint();
    }

    public void showMessage(String title, String message, Runnable onOk) {
        setPanelSize(UiScale.s(500), UiScale.s(300)); // Standard message size
        reset();
        titleLabel.setText(title);
        messageArea.setText(message);
        addButton("OK", e -> {
            setVisible(false);
            if (onOk != null) onOk.run();
        });
        showDialog();
    }

    public void showConfirm(String title, String message, Consumer<Boolean> onResult) {
        setPanelSize(UiScale.s(500), UiScale.s(300)); // Standard confirm size
        reset();
        titleLabel.setText(title);
        messageArea.setText(message);
        addButton("YES", e -> {
            setVisible(false);
            if (onResult != null) onResult.accept(true);
        });
        addButton("NO", e -> {
            setVisible(false);
            if (onResult != null) onResult.accept(false);
        });
        showDialog();
    }

    public void showCustom(String title, JPanel customContent, List<String> buttonLabels, Consumer<Integer> onAction) {
        // Size can be adjusted before calling showCustom or we set a default larger one
        reset();
        titleLabel.setText(title);
        messageArea.setVisible(false);

        int insetX = UiScale.s(60);
        int insetY = UiScale.s(90);
        int boxW = currentPanelW - (insetX * 2);
        int boxH = currentPanelH - UiScale.s(180);
        customContent.setBounds(insetX, insetY, boxW, boxH);
        contentPanel.add(customContent);
        contentPanel.setComponentZOrder(customContent, 0);

        for (int i = 0; i < buttonLabels.size(); i++) {
            final int index = i;
            addButton(buttonLabels.get(i), e -> {
                setVisible(false);
                if (onAction != null) onAction.accept(index);
            });
        }
        showDialog();
    }

    private void showDialog() {
        setVisible(true);
        if (getParent() != null) {
            getParent().setComponentZOrder(this, 0);
        }
        repaint();
    }

    private void addButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Serif", Font.BOLD, UiScale.font(16)));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(200, 190, 170));
        btn.setForeground(new Color(30, 25, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(UiScale.s(120), UiScale.s(40)));
        btn.addActionListener(listener);
        buttonPanel.add(btn);
        buttons.add(btn);
    }

    private void reset() {
        if (titleLabel == null) rebuildPanel();
        titleLabel.setText("");
        messageArea.setText("");
        messageArea.setVisible(true);
        buttonPanel.removeAll();
        buttons.clear();
        
        Component[] components = contentPanel.getComponents();
        for (Component c : components) {
            if (c != backgroundLabel && c != titleLabel && c != messageArea && c != buttonPanel) {
                contentPanel.remove(c);
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
