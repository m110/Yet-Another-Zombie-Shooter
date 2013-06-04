package org.m110.shooter.ai.entity;

import org.m110.shooter.Shooter;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class AI {
    protected final Entity me;
    protected Player player;

    public AI(Entity me) {
        this.me = me;
        player = Shooter.getInstance().getPlayer();
    }

    public void act(float delta) {}

    public void afterHit(Entity attacker) {
        if (me != attacker && !me.inCombat()) {
            me.startCombat(attacker);
        }
    }

    public void afterCollision() {}

    public void afterDeath() {
        if (player == null) {
            return;
        }

        float angle = me.angleWith(player) + 180.0f;
        float offset = 30.0f;
        me.setPiecesRecoil(angle - offset, angle + offset, 0.5f, 1.0f);
    }

    protected boolean updateVictim() {
        if (player == null) {
            return false;
        }

        if (me.isDead()) {
            return false;
        }

        if (me.inCombat()) {
            return true;
        } else {
            if (me.distanceTo(player) < player.getGame().getAggroRange()) {
                me.startCombat(player);
                return true;
            } else {
                return false;
            }
        }
    }
}
