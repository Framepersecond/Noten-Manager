import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            NotenGUI gui = new NotenGUI();
            gui.setVisible(true);
        });
        Login.initializeUsersFile();

    }
}