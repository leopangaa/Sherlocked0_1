package core;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private static GameState instance;
    
    public int currentFloor;
    public ArrayList<String> clues;
    private transient ArrayList<Runnable> listeners;
    
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

    /**
     * Updates the singleton instance with data from a loaded state.
     * @param loadedState The state loaded from a file.
     */
    public void loadFrom(GameState loadedState) {
        if (loadedState == null) return;
        
        this.currentFloor = loadedState.currentFloor;
        this.clues = new ArrayList<>(loadedState.clues);
        this.lobbyComplete = loadedState.lobbyComplete;
        this.floor1Complete = loadedState.floor1Complete;
        this.floor2Complete = loadedState.floor2Complete;
        this.floor3Complete = loadedState.floor3Complete;
        this.floor4Complete = loadedState.floor4Complete;
        this.floor5Complete = loadedState.floor5Complete;
        this.floor6Complete = loadedState.floor6Complete;
        this.rooftopComplete = loadedState.rooftopComplete;
        
        notifyListeners();
    }

    public void addListener(Runnable listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(Runnable listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners() {
        if (listeners == null) return;
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
            
            // Floor 0 completion
            if (clues.contains("Mysterious note") && 
                clues.contains("Dr. Kells photo") && 
                clues.contains("Guest Register entry") && 
                clues.contains("Mirror reflection hint")) {
                lobbyComplete = true;
            }

            // Floor 1 completion
            if (clues.contains("Harper Testimony") &&
                clues.contains("Doyle Statement") &&
                clues.contains("Signs of Struggle") &&
                clues.contains("CCTV Footage Anomaly") &&
                clues.contains("Sealed Window")) {
                floor1Complete = true;
            }

            // Floor 2 completion
            if (clues.contains("Rina Testimony") &&
                clues.contains("Jared Statement") &&
                clues.contains("Hidden Journal") &&
                clues.contains("CCTV Footage Anomaly") &&
                clues.contains("Patient Record")) {
                floor2Complete = true;
            }

            notifyListeners();
        }
    }

    public boolean hasClue(String clue) {
        return clues.contains(clue);
    }
}
