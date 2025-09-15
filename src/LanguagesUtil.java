import java.util.*;
import java.util.stream.Collectors;

public class LanguagesUtil {

    public static java.util.List<DisplayItem> getAllCountries(String uiLanguage) {
        Locale ui = localeOf(uiLanguage, null);
        String[] codes = Locale.getISOCountries();
        java.util.List<DisplayItem> list = new ArrayList<>();
        for (String code : codes) {
            Locale l = new Locale("", code);
            String name = l.getDisplayCountry(ui);
            if (name != null && !name.isBlank()) {
                list.add(new DisplayItem(code, name));
            }
        }
        list.sort(Comparator.comparing(a -> a.display));
        return list;
    }

    public static java.util.List<DisplayItem> getAllLanguages(String uiLanguage) {
        Locale ui = localeOf(uiLanguage, null);
        String[] codes = Locale.getISOLanguages();
        Set<String> seen = new HashSet<>();
        java.util.List<DisplayItem> list = new ArrayList<>();
        for (String code : codes) {
            Locale l = new Locale(code);
            String name = l.getDisplayLanguage(ui);
            if (name != null && !name.isBlank()) {
                String key = name.toLowerCase(ui);
                if (seen.add(key)) {
                    list.add(new DisplayItem(code, capitalize(name, ui)));
                }
            }
        }
        list.sort(Comparator.comparing(a -> a.display));
        return list;
    }

    private static String capitalize(String s, Locale locale) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(locale) + s.substring(1);
    }

    public static Locale localeOf(String language, String country) {
        if (language == null || language.isBlank()) language = Locale.getDefault().getLanguage();
        if (country == null) return new Locale(language);
        return new Locale(language, country);
    }
}
