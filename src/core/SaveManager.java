package core;

import java.io.*;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.dat";

    /**
     * Saves the current game state to a file.
     */
    public static boolean saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(GameState.getInstance());
            System.out.println("Game saved successfully to " + SAVE_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the game state from a file and updates the current instance.
     */
    public static boolean loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("No save file found.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            GameState loadedState = (GameState) ois.readObject();
            GameState.getInstance().loadFrom(loadedState);
            System.out.println("Game loaded successfully from " + SAVE_FILE);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a save file exists.
     */
    public static boolean hasSaveFile() {
        return new File(SAVE_FILE).exists();
    }
}
