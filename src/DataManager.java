import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataManager {

    private static final String DATA_FILE_NAME = "userdata.json";
    private static final File DATA_FILE = new File(System.getProperty("user.dir"), DATA_FILE_NAME);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, UserData> allUsersData;

    public DataManager() {
        initializeDataFile();
        this.allUsersData = readAllUsersData();
    }

    private void initializeDataFile() {
        if (!DATA_FILE.exists()) {
            try {
                DATA_FILE.createNewFile();
                try (FileWriter writer = new FileWriter(DATA_FILE)) {
                    GSON.toJson(new HashMap<String, UserData>(), writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, UserData> readAllUsersData() {
        try (Reader reader = new FileReader(DATA_FILE, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, UserData>>() {}.getType();
            Map<String, UserData> data = GSON.fromJson(reader, type);
            return data != null ? data : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveAllUsersData() {
        try (Writer writer = new FileWriter(DATA_FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(allUsersData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Konnte Passwort nicht hashen", e);
        }
    }

    public boolean registerUser(String username, String password) {
        if (allUsersData.containsKey(username)) {
            return false;
        }
        String hashedPassword = hashPassword(password);
        UserData newUserData = new UserData(hashedPassword);
        allUsersData.put(username, newUserData);
        saveAllUsersData();
        return true;
    }

    public boolean loginUser(String username, String password) {
        UserData userData = allUsersData.get(username);
        if (userData == null) {
            return false;
        }
        String hashedPassword = hashPassword(password);
        return Objects.equals(hashedPassword, userData.getHashedPassword());
    }

    public UserData getUserData(String username) {
        return allUsersData.get(username);
    }

    public boolean addFach(String username, String fachName) {
        UserData userData = getUserData(username);
        if (userData != null && !userData.getFaecher().containsKey(fachName)) {
            userData.getFaecher().put(fachName, new Fach(fachName));
            saveAllUsersData();
            return true;
        }
        return false;
    }

    public void addNote(String username, String fachName, double noteWert, double gewichtung) {
        UserData userData = getUserData(username);
        if (userData != null) {
            Fach fach = userData.getFaecher().get(fachName);
            if (fach != null) {
                fach.addNote(new Note(noteWert, gewichtung));
                saveAllUsersData();
            }
        }
    }

    public void removeNote(String username, String fachName, int noteIndex) {
        UserData userData = getUserData(username);
        if (userData != null) {
            Fach fach = userData.getFaecher().get(fachName);
            if (fach != null && fach.getNoten() != null && noteIndex >= 0 && noteIndex < fach.getNoten().size()) {
                fach.getNoten().remove(noteIndex);
                saveAllUsersData();
            }
        }
    }

    public double getGesamtschnitt(String username) {
        UserData userData = getUserData(username);
        if (userData == null || userData.getFaecher().isEmpty()) {
            return 0.0;
        }

        double totalGewichteteSumme = 0;
        double totalGewichtung = 0;

        for (Fach fach : userData.getFaecher().values()) {
            if (fach.getNoten() != null && !fach.getNoten().isEmpty()) {
                totalGewichteteSumme += fach.getSchnitt();
                totalGewichtung += 1;
            }
        }

        return totalGewichtung > 0 ? totalGewichteteSumme / totalGewichtung : 0.0;
    }
}
