public final class UiScale {
    public static final int BASE_WIDTH = 900;
    public static final int BASE_HEIGHT = 600;

    public static final int GAME_WIDTH = 1280;
    public static final int GAME_HEIGHT = 720;

    public static final double SCALE_X = GAME_WIDTH / (double) BASE_WIDTH;
    public static final double SCALE_Y = GAME_HEIGHT / (double) BASE_HEIGHT;
    public static final double SCALE_UNIFORM = Math.min(SCALE_X, SCALE_Y);

    private UiScale() {
    }

    public static int x(int baseX) {
        return (int) Math.round(baseX * SCALE_X);
    }

    public static int y(int baseY) {
        return (int) Math.round(baseY * SCALE_Y);
    }

    public static int w(int baseW) {
        return (int) Math.round(baseW * SCALE_X);
    }

    public static int h(int baseH) {
        return (int) Math.round(baseH * SCALE_Y);
    }

    public static int s(int baseSize) {
        return (int) Math.round(baseSize * SCALE_UNIFORM);
    }

    public static int font(int basePt) {
        return Math.max(10, (int) Math.round(basePt * SCALE_UNIFORM));
    }
}

