package core;

import java.util.ArrayList;

public class GameState {
    private static GameState instance;
    
    public int currentFloor;
    public ArrayList<String> clues;
    private ArrayList<Runnable> listeners;
    
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
        listeners = new ArrayList<>();
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

    public void addListener(Runnable listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Runnable r : new ArrayList<>(listeners)) {
            r.run();
        }
    }

    public void setCurrentFloor(int floor) {
        if (currentFloor != floor) {
            currentFloor = floor;
            notifyListeners();
        }
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
            notifyListeners();
        }
    }

    public boolean hasClue(String clue) {
        return clues.contains(clue);
    }
}
