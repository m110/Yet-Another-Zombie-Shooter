package org.m110.shooter.ai.entity;

import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.CombatEntity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class ChaseAI extends CombatAI {

    private final IntervalTimer wanderTimer;
    private final float wanderRange;

    public ChaseAI(CombatEntity me) {
        super(me);
        wanderTimer = new IntervalTimer(3.0f);
        wanderRange = 50.0f;
    }

    @Override
    public void act(float delta) {
        if (me.isDead()) {
            return;
        }

        if (!updateVictim()) {
            // Wander around
            wanderTimer.update(delta);
            if (wanderTimer.ready()) {
                float rx = me.getWorldX() + MathUtils.random(-wanderRange, wanderRange);
                float ry = me.getWorldY() + MathUtils.random(-wanderRange, wanderRange);
                me.lookAt(rx, ry);
                me.moveChase(rx, ry);
                wanderTimer.reset();
            }
            return;
        }

        me.attackChase();
    }
}
