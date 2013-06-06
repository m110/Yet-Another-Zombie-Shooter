package org.m110.shooter.ai.entity;

import org.m110.shooter.core.timers.RandomIntervalTimer;
import org.m110.shooter.entities.enemies.CombatEntity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class ChargerAI extends CombatAI {

    private boolean charging = false;
    private RandomIntervalTimer chargeTimer;

    public ChargerAI(CombatEntity me) {
        super(me);
        chargeTimer = new RandomIntervalTimer(0.75f, 2.0f);
    }

    @Override
    public void act(float delta) {
        if (!updateVictim()) {
            return;
        }

        if (charging) {
            if (!me.isInMeleeRange(me.getVictim())) {
                me.moveForward();
            } else {
                me.getVictim().pushAwayFrom(me, 75.0f);
                me.dealDamage(me.getVictim());
                // Charging done
                charging = false;
                me.setBonusVelocity(0.0f);
            }
        } else {
            chargeTimer.update(delta);
            if (chargeTimer.ready()) {
                // Charge the player
                me.playAttackSound();
                me.setBonusVelocity(25.0f);
                chargeTimer.reset();
                charging = true;
            } else {
                // Remember coords of player
                me.lookAt(me.getVictim());
            }
        }
    }

    @Override
    public void afterCollision() {
        charging = false;
        me.setBonusVelocity(0.0f);
    }
}
