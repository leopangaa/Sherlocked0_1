package view.screens;

import main.MainGame;
import utils.Assets;
import utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {

    private final Image backgroundImage;

    public MainMenuPanel() {
        setLayout(null);

        backgroundImage = new ImageIcon(Assets.img("main-menu.png")).getImage();

        JPanel newGameHotspot = createHotspot(
                UiScale.x(350), UiScale.y(268), UiScale.w(200), UiScale.h(40), "_______________");
        JPanel continueHotspot = createHotspot(
                UiScale.x(350), UiScale.y(328), UiScale.w(200), UiScale.h(40), "______________");
        JPanel optionsHotspot = createHotspot(
                UiScale.x(350), UiScale.y(393), UiScale.w(200), UiScale.h(40), "____________");
        JPanel quitHotspot = createHotspot(
                UiScale.x(350), UiScale.y(455), UiScale.w(200), UiScale.h(40), "______");

        newGameHotspot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainGame.getInstance().startNewGame();
            }
        });

        continueHotspot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainGame.getInstance().continueGame();
            }
        });

        optionsHotspot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainGame.getInstance().openOptions();
            }
        });

        quitHotspot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainGame.getInstance().quitGame();
            }
        });

        add(newGameHotspot);
        add(continueHotspot);
        add(optionsHotspot);
        add(quitHotspot);
    }

    private JPanel createHotspot(int x, int y, int w, int h, String name) {

        JPanel wrapper = new JPanel(null);
        wrapper.setBounds(x, y, w, h);
        wrapper.setOpaque(false);
        wrapper.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // LEFT ARROW
        JLabel leftArrow = new JLabel("▶", SwingConstants.CENTER);
        leftArrow.setForeground(Color.RED);
        leftArrow.setFont(new Font("Serif", Font.BOLD, UiScale.font(22)));
        leftArrow.setBounds(-UiScale.s(30), 0, UiScale.s(30), h);
        leftArrow.setVisible(false);

        // TEXT (INVISIBLE BY DEFAULT)
        JLabel textLabel = new JLabel(name, SwingConstants.CENTER);
        textLabel.setBounds(0, 0, w, h);
        textLabel.setForeground(new Color(255, 0, 0, 0)); // fully transparent
        textLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(22)));

        // RIGHT ARROW
        JLabel rightArrow = new JLabel("◀", SwingConstants.CENTER);
        rightArrow.setForeground(Color.RED);
        rightArrow.setFont(new Font("Serif", Font.BOLD, UiScale.font(22)));
        rightArrow.setBounds(w, 0, UiScale.s(30), h);
        rightArrow.setVisible(false);

        MouseAdapter hoverAdapter = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                leftArrow.setVisible(true);
                rightArrow.setVisible(true);

                // STRONG RED APPEAR
                textLabel.setForeground(new Color(255, 50, 50)); // bright red
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), wrapper);

                if (!wrapper.contains(p)) {
                    leftArrow.setVisible(false);
                    rightArrow.setVisible(false);

                    // BACK TO INVISIBLE
                    textLabel.setForeground(new Color(255, 0, 0, 0));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                for (java.awt.event.MouseListener listener : wrapper.getMouseListeners()) {
                    if (listener != this) {
                        listener.mouseClicked(
                                SwingUtilities.convertMouseEvent(e.getComponent(), e, wrapper));
                    }
                }
            }
        };

        wrapper.addMouseListener(hoverAdapter);
        textLabel.addMouseListener(hoverAdapter);
        leftArrow.addMouseListener(hoverAdapter);
        rightArrow.addMouseListener(hoverAdapter);

        wrapper.add(leftArrow);
        wrapper.add(textLabel);
        wrapper.add(rightArrow);

        return wrapper;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}