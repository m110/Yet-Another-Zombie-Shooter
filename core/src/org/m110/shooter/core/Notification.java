package org.m110.shooter.core;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.timers.CountdownTimer;

/**
 * @author m1_10sz <m110@m110.pl>
 */

public class Notification {

    private final String message;
    private final CountdownTimer timer;
    private final float x;
    private float y;

    private static final float duration = 5.0f;
    private final BitmapFont font = Font.medium;

    public Notification(String message, float x, float y) {
        this.message = message;
        this.x = x;
        this.y = y;

        timer = new CountdownTimer(duration);
    }

    public void update(float delta) {
        timer.update(delta);
    }

    public void draw(SpriteBatch batch) {
        font.setColor(1.0f, 1.0f, 1.0f, Math.min(Math.max(timer.getTimeLeft(), 0.0f), 1.0f));
        font.draw(batch, message, x, y);
    }

    public void moveDown() {
        y -= 20;
    }

    public boolean isDone() {
        return timer.ready();
    }
}
