package utils; 
 
import java.io.IOException; 
 
public class TTSManager { 
 
    private static Process currentProcess; 
    private static Thread speechThread; 
 
    public static void speak(String text) { 
        stop(); 
 
        if (text == null || text.trim().isEmpty()) return; 
 
        speechThread = new Thread(() -> { 
            String escapedText = text 
                    .replace("'", "''") 
                    .replace("\"", "`\""); 
 
            String command = 
                    "Add-Type -AssemblyName System.Speech; " + 
                    "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " + 
                    "$speak.Rate = 0; " + 
                    "$speak.Volume = 100; " + 
                    "$speak.Speak('" + escapedText + "');"; 
 
            ProcessBuilder pb = new ProcessBuilder( 
                    "powershell.exe", 
                    "-Command", 
                    command 
            ); 
 
            try { 
                currentProcess = pb.start(); 
                currentProcess.waitFor(); 
            } catch (IOException | InterruptedException e) { 
                e.printStackTrace(); 
            } 
        }); 
 
        speechThread.setDaemon(true); 
        speechThread.start(); 
    } 
 
    public static void stop() { 
        if (currentProcess != null && currentProcess.isAlive()) { 
            currentProcess.destroy(); 
        } 
        if (speechThread != null && speechThread.isAlive()) { 
            speechThread.interrupt(); 
        } 
    } 
} 
