package org.m110.shooter.ai.entity;

import org.m110.shooter.entities.Entity;

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

    @Override
    public void afterDeath() {
        me.setPiecesRecoil(0.0f, 360.0f, 1.0f, 1.5f);
    }
}
