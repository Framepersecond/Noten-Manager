import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;

public class FadeSlideLayerUI<T extends JComponent> extends LayerUI<T> {
    private float alpha = 0f;
    private int yOffset = 12;

    public void setProgress(float t) {
        t = Easing.clamp01(t);
        this.alpha = t;
        this.yOffset = (int) (12 * (1f - t));
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alpha))));
            g2.translate(0, yOffset);
            super.paint(g2, c);
        } finally {
            g2.dispose();
        }
    }
}
