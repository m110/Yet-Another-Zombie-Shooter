package org.m110.shooter.ai;

import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class AI {
    protected final Entity me;

    public AI(Entity me) {
        this.me = me;
    }

    public void act(float delta) {}
    public void afterHit() {}
    public void hitWall() {}
}
