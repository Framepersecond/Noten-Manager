public class SettingsManager {
    public enum GradingScheme { NUMERIC_1_6, LETTER_A_F, PERCENT_0_100, POINTS_0_15 }

    private String countryCode = "DE";
    private String language = "de";
    private GradingScheme scheme = GradingScheme.NUMERIC_1_6;

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String code) { this.countryCode = code != null ? code : "DE"; }
    public String getLanguage() { return language; }
    public void setLanguage(String lang) { this.language = (lang == null || lang.isBlank()) ? "en" : lang; }
    public GradingScheme getScheme() { return scheme; }
    public void setScheme(GradingScheme s) { this.scheme = (s != null) ? s : GradingScheme.NUMERIC_1_6; }

    // Mapping A-F <-> 1-6
    public double letterToNumeric(String letter) {
        if (letter == null || letter.isBlank()) return 3.5; // neutral
        String l = letter.trim().toUpperCase();
        double base;
        switch (l.charAt(0)) {
            case 'A': base = 1.0; break;
            case 'B': base = 2.0; break;
            case 'C': base = 3.0; break;
            case 'D': base = 4.0; break;
            case 'E': base = 5.0; break;
            default:  base = 6.0; break; // F
        }
        if (l.length() > 1) {
            char mod = l.charAt(1);
            if (mod == '+') base -= 0.3;
            else if (mod == '-') base += 0.3;
        }
        return clamp1to6(base);
    }

    public String numericToLetter(double val) {
        val = clamp1to6(val);
        int idx = (int) Math.round(val - 1.0); // 0..5
        String[] letters = {"A","B","C","D","E","F"};
        String base = letters[Math.max(0, Math.min(letters.length-1, idx))];
        double frac = val - Math.floor(val);
        String mod = "";
        if (frac <= 0.2) mod = "+";
        else if (frac >= 0.8) mod = "-";
        return base + mod;
    }

    // Konvertierung verschiedener Skalen zur internen 1..6-Skala
    public double toNumeric1to6(double value) {
        switch (scheme) {
            case NUMERIC_1_6:
                return clamp1to6(value);
            case PERCENT_0_100:
                // 100% -> 1.0 (beste), 0% -> 6.0 (schlechteste)
                double t = 1.0 - (value / 100.0);
                return clamp1to6(1.0 + t * 5.0);
            case POINTS_0_15:
                // 15 -> 1.0 ; 0 -> 6.0 (linear)
                double inv = 1.0 - (value / 15.0);
                return clamp1to6(1.0 + inv * 5.0);
            case LETTER_A_F:
            default:
                return letterToNumeric(String.valueOf(value));
        }
    }

    public double fromNumeric1to6(double numeric) {
        numeric = clamp1to6(numeric);
        switch (scheme) {
            case NUMERIC_1_6:
                return numeric;
            case PERCENT_0_100:
                // invert Mapping
                double t = (numeric - 1.0) / 5.0; // 0..1
                return Math.round((1.0 - t) * 100.0);
            case POINTS_0_15:
                double ti = (numeric - 1.0) / 5.0;
                return Math.round((1.0 - ti) * 15.0);
            case LETTER_A_F:
            default:
                // nicht genutzt (UI nimmt Letter direkt)
                return numeric;
        }
    }

    private double clamp1to6(double v) {
        return Math.max(1.0, Math.min(6.0, v));
    }
}
