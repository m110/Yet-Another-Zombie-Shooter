package org.m110.shooter.core.timers;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class IntervalTimer extends Timer {

    private final float interval;
    private float time;

    public IntervalTimer(float interval) {
        this.interval = interval;
        time = interval;
    }

    @Override
    public void update(float delta) {
        if (time > 0) {
            time -= delta;
        }
    }

    @Override
    public void reset() {
        time = interval;
    }

    @Override
    public boolean ready() {
        return time <= 0;
    }
}
