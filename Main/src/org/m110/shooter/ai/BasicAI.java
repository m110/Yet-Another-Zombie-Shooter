package org.m110.shooter.ai;

import org.m110.shooter.Shooter;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BasicAI extends AI {

    public BasicAI(Entity me) {
        super(me);
    }

    @Override
    public void act(float delta) {
        if (!updateVictim()) {
            return;
        }

        me.attackChase();
    }
}
