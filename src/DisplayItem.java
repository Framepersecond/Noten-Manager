public class DisplayItem {
    public final String code;
    public final String display;

    public DisplayItem(String code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
