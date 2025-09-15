import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TransitionManager {
    private final JFrame frame;

    public TransitionManager(JFrame frame) {
        this.frame = frame;
    }

    public void slideTo(JPanel cardContainer, String cardName, int direction) {
        // direction: +1 (neue Seite von rechts), -1 (von links)
        BufferedImage before = snapshot(cardContainer);
        CardLayout cl = (CardLayout) cardContainer.getLayout();
        cl.show(cardContainer, cardName);
        cardContainer.doLayout();
        cardContainer.revalidate();
        cardContainer.repaint();
        BufferedImage after = snapshot(cardContainer);

        JComponent overlay = new JComponent() {
            float progress = 0f;
            { setOpaque(false); }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (before == null || after == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth();
                    int offset = (int) (w * (1f - progress) * direction);

                    // alte Seite
                    g2.drawImage(before, -offset, 0, null);
                    // neue Seite
                    g2.drawImage(after, w * direction - offset, 0, null);
                } finally {
                    g2.dispose();
                }
            }
        };
        overlay.setBounds(cardContainer.getBounds());
        Point p = SwingUtilities.convertPoint(cardContainer.getParent(), cardContainer.getLocation(), frame.getLayeredPane());
        overlay.setLocation(p);

        frame.getLayeredPane().add(overlay, JLayeredPane.DRAG_LAYER);
        frame.getLayeredPane().repaint();

        Animator.play(350, Easing::easeOutCubic, v -> {
            overlay.repaint();
            try {
                var field = overlay.getClass().getDeclaredField("progress");
                field.setAccessible(true);
                field.setFloat(overlay, v);
            } catch (Exception ignored) {}
        }, () -> {
            frame.getLayeredPane().remove(overlay);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();
        });
    }

    public void fadeTheme(JComponent target, Runnable applyTheme) {
        BufferedImage before = snapshot(target);
        applyTheme.run();
        BufferedImage after = snapshot(target);

        JComponent overlay = new JComponent() {
            float alpha = 0f;
            { setOpaque(false); }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (before == null || after == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.drawImage(after, 0, 0, null);
                    Composite old = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - alpha));
                    g2.drawImage(before, 0, 0, null);
                    g2.setComposite(old);
                } finally {
                    g2.dispose();
                }
            }
        };
        overlay.setBounds(target.getBounds());
        Point p = SwingUtilities.convertPoint(target.getParent(), target.getLocation(), frame.getLayeredPane());
        overlay.setLocation(p);
        frame.getLayeredPane().add(overlay, JLayeredPane.DRAG_LAYER);
        frame.getLayeredPane().revalidate();
        frame.getLayeredPane().repaint();

        Animator.play(220, Easing::easeInOutCubic, v -> {
            try {
                var f = overlay.getClass().getDeclaredField("alpha");
                f.setAccessible(true);
                f.setFloat(overlay, v);
            } catch (Exception ignored) {}
            overlay.repaint();
        }, () -> {
            frame.getLayeredPane().remove(overlay);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();
        });
    }

    private static BufferedImage snapshot(JComponent comp) {
        int w = Math.max(1, comp.getWidth());
        int h = Math.max(1, comp.getHeight());
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        comp.paintAll(g2);
        g2.dispose();
        return img;
    }
}
