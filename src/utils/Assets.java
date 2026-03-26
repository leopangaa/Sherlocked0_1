package utils;

import java.io.File;

public final class Assets {
    private static final String ROOT = "src/assets";
    private static final String IMAGES = ROOT + "/images";
    private static final String SOUNDS = ROOT + "/sounds";

    private Assets() {
    }

    public static String img(String name) {
        String[] candidates = {
            IMAGES + "/backgrounds/" + name,
            IMAGES + "/characters/" + name,
            IMAGES + "/ui/" + name,
            IMAGES + "/" + name
        };
        for (String p : candidates) {
            if (new File(p).exists()) return p;
        }
        return candidates[0];
    }

    public static String audio(String name) {
        String p = SOUNDS + "/" + name;
        return new File(p).exists() ? p : p;
    }
}
