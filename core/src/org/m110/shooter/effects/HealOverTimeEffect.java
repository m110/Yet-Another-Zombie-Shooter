package org.m110.shooter.effects;

import org.m110.shooter.auras.Aura;
import org.m110.shooter.auras.HealAura;
import org.m110.shooter.entities.Entity;

public class HealOverTimeEffect implements EntityEffect {

    private int healAmount;
    private float duration;
    private int ticks;

    public HealOverTimeEffect(int healAmount, float duration, int ticks) {
        this.healAmount = healAmount;
        this.duration = duration;
        this.ticks = ticks;
    }

    @Override
    public boolean effect(Entity target) {
        target.addAura(new Aura(target, "heal", duration, ticks, new HealAura(healAmount)));
        return true;
    }
}

