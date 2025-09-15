import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ShadowPanel extends JPanel {
    private int cornerRadius = 12;
    private int shadowSize = 10;
    private Color shadowColor = new Color(0, 0, 0, 80);

    private float hoverProgress = 0f;
    private float target = 0f;
    private final Timer hoverTimer;

    public ShadowPanel() {
        setOpaque(false);
        hoverTimer = new Timer(1000 / 120, e -> {
            float speed = 0.18f;
            if (Math.abs(hoverProgress - target) < 0.01f) {
                hoverProgress = target;
                ((Timer) e.getSource()).stop();
            } else {
                hoverProgress += (target - hoverProgress) * speed;
            }
            repaint();
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                target = 1f;
                if (!hoverTimer.isRunning()) hoverTimer.start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                target = 0f;
                if (!hoverTimer.isRunning()) hoverTimer.start();
            }
        });
    }

    public ShadowPanel(int cornerRadius, int shadowSize) {
        this();
        this.cornerRadius = cornerRadius;
        this.shadowSize = shadowSize;
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setShadowSize(int size) {
        this.shadowSize = size;
        repaint();
    }

    public void setShadowColor(Color c) {
        this.shadowColor = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int lift = Math.round(4 * hoverProgress);
            int dynamicShadow = shadowSize + Math.round(4 * hoverProgress);

            // Schatten
            for (int i = dynamicShadow; i >= 1; i--) {
                float alphaMul = (i / (float) (dynamicShadow * 2));
                int alpha = Math.round(shadowColor.getAlpha() * alphaMul);
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), alpha));
                g2.fill(new RoundRectangle2D.Float(
                        i, i - lift, w - i * 2f, h - i * 2f, cornerRadius + i, cornerRadius + i
                ));
            }

            // Inhalt (leicht angehoben)
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(
                    dynamicShadow / 2f, dynamicShadow / 2f - lift,
                    w - dynamicShadow, h - dynamicShadow,
                    cornerRadius, cornerRadius
            ));
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
