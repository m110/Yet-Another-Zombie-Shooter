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
    public void tick(Entity owner, int ticks) {
        owner.addHealth(healAmount / ticks);
    }

    @Override
    public void apply(Entity owner) {}

    @Override
    public void wearOff(Entity owner) {}
}
