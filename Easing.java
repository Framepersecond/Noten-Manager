public class Easing {
    public static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public static float easeInOutCubic(float t) {
        t = clamp01(t);
        return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3f) / 2f;
    }

    public static float easeOutCubic(float t) {
        t = clamp01(t);
        return 1f - (float) Math.pow(1f - t, 3);
    }

    public static float easeOutBack(float t) {
        t = clamp01(t);
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
    }
}
