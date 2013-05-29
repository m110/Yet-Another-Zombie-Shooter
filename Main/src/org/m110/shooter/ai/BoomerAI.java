package org.m110.shooter.ai;

import org.m110.shooter.Shooter;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BoomerAI extends AI {
    public BoomerAI(Entity me) {
        super(me);
    }

    @Override
    public void act(float delta) {
        if (!updateVictim()) {
            return;
        }

        if (me.isInMeleeRange(me.getVictim())) {
            me.dealDamage(me.getVictim());
            me.die();
        } else {
            me.moveChase();
        }
    }
}
