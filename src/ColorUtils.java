import java.awt.*;
import java.awt.*;

public class ColorUtils {
    public static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed() * (1 - t) + b.getRed() * t);
        int g = (int) (a.getGreen() * (1 - t) + b.getGreen() * t);
        int bch = (int) (a.getBlue() * (1 - t) + b.getBlue() * t);
        return new Color(r, g, bch);
    }

    public static Color gradeToColor(double numericGrade) {
        // Erwartet 1.0 (sehr gut) -> 6.0 (ungenügend)
        float t = (float) ((numericGrade - 1.0) / 5.0);
        t = Math.max(0f, Math.min(1f, t));
        Color good = new Color(56, 142, 60);   // grün
        Color bad  = new Color(229, 57, 53);   // rot
        return lerp(good, bad, t);
    }

    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }
}
