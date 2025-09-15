import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color c1 = new Color(0, 122, 204);
    private Color c2 = new Color(255, 99, 132);

    public GradientPanel() {
        setOpaque(false);
    }

    public void setColors(Color c1, Color c2) {
        this.c1 = c1;
        this.c2 = c2;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
