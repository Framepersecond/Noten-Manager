import java.util.ArrayList;
import java.util.List;

public class Fach {
    private String name;
    private List<Note> noten;

    public Fach(String name) {
        this.name = name;
        this.noten = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Note> getNoten() {
        return noten;
    }

    public void addNote(Note note) {
        this.noten.add(note);
    }

    public double getSchnitt() {
        if (noten == null || noten.isEmpty()) {
            return 0.0;
        }
        double totalGewichteteSumme = 0;
        double totalGewichtung = 0;
        for (Note note : noten) {
            totalGewichteteSumme += note.getWert() * note.getGewichtung();
            totalGewichtung += note.getGewichtung();
        }
        return totalGewichtung > 0 ? totalGewichteteSumme / totalGewichtung : 0.0;
    }
}
