import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernToggleButton extends JToggleButton {
    private Color accent = new Color(0, 122, 204);
    private boolean darkMode = false;

    private float hover = 0f;
    private float target = 0f;
    private final Timer hoverTimer;

    public ModernToggleButton(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 16, 10, 16));
        setForeground(Color.WHITE);

        hoverTimer = new Timer(1000 / 120, e -> {
            float speed = 0.15f;
            if (Math.abs(hover - target) < 0.01f) {
                hover = target;
                ((Timer) e.getSource()).stop();
            } else {
                hover += (target - hover) * speed;
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                target = 1f;
                if (!hoverTimer.isRunning()) hoverTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                target = 0f;
                if (!hoverTimer.isRunning()) hoverTimer.start();
            }
        });
    }

    public void setAccentColor(Color accent) {
        this.accent = accent;
        repaint();
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension base = super.getPreferredSize();
        int minH = 36;
        return new Dimension(Math.max(base.width, 80), Math.max(base.height, minH));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 14;

            Color base = accent;
            Color bg;
            if (isSelected()) {
                bg = blend(base, Color.WHITE, 0.12f + 0.10f * hover);
            } else {
                int alpha = (int) (darkMode ? 55 + 40 * hover : 40 + 50 * hover);
                bg = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
            }
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

            // Text
            super.paintComponent(g);
        } finally {
            g2.dispose();
        }
    }

    private static Color blend(Color c1, Color c2, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        int a = (int) (c1.getAlpha() * (1 - ratio) + c2.getAlpha() * ratio);
        return new Color(r, g, b, a);
    }
}
