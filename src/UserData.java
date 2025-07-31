import java.util.HashMap;
import java.util.Map;

public class UserData {

    private String hashedPassword;
    private Map<String, Fach> faecher;

    public UserData(String hashedPassword) {
        this.hashedPassword = hashedPassword;
        this.faecher = new HashMap<>();
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Map<String, Fach> getFaecher() {
        return faecher;
    }
}
