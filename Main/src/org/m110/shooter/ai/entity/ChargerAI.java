package org.m110.shooter.ai.entity;

import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class ChargerAI extends AI {

    private boolean charging = false;
    private IntervalTimer chargeTimer;
    private float pushbackPower = 50.0f;

    public ChargerAI(Entity me) {
        super(me);
        chargeTimer = new IntervalTimer(2.0f);
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
                float vx = me.getVictim().getWorldX();
                float vy = me.getVictim().getWorldY();

                // Push the victim back a bit
                float dx = Math.signum(vx - me.getWorldX());
                float dy = Math.signum(vy - me.getWorldY());
                me.getVictim().move(vx + pushbackPower * dx, vy + pushbackPower * dy);
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
                me.lookAt(me.getVictim().getWorldX(), me.getVictim().getWorldY());
            }
        }
    }

    @Override
    public void afterCollision() {
        charging = false;
        me.setBonusVelocity(0.0f);
    }
}
