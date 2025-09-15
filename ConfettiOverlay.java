import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfettiOverlay extends JComponent {
    private static class Particle {
        float x, y, vx, vy, size, life;
        Color color;
        float rotation, vr;
        Shape shape;
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Timer timer;
    private final Random rnd = new Random();

    public ConfettiOverlay(Rectangle bounds, Color[] palette, int count) {
        setOpaque(false);
        setBounds(bounds);
        for (int i = 0; i < count; i++) {
            particles.add(createParticle(palette));
        }
        timer = new Timer(1000 / 120, e -> tick());
    }

    private Particle createParticle(Color[] palette) {
        Particle p = new Particle();
        p.x = getWidth() / 2f + rnd.nextFloat() * 60f - 30f;
        p.y = getHeight() / 2f + rnd.nextFloat() * 20f - 10f;
        float speed = 2.5f + rnd.nextFloat() * 3.5f;
        double angle = Math.toRadians(rnd.nextInt(360));
        p.vx = (float) (Math.cos(angle) * speed);
        p.vy = (float) (Math.sin(angle) * speed - 2.5f);
        p.size = 6f + rnd.nextFloat() * 10f;
        p.life = 1f;
        p.color = palette[rnd.nextInt(palette.length)];
        p.rotation = rnd.nextFloat() * 360f;
        p.vr = (rnd.nextFloat() - 0.5f) * 10f;
        int sh = rnd.nextInt(3);
        if (sh == 0) p.shape = new Rectangle(0, 0, 1, 1);
        else if (sh == 1) p.shape = new Polygon(new int[]{0,1,0}, new int[]{0,0,1}, 3);
        else p.shape = new Polygon(new int[]{0,1,2}, new int[]{1,0,1}, 3);
        return p;
    }

    private void tick() {
        float gravity = 0.15f;
        float drag = 0.995f;
        for (Particle p : particles) {
            p.vy += gravity;
            p.vx *= drag;
            p.vy *= drag;
            p.x += p.vx;
            p.y += p.vy;
            p.rotation += p.vr;
            p.life -= 0.008f;
        }
        particles.removeIf(pt -> pt.life <= 0f || pt.y > getHeight() + 40);
        repaint();
        if (particles.isEmpty()) {
            timer.stop();
            Container parent = getParent();
            if (parent != null) {
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (Particle p : particles) {
                g2.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), Math.round(255 * Math.max(0f, Math.min(1f, p.life)))));
                g2.translate(p.x, p.y);
                g2.rotate(Math.toRadians(p.rotation));
                g2.fill(new Rectangle(-Math.round(p.size/2), -Math.round(p.size/2), Math.round(p.size), Math.round(p.size)));
                g2.setTransform(new AffineTransform());
            }
        } finally {
            g2.dispose();
        }
    }

    public void start() {
        timer.start();
    }

    public static void burst(JFrame frame, Color[] palette, int count) {
        JLayeredPane layered = frame.getLayeredPane();
        Rectangle bounds = new Rectangle(0, 0, layered.getWidth(), layered.getHeight());
        ConfettiOverlay overlay = new ConfettiOverlay(bounds, palette, count);
        layered.add(overlay, JLayeredPane.POPUP_LAYER);
        overlay.start();
    }
}
