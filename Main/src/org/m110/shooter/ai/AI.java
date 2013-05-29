package org.m110.shooter.ai;

import org.m110.shooter.Shooter;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class AI {
    protected final Entity me;
    protected Player player;
    protected float tauntRange = 400.0f;

    public AI(Entity me) {
        this.me = me;
        player = Shooter.getInstance().getGame().getPlayer();
    }

    public void act(float delta) {}

    public void afterHit(Entity attacker) {
        if (me != attacker && !me.inCombat()) {
            me.startCombat(attacker);
        }
    }

    public void afterCollision() {}

    protected boolean updateVictim() {
        player = Shooter.getInstance().getGame().getPlayer();
        if (player == null) {
            return false;
        }

        if (me.isDead()) {
            return false;
        }

        if (me.inCombat()) {
            return true;
        } else {
            if (me.distanceTo(player) < tauntRange) {
                me.startCombat(player);
                return true;
            } else {
                return false;
            }
        }
    }
}
