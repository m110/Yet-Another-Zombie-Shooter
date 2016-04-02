package org.m110.shooter.ai.entity;

import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.CombatEntity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class CombatAI extends AI {

    protected final CombatEntity me;

    public CombatAI(CombatEntity me) {
        super(me);
        this.me = me;
    }

    @Override
    public void afterHit(Entity attacker) {
        if (me != attacker && !me.inCombat()) {
            me.startCombat(attacker);
        }
    }

    @Override
    public void afterDeath() {
        if (me.getVictim() != null) {
            float angle = me.angleWith(player) + 180.0f;
            float offset = 30.0f;
            me.setPiecesRecoil(angle - offset, angle + offset, 0.1f, 1.0f);
        }
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
