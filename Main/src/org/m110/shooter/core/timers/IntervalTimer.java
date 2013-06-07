package org.m110.shooter.core.timers;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class IntervalTimer extends Timer {

    protected final float interval;

    public IntervalTimer(float interval) {
        this.interval = interval;
        time = interval;
    }

    @Override
    public void update(float delta) {
        if (time > 0.0f) {
            time -= delta;
        }
    }

    @Override
    public void reset() {
        time = interval;
    }

    public void reset(float time) {
        this.time = time;
    }

    @Override
    public boolean ready() {
        return time <= 0.0f;
    }

    @Override
    public void disable() {
        time = 0.0f;
    }
}
