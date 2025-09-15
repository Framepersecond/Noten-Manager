import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class Login {

    private static final String USERS_FILE_NAME = "users.json";
    private static final File DATA_FOLDER = new File(System.getProperty("user.dir"));
    private static final File USERS_FILE = new File(DATA_FOLDER, USERS_FILE_NAME);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void initializeUsersFile() {
        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs();
        }
        if (!USERS_FILE.exists()) {
            try {
                USERS_FILE.createNewFile();
                try (FileWriter writer = new FileWriter(USERS_FILE)) {
                    GSON.toJson(new HashMap<String, String>(), writer);
                }
                System.out.println("Datei 'users.json' wurde erstellt.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Fehler: Konnte die Datei 'users.json' nicht erstellen!");
            }
        }
    }

    private static Map<String, String> readUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>() {};
            Map<String, String> users = GSON.fromJson(reader, typeToken.getType());
            return users != null ? users : new HashMap<>();
        } catch (FileNotFoundException e) {
            System.err.println("Fehler: Datei 'users.json' nicht gefunden. Erstelle eine neue.");
            initializeUsersFile();
            return new HashMap<>();
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Benutzerdaten aus 'users.json': " + e.getMessage());
            return new HashMap<>();
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Fehler beim Parsen der JSON-Datei 'users.json'. Die Datei ist möglicherweise beschädigt: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private static void writeUsersToFile(Map<String, String> users) {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            GSON.toJson(users, writer);
            System.out.println("Benutzerdaten wurden in 'users.json' gespeichert.");
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben der Benutzerdaten in 'users.json': " + e.getMessage());
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Fehler beim Hashing des Passworts: " + e.getMessage());
            return null;
        }
    }

    public static void registerUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Benutzerregistrierung ---");
        System.out.print("Geben Sie einen neuen Nutzernamen ein: ");
        String username = scanner.next();

        Map<String, String> users = readUsersFromFile();

        if (users.containsKey(username)) {
            System.out.println("Fehler: Der Nutzername '" + username + "' existiert bereits. Bitte wählen Sie einen anderen.");
            return;
        }

        System.out.print("Geben Sie ein Passwort ein: ");
        String password = scanner.next();

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            System.err.println("Registrierung fehlgeschlagen aufgrund eines Hashing-Fehlers.");
            return;
        }

        users.put(username, hashedPassword);
        writeUsersToFile(users);
        System.out.println("Benutzer '" + username + "' erfolgreich registriert!");
    }

    public static void loginUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Benutzeranmeldung ---");
        System.out.print("Geben Sie Ihren Nutzernamen ein: ");
        String username = scanner.next();
        System.out.print("Geben Sie Ihr Passwort ein: ");
        String password = scanner.next();

        Map<String, String> users = readUsersFromFile();

        if (!users.containsKey(username)) {
            System.out.println("Fehler: Falscher Nutzername oder Passwort.");
            return;
        }

        String storedHashedPassword = users.get(username);
        String enteredHashedPassword = hashPassword(password);

        if (enteredHashedPassword == null || !Objects.equals(enteredHashedPassword, storedHashedPassword)) {
            System.out.println("Fehler: Falscher Nutzername oder Passwort.");
        } else {
            System.out.println("Willkommen, " + username + "! Sie sind erfolgreich angemeldet.");
        }
    }
}
