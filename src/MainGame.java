import javax.swing.*;
import java.awt.*;

public class MainGame {

    JFrame frame;
    CardLayout cardLayout;
    JPanel container;

    public MainGame() {
        frame = new JFrame("Sherlocked");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        LobbyPanel lobby = new LobbyPanel();

        container.add(lobby, "LOBBY");

        frame.add(container);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainGame();
    }
}