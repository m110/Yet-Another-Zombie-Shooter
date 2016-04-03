package org.m110.shooter.effects;

import org.m110.shooter.entities.Entity;

public class HealEffect implements EntityEffect {

    private Integer healAmount;

    public HealEffect(Integer healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public boolean effect(Entity target) {
        target.addHealth(healAmount);
        return true;
    }
}
