import java.util.*;

public class TemplateDatabase {

    public static java.util.List<String> getSubjects(String countryCode, String language) {
        // Einfache, sprach-sensible Defaults; bei Unbekannt -> generische Liste (engl./de.)
        String lang = language == null ? "en" : language.toLowerCase(Locale.ROOT);
        boolean german = lang.startsWith("de");
        boolean english = lang.startsWith("en");

        Map<String, List<String>> mapDE = new HashMap<>();
        mapDE.put("DE", Arrays.asList("Mathematik","Deutsch","Biologie","Chemie","Physik","Geschichte","Geographie","Informatik","Kunst","Musik","Sport","Englisch","Französisch","Politik","Religion","Ethik"));
        mapDE.put("AT", Arrays.asList("Mathematik","Deutsch","Biologie","Chemie","Physik","Geschichte und Sozialkunde","Geographie und Wirtschaftskunde","Informatik","Kunst","Musik","Bewegung und Sport","Englisch","Französisch"));
        mapDE.put("CH", Arrays.asList("Mathematik","Deutsch","Biologie","Chemie","Physik","Geschichte","Geografie","Informatik","Bildnerisches Gestalten","Musik","Sport","Englisch","Französisch","Italienisch"));

        Map<String, List<String>> mapEN = new HashMap<>();
        mapEN.put("US", Arrays.asList("Mathematics","English","Biology","Chemistry","Physics","History","Geography","Computer Science","Art","Music","Physical Education","Spanish","French","Economics","Government"));
        mapEN.put("GB", Arrays.asList("Mathematics","English","Biology","Chemistry","Physics","History","Geography","Computer Science","Art","Music","Physical Education","Spanish","French","German"));
        mapEN.put("CA", Arrays.asList("Mathematics","English","Biology","Chemistry","Physics","History","Geography","Computer Science","Art","Music","Physical Education","French","Spanish"));

        List<String> res = (german ? mapDE.get(countryCode) : mapEN.get(countryCode));
        if (res != null) return res;

        // generische Fallbacks
        if (german) {
            return Arrays.asList("Mathematik","Deutsch","Biologie","Chemie","Physik","Geschichte","Geographie","Informatik","Kunst","Musik","Sport","Englisch");
        } else {
            return Arrays.asList("Mathematics","Language Arts","Biology","Chemistry","Physics","History","Geography","Computer Science","Art","Music","Physical Education","English");
        }
    }

    public static java.util.List<String> getGradeTemplates(String countryCode, String language) {
        String lang = language == null ? "en" : language.toLowerCase(Locale.ROOT);
        boolean german = lang.startsWith("de");

        if (german) {
            return Arrays.asList("Hausaufgabe","Kurztest","Klassenarbeit","Klausur","Projekt","Referat","Praktikum","Mündlich");
        } else {
            return Arrays.asList("Homework","Quiz","Test","Exam","Project","Presentation","Lab","Oral");
        }
    }

    public static String[] letterChoices() {
        return new String[] {"A+","A","A-","B+","B","B-","C+","C","C-","D+","D","D-","E","F"};
    }
}
