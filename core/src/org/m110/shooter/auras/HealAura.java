package org.m110.shooter.auras;

import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class HealAura implements AuraPeriodicEffect {

    private int healAmount;

    public HealAura(int healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public void effect(Entity owner, int ticks) {
        owner.addHealth(healAmount / ticks);
    }
}
