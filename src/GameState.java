import java.util.ArrayList;

public class GameState {
    private static GameState instance;
    
    public int currentFloor;
    public ArrayList<String> clues;
    
    // Floor completion status
    public boolean lobbyComplete;
    public boolean floor1Complete;
    public boolean floor2Complete;
    public boolean floor3Complete;
    public boolean floor4Complete;
    public boolean floor5Complete;
    public boolean floor6Complete;
    public boolean rooftopComplete;

    private GameState() {
        currentFloor = 0; // 0 = Lobby
        clues = new ArrayList<>();
        lobbyComplete = false;
        floor1Complete = false;
        floor2Complete = false;
        floor3Complete = false;
        floor4Complete = false;
        floor5Complete = false;
        floor6Complete = false;
        rooftopComplete = false;
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void addClue(String clue) {
        if (!clues.contains(clue)) {
            clues.add(clue);
            System.out.println("Clue found: " + clue);
            
            // Check if lobby is complete after finding enough clues
            if (clues.contains("Mysterious note") && 
                clues.contains("Dr. Kells photo") && 
                clues.contains("Guest Register entry") && 
                clues.contains("Mirror reflection hint")) {
                lobbyComplete = true;
            }
        }
    }

    public boolean hasClue(String clue) {
        return clues.contains(clue);
    }
}
