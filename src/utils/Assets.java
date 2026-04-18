package utils;

import java.io.File;
import java.net.URISyntaxException;

public final class Assets {

    private Assets() {}

    // Get the folder where the jar/exe is located
    private static File baseDir() {
        try {
            File jarFile = new File(
                    Assets.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );

            return jarFile.isFile() ? jarFile.getParentFile() : jarFile;
        } catch (URISyntaxException e) {
            return new File(".");
        }
    }

    private static File assetsDir() {
        return new File(baseDir(), "assets");
    }

    public static String img(String name) {
        File root = new File(assetsDir(), "images");

        File[] candidates = {
                new File(root, "backgrounds/" + name),
                new File(root, "characters/" + name),
                new File(root, "ui/" + name),
                new File(root, name)
        };

        for (File f : candidates) {
            if (f.exists()) return f.getAbsolutePath();
        }

        // fallback (still return first path for debugging)
        return candidates[0].getAbsolutePath();
    }

    public static String audio(String name) {
        File f = new File(assetsDir(), "sounds/" + name);
        return f.getAbsolutePath();
    }
}