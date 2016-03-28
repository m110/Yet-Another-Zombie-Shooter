package org.m110.shooter.auras;

import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class HealAura extends Aura {
    protected static final float duration = 5.0f;
    protected static final int ticks = 10;
    public HealAura(final Entity owner, final int healAmount) {
        super(owner, "heal", duration, ticks, new AuraAction() {
            @Override
            public void action() {}
        }, new AuraAction() {
            @Override
            public void action() {
                owner.addHealth(healAmount / ticks);
            }
        });
    }
}
