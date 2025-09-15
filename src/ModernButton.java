import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JButton {
    private boolean filled;
    private Color accent = new Color(0, 122, 204);
    private boolean darkMode = false;

    private float hover = 0f;
    private float target = 0f;
    private final Timer hoverTimer;

    // Ripple
    private float rippleRadius = 0f;
    private float rippleAlpha = 0f;
    private Point rippleCenter = null;
    private Timer rippleTimer;

    public ModernButton(String text, boolean filled) {
        super(text);
        this.filled = filled;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 16, 10, 16));
        setForeground(filled ? Color.WHITE : accent);

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

            @Override
            public void mousePressed(MouseEvent e) {
                startRipple(e.getPoint());
            }
        });
    }

    private void startRipple(Point p) {
        rippleCenter = p;
        rippleRadius = 0f;
        rippleAlpha = 0.35f;
        if (rippleTimer != null && rippleTimer.isRunning()) rippleTimer.stop();
        int maxR = (int) Math.hypot(Math.max(p.x, getWidth() - p.x), Math.max(p.y, getHeight() - p.y));
        rippleTimer = new Timer(16, e -> {
            rippleRadius += Math.max(8f, maxR / 14f);
            rippleAlpha *= 0.92f;
            if (rippleRadius >= maxR || rippleAlpha < 0.03f) {
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        rippleTimer.start();
    }

    public void setAccentColor(Color accent) {
        this.accent = accent;
        if (!filled) {
            setForeground(accent);
        } else {
            setForeground(Color.WHITE);
        }
        repaint();
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        repaint();
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        setForeground(filled ? Color.WHITE : accent);
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

            // Hintergrund
            if (filled) {
                Color base = accent;
                Color bg = blend(base, Color.WHITE, 0.12f * hover);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
            } else {
                // dezente Hover-Fläche
                Color hoverFill = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.min(60, (int) (90 * hover)));
                if (hover > 0.02f) {
                    g2.setColor(hoverFill);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
                }
                // feine Umrandung
                Color borderCol = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), darkMode ? 120 : 160);
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(borderCol);
                g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, w - 1.2f, h - 1.2f, arc, arc));
            }

            // Ripple
            if (rippleCenter != null && rippleAlpha > 0f) {
                g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
                Color rippleCol = new Color(255, 255, 255, Math.min(255, (int) (rippleAlpha * 255)));
                if (!filled) {
                    rippleCol = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.min(255, (int) (rippleAlpha * 255)));
                }
                g2.setColor(rippleCol);
                float r = rippleRadius;
                Shape circle = new Ellipse2D.Float(rippleCenter.x - r, rippleCenter.y - r, r * 2, r * 2);
                g2.fill(circle);
            }

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
