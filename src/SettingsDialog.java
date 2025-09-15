import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SettingsDialog extends JDialog {
    private final AutoCompleteComboBox<DisplayItem> countryBox;
    private final AutoCompleteComboBox<DisplayItem> languageBox;
    private final JComboBox<SettingsManager.GradingScheme> schemeBox;
    private boolean accepted = false;

    public SettingsDialog(JFrame owner, SettingsManager current) {
        super(owner, "Einstellungen", true);
        setSize(520, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        form.add(new JLabel("Land:"), gbc);
        gbc.gridx = 1;
        List<DisplayItem> countries = LanguagesUtil.getAllCountries(current.getLanguage());
        countryBox = new AutoCompleteComboBox<>(countries);
        // Vorbelegen
        for (DisplayItem di : countries) {
            if (di.code.equalsIgnoreCase(current.getCountryCode())) {
                countryBox.setSelectedItem(di);
                break;
            }
        }
        form.add(countryBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Sprache:"), gbc);
        gbc.gridx = 1;
        List<DisplayItem> languages = LanguagesUtil.getAllLanguages(current.getLanguage());
        languageBox = new AutoCompleteComboBox<>(languages);
        for (DisplayItem di : languages) {
            if (di.code.equalsIgnoreCase(current.getLanguage())) {
                languageBox.setSelectedItem(di);
                break;
            }
        }
        form.add(languageBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Notenschema:"), gbc);
        gbc.gridx = 1;
        schemeBox = new JComboBox<>(SettingsManager.GradingScheme.values());
        schemeBox.setSelectedItem(current.getScheme());
        form.add(schemeBox, gbc);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ModernButton cancel = new ModernButton("Abbrechen", false);
        ModernButton ok = new ModernButton("Übernehmen", true);
        buttons.add(cancel);
        buttons.add(ok);
        add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> { accepted = false; setVisible(false); });
        ok.addActionListener(e -> { accepted = true; setVisible(false); });
    }

    public boolean showDialog() {
        setVisible(true);
        return accepted;
    }

    public String getSelectedCountryCode() {
        Object o = countryBox.getSelectedItem();
        return (o instanceof DisplayItem) ? ((DisplayItem) o).code : "DE";
        }

    public String getSelectedLanguage() {
        Object o = languageBox.getSelectedItem();
        return (o instanceof DisplayItem) ? ((DisplayItem) o).code : "en";
    }

    public SettingsManager.GradingScheme getSelectedScheme() { return (SettingsManager.GradingScheme) schemeBox.getSelectedItem(); }
}
