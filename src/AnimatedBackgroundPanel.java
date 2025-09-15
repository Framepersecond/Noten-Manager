import javax.swing.*;
import java.awt.*;

public class AnimatedBackgroundPanel extends JPanel {
    private final Timer timer;
    private float phase = 0f;
    private Color[] palette = new Color[] {
            new Color(0, 122, 204),
            new Color(171, 71, 188),
            new Color(0, 200, 180),
            new Color(255, 193, 7),
            new Color(255, 99, 132)
    };

    public AnimatedBackgroundPanel() {
        setOpaque(true);
        timer = new Timer(1000 / 120, e -> {
            phase += 0.0025f; // langsam
            repaint();
        });
        timer.start();
    }

    public void setPalette(Color[] colors) {
        if (colors != null && colors.length >= 2) {
            this.palette = colors.clone();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // weicher Übergang zwischen 2 Palettenfarben
            int i1 = (int) Math.floor(phase % palette.length);
            int i2 = (i1 + 1) % palette.length;
            float localT = phase - (float) Math.floor(phase);
            Color c1 = palette[i1];
            Color c2 = palette[i2];
            Color mix1 = ColorUtils.lerp(c1, c2, localT);

            // diagonaler Verlauf + radialer Hauch
            GradientPaint gp = new GradientPaint(0, 0, mix1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setPaint(new RadialGradientPaint(
                    new Point(w / 2, h / 3),
                    Math.max(w, h),
                    new float[]{0f, 1f},
                    new Color[]{ColorUtils.withAlpha(c1, 60), ColorUtils.withAlpha(c2, 0)}
            ));
            g2.fillRect(0, 0, w, h);
        } finally {
            g2.dispose();
        }
    }
}
