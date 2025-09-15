import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedBorder extends AbstractBorder {
    private final int radius;
    private final int thickness;
    private final int padding;
    private Color lineColor;

    public RoundedBorder(int radius, Color lineColor, int thickness, int padding) {
        this.radius = radius;
        this.lineColor = lineColor;
        this.thickness = thickness;
        this.padding = padding;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(thickness));
            float off = thickness / 2f;
            RoundRectangle2D rr = new RoundRectangle2D.Float(
                    x + off, y + off, width - thickness, height - thickness, radius, radius
            );
            g2.draw(rr);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(padding, padding, padding, padding);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = insets.bottom = insets.left = insets.right = padding;
        return insets;
    }
}
