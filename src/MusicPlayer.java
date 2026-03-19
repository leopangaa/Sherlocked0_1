import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {
    private Clip clip;
    private FloatControl gainControl;
    private String currentPath;

    public synchronized void playLoop(String path) {
        if (path == null || path.isBlank()) return;
        if (path.equals(currentPath) && clip != null && clip.isRunning()) return;
        stop();
        currentPath = path;

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(path));
            Clip newClip = AudioSystem.getClip();
            newClip.open(stream);
            clip = newClip;

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            } else {
                gainControl = null;
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            currentPath = null;
            clip = null;
            gainControl = null;
        }
    }

    public synchronized void stop() {
        if (clip != null) {
            try {
                clip.stop();
                clip.close();
            } catch (Exception ignored) {
            }
        }
        clip = null;
        gainControl = null;
        currentPath = null;
    }

    public synchronized void setVolume(float volume01) {
        if (gainControl == null) return;
        float v = Math.max(0f, Math.min(1f, volume01));
        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();
        float db = min + (max - min) * v;
        gainControl.setValue(db);
    }

    public synchronized boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}

