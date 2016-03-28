package org.m110.shooter.core.timers;

import com.badlogic.gdx.math.MathUtils;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class RandomIntervalTimer extends IntervalTimer {

    private final float from;
    private final float to;

    public RandomIntervalTimer(float from, float to) {
        super(MathUtils.random(from, to));
        this.from = from;
        this.to = to;
    }

    @Override
    public void reset() {
        time = MathUtils.random(from, to);
    }
}
