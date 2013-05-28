package org.m110.shooter.ai;

import org.m110.shooter.actors.ShooterActor;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class AI {
    public abstract void act(ShooterActor me, float delta);
}
