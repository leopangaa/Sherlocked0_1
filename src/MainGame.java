import javax.swing.*;
import java.awt.*;

public class MainGame {

    static MainGame instance;
    JFrame frame;
    CardLayout cardLayout;
    JPanel container;
    CluePuzzlePanel puzzlePanel;

    public MainGame() {
        instance = this;
        frame = new JFrame("Sherlocked");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        LobbyPanel lobby = new LobbyPanel();
        Floor1Panel floor1 = new Floor1Panel();
        puzzlePanel = new CluePuzzlePanel();

        container.add(lobby, "LOBBY");
        container.add(floor1, "FLOOR1");
        container.add(puzzlePanel, "PUZZLE");

        frame.add(container);
        frame.setVisible(true);
    }

    public void openPuzzle(String clue, String returnTo) {
        puzzlePanel.startPuzzle(clue, returnTo);
        cardLayout.show(container, "PUZZLE");
    }

    public static MainGame getInstance() {
        return instance;
    }

    public void switchFloor(String floorName) {
        cardLayout.show(container, floorName);
    }

    public static void main(String[] args) {
        new MainGame();
    }
}