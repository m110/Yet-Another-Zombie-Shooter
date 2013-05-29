package org.m110.shooter.core.timers;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Timer {

    public abstract void update(float delta);
    public abstract void reset();
    public abstract boolean ready();
}
