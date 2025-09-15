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

    // Zusätzliche „spaßige“ Akzentfarben
    private static final Color ACCENT_FUCHSIA = new Color(255, 99, 132);
    private static final Color ACCENT_PURPLE = new Color(171, 71, 188);
    private static final Color ACCENT_TEAL = new Color(0, 200, 180);
    private static final Color ACCENT_YELLOW = new Color(255, 193, 7);

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

    // Transition Manager für animierte Seitenwechsel
    private TransitionManager transitions;

    // Einstellungen & UI
    private final SettingsManager settingsManager = new SettingsManager();
    private AnimatedBackgroundPanel animatedBg;
    private GradientPanel headerPanelRef;


    public NotenGUI() {
        setTitle("Notenverwaltung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "login");

        registerPanel = createRegisterPanel();
        mainPanel.add(registerPanel, "register");

        // animierter Hintergrund + Hauptinhalt
        animatedBg = new AnimatedBackgroundPanel();
        animatedBg.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        animatedBg.add(mainPanel, BorderLayout.CENTER);
        add(animatedBg);

        transitions = new TransitionManager(this);
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
        ModernButton loginButton = new ModernButton("Anmelden", true);
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        ModernButton registerButton = new ModernButton("Noch kein Konto? Registrieren", false);
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

        registerButton.addActionListener(e -> {
            if (transitions != null) {
                transitions.slideTo(mainPanel, "register", +1);
            } else {
                cardLayout.show(mainPanel, "register");
            }
        });
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
        ModernButton registerButton = new ModernButton("Registrieren", true);
        panel.add(registerButton, gbc);

        gbc.gridy = 4;
        ModernButton backToLoginButton = new ModernButton("Zurück zur Anmeldung", false);
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

        backToLoginButton.addActionListener(e -> {
            if (transitions != null) {
                transitions.slideTo(mainPanel, "login", -1);
            } else {
                cardLayout.show(mainPanel, "login");
            }
        });
        return panel;
    }

    private void setupAndShowGradesPanel() {
        gradesPanel = new JPanel(new BorderLayout(10, 10));
        gradesPanel.setName("gradesPanel");

        this.headerPanelRef = new GradientPanel();
        GradientPanel headerPanel = this.headerPanelRef;
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setColors(isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR, ACCENT_FUCHSIA);
        JLabel titleLabel = new JLabel("Notenübersicht für " + currentUsername);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel eastHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        double gesamtschnitt = dataManager.getGesamtschnitt(currentUsername);
        this.gesamtschnittLabel = new JLabel(String.format("Gesamtschnitt: %.2f", gesamtschnitt));
        this.gesamtschnittLabel.setFont(new Font("Arial", Font.BOLD, 20));

        ModernToggleButton themeToggle = new ModernToggleButton("Dark Mode");
        themeToggle.setSelected(isDarkMode);
        themeToggle.addActionListener(e -> {
            isDarkMode = themeToggle.isSelected();
            themeToggle.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            updateTheme();
        });

        eastHeaderPanel.add(this.gesamtschnittLabel);
        eastHeaderPanel.add(themeToggle);
        ModernButton settingsButton = new ModernButton("Einstellungen", false);
        eastHeaderPanel.add(settingsButton);
        headerPanel.add(eastHeaderPanel, BorderLayout.EAST);
        gradesPanel.add(headerPanel, BorderLayout.NORTH);

        settingsButton.addActionListener(e -> {
            SettingsDialog dlg = new SettingsDialog(this, settingsManager);
            if (dlg.showDialog()) {
                settingsManager.setCountryCode(dlg.getSelectedCountryCode());
                settingsManager.setLanguage(dlg.getSelectedLanguage());
                settingsManager.setScheme(dlg.getSelectedScheme());
                applySettings();
            }
        });

        faecherContainerPanel = new JPanel();
        faecherContainerPanel.setLayout(new BoxLayout(faecherContainerPanel, BoxLayout.Y_AXIS));
        refreshFaecherPanel();

        JScrollPane scrollPane = new JScrollPane(faecherContainerPanel);
        scrollPane.setBorder(null);
        gradesPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ModernButton addFachButton = new ModernButton("Neues Fach hinzufügen", true);
        ModernButton addNoteButton = new ModernButton("Note hinzufügen", true);
        footerPanel.add(addFachButton);
        footerPanel.add(addNoteButton);
        gradesPanel.add(footerPanel, BorderLayout.SOUTH);

        addFachButton.addActionListener(e -> addFachAction());
        addNoteButton.addActionListener(e -> addNoteAction());

        mainPanel.add(gradesPanel, "grades");
        updateTheme(); // Theme auf das neue Panel anwenden
        if (transitions != null) {
            transitions.slideTo(mainPanel, "grades", +1);
        } else {
            cardLayout.show(mainPanel, "grades");
        }
    }

    private void refreshFaecherPanel() {
        if (faecherContainerPanel == null) {
            return;
        }
        faecherContainerPanel.removeAll();
        UserData userData = dataManager.getUserData(currentUsername);
        if (userData != null && userData.getFaecher() != null) {
            for (Fach fach : userData.getFaecher().values()) {
                ShadowPanel fachPanel = new ShadowPanel(12, 10);
                fachPanel.setLayout(new BorderLayout(10, 0));
                fachPanel.setCornerRadius(12);
                fachPanel.setShadowSize(10);
                fachPanel.setBackground(isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
                fachPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                fachPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

                JLabel fachNameLabel = new JLabel(fach.getName());
                fachNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                fachPanel.add(fachNameLabel, BorderLayout.WEST);

                JLabel fachSchnittLabel = new JLabel(String.format("Schnitt: %.2f", fach.getSchnitt()));
                fachSchnittLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                fachPanel.add(fachSchnittLabel, BorderLayout.CENTER);

                ModernButton viewButton = new ModernButton("Noten anzeigen", true);
                viewButton.addActionListener(e -> showNotenForFach(fach));
                fachPanel.add(viewButton, BorderLayout.EAST);

                FadeSlideLayerUI<JPanel> ui = new FadeSlideLayerUI<>();
                JLayer<JPanel> layer = new JLayer<>(fachPanel, ui);
                ui.setProgress(0f);
                faecherContainerPanel.add(layer);
                faecherContainerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        faecherContainerPanel.setBackground(isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND);

        // Gestaffelte Reveal-Animation: jedes JLayer nacheinander einblenden
        int count = faecherContainerPanel.getComponentCount();
        int delayStep = 60;
        int idx = 0;
        for (Component comp : faecherContainerPanel.getComponents()) {
            if (comp instanceof JLayer) {
                final FadeSlideLayerUI<?> ui = (FadeSlideLayerUI<?>) ((JLayer<?>) comp).getUI();
                final int startDelay = idx * delayStep;
                new javax.swing.Timer(startDelay, e -> {
                    ((javax.swing.Timer) e.getSource()).stop();
                    Animator.play(380, Easing::easeOutBack, t -> {
                        ui.setProgress(t);
                        comp.repaint();
                    });
                }).start();
                idx++;
            }
        }

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

        ModernButton deleteButton = new ModernButton("Ausgewählte Note löschen", true);
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

        // Farben für den Dialog und die Liste anwenden
        Color fg = isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND;
        Color panelBg = isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND;
        Color accent = isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR;
        notenList.setBackground(panelBg);
        notenList.setForeground(fg);
        notenList.setSelectionBackground(accent);
        notenList.setSelectionForeground(Color.WHITE);
        updatePanelColors(dialog.getContentPane());

        // Fade-in: Fenster-Opacity animieren, wenn unterstützt
        try {
            dialog.setOpacity(0f);
        } catch (Exception ignored) {}
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                try {
                    Animator.play(220, Easing::easeInOutCubic, v -> {
                        try { dialog.setOpacity(v); } catch (Exception ignored) {}
                    });
                } catch (Exception ignored) {}
            }
        });
        dialog.setVisible(true);
    }

    private void applySettings() {
        // Systemweite Locale für Anzeige (Zahlen, Länder-/Sprachnamen)
        try {
            java.util.Locale.setDefault(LanguagesUtil.localeOf(settingsManager.getLanguage(), settingsManager.getCountryCode()));
        } catch (Exception ignored) {}

        updateTheme();
        refreshFaecherPanel();
        refreshGesamtschnitt();
    }

    private void refreshGesamtschnitt() {
        if (this.gesamtschnittLabel != null) {
            double gesamtschnitt = dataManager.getGesamtschnitt(currentUsername);
            String suffix = "";
            if (settingsManager.getScheme() == SettingsManager.GradingScheme.LETTER_A_F) {
                String letter = settingsManager.numericToLetter(gesamtschnitt);
                suffix = " (" + String.format("%.2f", gesamtschnitt) + ", " + letter + ")";
                this.gesamtschnittLabel.setText("Gesamtschnitt:" + suffix);
            } else {
                this.gesamtschnittLabel.setText(String.format("Gesamtschnitt: %.2f", gesamtschnitt));
            }
            Color gradeCol = ColorUtils.gradeToColor(gesamtschnitt);
            if (headerPanelRef != null) {
                headerPanelRef.setColors(gradeCol, isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND);
                headerPanelRef.repaint();
            }
        }
    }

    private void addFachAction() {
        String fachName = JOptionPane.showInputDialog(this, "Geben Sie den Namen des neuen Fachs ein:", "Neues Fach", JOptionPane.PLAIN_MESSAGE);
        if (fachName != null && !fachName.trim().isEmpty()) {
            if (dataManager.addFach(currentUsername, fachName)) {
                JOptionPane.showMessageDialog(this, "Fach '" + fachName + "' wurde hinzugefügt.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                refreshFaecherPanel();
                // Konfetti-Effekt
                Color[] palette = new Color[]{ isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR, ACCENT_FUCHSIA, ACCENT_PURPLE, ACCENT_TEAL, ACCENT_YELLOW };
                ConfettiOverlay.burst(this, palette, 200);
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

        // Panel-Farben dem aktuellen Theme anpassen
        updatePanelColors(panel);

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
                    // Spaßiger Effekt
                    Color[] palette = new Color[]{ isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR, ACCENT_FUCHSIA, ACCENT_TEAL };
                    ConfettiOverlay.burst(this, palette, 120);
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
        // Zusätzliche Defaults für bessere Lesbarkeit im Dark Mode
        UIManager.put("Label.foreground", fg);
        UIManager.put("Panel.foreground", fg);
        UIManager.put("ScrollPane.background", bg);

        UIManager.put("List.background", isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
        UIManager.put("List.foreground", fg);
        UIManager.put("List.selectionBackground", isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR);
        UIManager.put("List.selectionForeground", Color.WHITE);

        UIManager.put("TextField.background", isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
        UIManager.put("TextField.foreground", fg);
        UIManager.put("PasswordField.background", isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
        UIManager.put("PasswordField.foreground", fg);
        UIManager.put("TextArea.background", isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
        UIManager.put("TextArea.foreground", fg);
        UIManager.put("TextPane.background", isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
        UIManager.put("TextPane.foreground", fg);
        UIManager.put("EditorPane.background", isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
        UIManager.put("EditorPane.foreground", fg);

        UIManager.put("ComboBox.background", isDarkMode ? DARK_PANEL_BACKGROUND : LIGHT_PANEL_BACKGROUND);
        UIManager.put("ComboBox.foreground", fg);
        UIManager.put("ComboBox.selectionBackground", isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

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
            if (c instanceof ModernButton) {
                ModernButton mb = (ModernButton) c;
                mb.setAccentColor(accent);
                mb.setDarkMode(isDarkMode);
            } else if (c instanceof JButton) {
                JButton button = (JButton) c;
                if (!button.isBorderPainted()) {
                    button.setForeground(accent);
                    button.setBackground(bg);
                } else {
                    button.setBackground(accent);
                    button.setForeground(Color.WHITE);
                    button.setBorderPainted(false);
                }
            } else if (c instanceof ModernToggleButton) {
                ModernToggleButton tb = (ModernToggleButton) c;
                tb.setAccentColor(accent);
                tb.setDarkMode(isDarkMode);
                tb.setForeground(Color.WHITE);
            } else if (c instanceof JToggleButton) {
                c.setBackground(accent);
                c.setForeground(Color.WHITE);
            } else if (c instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) c;
                textComponent.setBackground(panelBg);
                textComponent.setForeground(fg);
                textComponent.setCaretColor(fg);
                // runde Ränder für Textfelder
                Color line = isDarkMode ? new Color(255, 255, 255, 45) : new Color(0, 0, 0, 50);
                textComponent.setBorder(new RoundedBorder(10, line, 1, 8));
            } else if (c instanceof JLabel) {
                c.setForeground(fg);
            } else if (c instanceof JList) {
                JList<?> list = (JList<?>) c;
                list.setBackground(panelBg);
                list.setForeground(fg);
                list.setSelectionBackground(accent);
                list.setSelectionForeground(Color.WHITE);
            } else if (c instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) c;
                combo.setBackground(panelBg);
                combo.setForeground(fg);
                if (combo.isEditable() && combo.getEditor() != null) {
                    Component editorComp = combo.getEditor().getEditorComponent();
                    if (editorComp != null) {
                        editorComp.setBackground(panelBg);
                        editorComp.setForeground(fg);
                        if (editorComp instanceof JTextComponent) {
                            Color line = isDarkMode ? new Color(255, 255, 255, 45) : new Color(0, 0, 0, 50);
                            ((JTextComponent) editorComp).setBorder(new RoundedBorder(10, line, 1, 8));
                        }
                    }
                }
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
