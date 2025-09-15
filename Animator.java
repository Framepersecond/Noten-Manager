import javax.swing.*;
import java.util.Objects;

public class Animator {

    public interface EasingFunction {
        float ease(float t);
    }

    public interface FloatConsumer {
        void accept(float value);
    }

    private final int durationMs;
    private final int fps;
    private final EasingFunction easing;
    private final Timer timer;
    private long startTime;
    private FloatConsumer onUpdate;
    private Runnable onComplete;

    public Animator(int durationMs, int fps, EasingFunction easing) {
        this.durationMs = durationMs;
        this.fps = fps;
        this.easing = easing;
        int delay = Math.max(5, 1000 / Math.max(1, fps));
        this.timer = new Timer(delay, e -> tick());
        this.timer.setCoalesce(true);
    }

    public Animator onUpdate(FloatConsumer consumer) {
        this.onUpdate = consumer;
        return this;
    }

    public Animator onComplete(Runnable r) {
        this.onComplete = r;
        return this;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        if (onUpdate != null) onUpdate.accept(0f);
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void tick() {
        long now = System.currentTimeMillis();
        float t = Math.min(1f, (now - startTime) / (float) durationMs);
        float v = easing != null ? easing.ease(t) : t;
        if (onUpdate != null) onUpdate.accept(v);
        if (t >= 1f) {
            timer.stop();
            if (onComplete != null) onComplete.run();
        }
    }

    public static void play(int durationMs, EasingFunction easing, FloatConsumer update, Runnable complete) {
        new Animator(durationMs, 120, easing).onUpdate(update).onComplete(complete).start();
    }

    public static void play(int durationMs, EasingFunction easing, FloatConsumer update) {
        play(durationMs, easing, update, null);
    }

    public static void playWithFps(int durationMs, int fps, EasingFunction easing, FloatConsumer update, Runnable complete) {
        new Animator(durationMs, Math.max(30, fps), easing).onUpdate(update).onComplete(complete).start();
    }
}
