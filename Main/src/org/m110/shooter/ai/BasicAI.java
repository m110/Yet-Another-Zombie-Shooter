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
        if (me.isDead()) {
            return;
        }

        Player player = Shooter.getInstance().getGame().getPlayer();

        if (!me.inCombat()) {
            if (me.distanceTo(player) < 400.0f) {
                me.startCombat(player);
            }
        } else {
            me.attackChase();
        }
    }

    @Override
    public void afterHit() {
        if (!me.inCombat()) {
            me.startCombat(me.getGame().getPlayer());
        }
    }
}
