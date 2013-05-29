package org.m110.shooter.ai;

import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BasicAI extends AI {

    private final IntervalTimer wanderTimer;
    private final float wanderRange;

    public BasicAI(Entity me) {
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
