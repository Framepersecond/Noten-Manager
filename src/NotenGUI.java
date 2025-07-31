import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class NotenGUI extends JFrame {

    private static final Color DARK_BACKGROUND = new Color(43, 43, 43);
    private static final Color DARK_FOREGROUND = new Color(187, 187, 187);
    private static final Color DARK_PANEL_BACKGROUND = new Color(60, 63, 65);
    private static final Color DARK_ACCENT_COLOR = new Color(0, 122, 204);

    private static final Color LIGHT_BACKGROUND = new Color(242, 242, 242);
    private static final Color LIGHT_FOREGROUND = Color.BLACK;
    private static final Color LIGHT_PANEL_BACKGROUND = Color.WHITE;
    private static final Color LIGHT_ACCENT_COLOR = new Color(0, 122, 204);

    private boolean isDarkMode = false;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final DataManager dataManager = new DataManager();
    private String currentUsername;
    private JLabel gesamtschnittLabel;

    private JPanel loginPanel;
    private JPanel registerPanel;
    private JPanel gradesPanel;
    private JPanel faecherContainerPanel;


    public NotenGUI() {
        setTitle("Notenverwaltung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "login");

        registerPanel = createRegisterPanel();
        mainPanel.add(registerPanel, "register");

        add(mainPanel);
        updateTheme(); // Initiales Theme setzen
        cardLayout.show(mainPanel, "login");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Anmeldung", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Benutzername:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Passwort:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Anmelden");
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        JButton registerButton = new JButton("Noch kein Konto? Registrieren");
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        panel.add(registerButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (dataManager.loginUser(username, password)) {
                currentUsername = username;
                JOptionPane.showMessageDialog(this, "Willkommen, " + username + "!", "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
                setupAndShowGradesPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Falscher Nutzername oder Passwort.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Registrierung", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Neuer Benutzername:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Passwort:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("Registrieren");
        panel.add(registerButton, gbc);

        gbc.gridy = 4;
        JButton backToLoginButton = new JButton("Zurück zur Anmeldung");
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setContentAreaFilled(false);
        panel.add(backToLoginButton, gbc);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (dataManager.registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Benutzer '" + username + "' erfolgreich registriert!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "login");
            } else {
                JOptionPane.showMessageDialog(this, "Benutzername existiert bereits.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        backToLoginButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        return panel;
    }

    private void setupAndShowGradesPanel() {
        gradesPanel = new JPanel(new BorderLayout(10, 10));
        gradesPanel.setName("gradesPanel");

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Notenübersicht für " + currentUsername);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel eastHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        double gesamtschnitt = dataManager.getGesamtschnitt(currentUsername);
        this.gesamtschnittLabel = new JLabel(String.format("Gesamtschnitt: %.2f", gesamtschnitt));
        this.gesamtschnittLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JToggleButton themeToggle = new JToggleButton("Dark Mode");
        themeToggle.setSelected(isDarkMode);
        themeToggle.addActionListener(e -> {
            isDarkMode = themeToggle.isSelected();
            themeToggle.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            updateTheme();
        });

        eastHeaderPanel.add(this.gesamtschnittLabel);
        eastHeaderPanel.add(themeToggle);
        headerPanel.add(eastHeaderPanel, BorderLayout.EAST);
        gradesPanel.add(headerPanel, BorderLayout.NORTH);

        faecherContainerPanel = new JPanel();
        faecherContainerPanel.setLayout(new BoxLayout(faecherContainerPanel, BoxLayout.Y_AXIS));
        refreshFaecherPanel();

        JScrollPane scrollPane = new JScrollPane(faecherContainerPanel);
        scrollPane.setBorder(null);
        gradesPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addFachButton = new JButton("Neues Fach hinzufügen");
        JButton addNoteButton = new JButton("Note hinzufügen");
        footerPanel.add(addFachButton);
        footerPanel.add(addNoteButton);
        gradesPanel.add(footerPanel, BorderLayout.SOUTH);

        addFachButton.addActionListener(e -> addFachAction());
        addNoteButton.addActionListener(e -> addNoteAction());

        mainPanel.add(gradesPanel, "grades");
        updateTheme(); // Theme auf das neue Panel anwenden
        cardLayout.show(mainPanel, "grades");
    }

    private void refreshFaecherPanel() {
        if (faecherContainerPanel == null) {
            return;
        }
        faecherContainerPanel.removeAll();
        UserData userData = dataManager.getUserData(currentUsername);
        if (userData != null && userData.getFaecher() != null) {
            for (Fach fach : userData.getFaecher().values()) {
                JPanel fachPanel = new JPanel(new BorderLayout(10, 0));
                fachPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(isDarkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY, 1, true),
                        new EmptyBorder(10, 10, 10, 10)
                ));
                fachPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

                JLabel fachNameLabel = new JLabel(fach.getName());
                fachNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                fachPanel.add(fachNameLabel, BorderLayout.WEST);

                JLabel fachSchnittLabel = new JLabel(String.format("Schnitt: %.2f", fach.getSchnitt()));
                fachSchnittLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                fachPanel.add(fachSchnittLabel, BorderLayout.CENTER);

                JButton viewButton = new JButton("Noten anzeigen");
                viewButton.addActionListener(e -> showNotenForFach(fach));
                fachPanel.add(viewButton, BorderLayout.EAST);

                faecherContainerPanel.add(fachPanel);
                faecherContainerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        faecherContainerPanel.setBackground(isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND);

        faecherContainerPanel.revalidate();
        faecherContainerPanel.repaint();
        refreshGesamtschnitt();
    }

    private void showNotenForFach(Fach fach) {
        JDialog dialog = new JDialog(this, "Noten für " + fach.getName(), true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (fach.getNoten() != null && !fach.getNoten().isEmpty()) {
            for(Note note : fach.getNoten()) {
                listModel.addElement(String.format("Note: %.2f  (Gewichtung: %.2f)", note.getWert(), note.getGewichtung()));
            }
        } else {
            listModel.addElement("Noch keine Noten für dieses Fach eingetragen.");
        }

        JList<String> notenList = new JList<>(listModel);
        notenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notenList.setBorder(new EmptyBorder(10,10,10,10));
        dialog.add(new JScrollPane(notenList), BorderLayout.CENTER);

        JButton deleteButton = new JButton("Ausgewählte Note löschen");
        deleteButton.addActionListener(e -> {
            int selectedIndex = notenList.getSelectedIndex();
            if (selectedIndex != -1 && !listModel.get(selectedIndex).startsWith("Noch keine")) {
                int choice = JOptionPane.showConfirmDialog(dialog,
                        "Möchten Sie die ausgewählte Note wirklich löschen?",
                        "Bestätigung",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    dataManager.removeNote(currentUsername, fach.getName(), selectedIndex);
                    dialog.dispose();
                    refreshFaecherPanel();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Bitte wählen Sie eine zu löschende Note aus.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(deleteButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void refreshGesamtschnitt() {
        if (this.gesamtschnittLabel != null) {
            double gesamtschnitt = dataManager.getGesamtschnitt(currentUsername);
            this.gesamtschnittLabel.setText(String.format("Gesamtschnitt: %.2f", gesamtschnitt));
        }
    }

    private void addFachAction() {
        String fachName = JOptionPane.showInputDialog(this, "Geben Sie den Namen des neuen Fachs ein:", "Neues Fach", JOptionPane.PLAIN_MESSAGE);
        if (fachName != null && !fachName.trim().isEmpty()) {
            if (dataManager.addFach(currentUsername, fachName)) {
                JOptionPane.showMessageDialog(this, "Fach '" + fachName + "' wurde hinzugefügt.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                refreshFaecherPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Fach '" + fachName + "' existiert bereits.", "Fehler", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void addNoteAction() {
        UserData userData = dataManager.getUserData(currentUsername);
        if (userData == null || userData.getFaecher().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte fügen Sie zuerst ein Fach hinzu.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] faecherArray = userData.getFaecher().keySet().toArray(new String[0]);
        JComboBox<String> fachComboBox = new JComboBox<>(faecherArray);
        JTextField notenField = new JTextField(5);
        JTextField gewichtungField = new JTextField(5);
        gewichtungField.setText("1.0");

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Fach:"));
        panel.add(fachComboBox);
        panel.add(new JLabel("Note (z.B. 1.75):"));
        panel.add(notenField);
        panel.add(new JLabel("Gewichtung (z.B. 1.0):"));
        panel.add(gewichtungField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Neue Note hinzufügen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String fachName = (String) fachComboBox.getSelectedItem();
                double noteWert = Double.parseDouble(notenField.getText().replace(',', '.'));
                double gewichtung = Double.parseDouble(gewichtungField.getText().replace(',', '.'));

                if (fachName != null) {
                    dataManager.addNote(currentUsername, fachName, noteWert, gewichtung);
                    JOptionPane.showMessageDialog(this, "Note wurde hinzugefügt.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                    refreshFaecherPanel();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ungültige Eingabe. Bitte geben Sie gültige Zahlen für Note und Gewichtung ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTheme() {
        Color bg = isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
        Color fg = isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND;

        UIManager.put("Panel.background", bg);
        UIManager.put("OptionPane.background", bg);
        UIManager.put("OptionPane.messageForeground", fg);
        UIManager.put("Button.background", isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.focus", new Color(0,0,0,0));

        SwingUtilities.updateComponentTreeUI(this);

        mainPanel.setBackground(bg);
        if(loginPanel != null) updatePanelColors(loginPanel);
        if(registerPanel != null) updatePanelColors(registerPanel);
        if(gradesPanel != null) {
            updatePanelColors(gradesPanel);
            refreshFaecherPanel();
        }
    }

    private void updatePanelColors(Container container) {
        Color bg = isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
        Color fg = isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND;
        Color panelBg = isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND;
        Color accent = isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR;

        container.setBackground(bg);
        container.setForeground(fg);

        for (Component c : container.getComponents()) {
            c.setForeground(fg);
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                if (!button.isBorderPainted()) {
                    button.setForeground(accent);
                    button.setBackground(bg);
                } else {
                    button.setBackground(accent);
                    button.setForeground(Color.WHITE);
                    button.setBorderPainted(false);
                }
            } else if (c instanceof JToggleButton) {
                c.setBackground(accent);
                c.setForeground(Color.WHITE);
            } else if (c instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) c;
                textComponent.setBackground(panelBg);
                textComponent.setForeground(fg);
                textComponent.setCaretColor(fg);
            } else if (c instanceof JLabel) {
                c.setForeground(fg);
            } else if (c instanceof JPanel) {
                updatePanelColors((Container) c);
            } else if (c instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) c;
                scrollPane.getViewport().setBackground(bg);
                scrollPane.setBackground(bg);
            } else {
                c.setBackground(bg);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NotenGUI gui = new NotenGUI();
            gui.setVisible(true);
        });
    }
}
