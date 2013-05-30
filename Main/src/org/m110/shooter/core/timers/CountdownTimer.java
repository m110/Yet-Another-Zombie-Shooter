package org.m110.shooter.core.timers;

import com.badlogic.gdx.Gdx;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class CountdownTimer extends Timer {

    private final float countdown;
    private float time;
    private boolean done;

    public CountdownTimer(float countdown) {
        this.countdown = countdown;
        this.time = countdown;
        done = false;
    }

    @Override
    public void update(float delta) {
        if (done) {
            return;
        }

        if (time > 0) {
            time -= delta;
        } else {
            action();
        }
    }

    protected void action() {
        done = true;
    }

    @Override
    public void reset() {
        this.time = countdown;
    }

    @Override
    public boolean ready() {
        return done;
    }
}
