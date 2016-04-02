package org.m110.shooter.effects;

import org.m110.shooter.auras.Aura;
import org.m110.shooter.auras.HealAura;
import org.m110.shooter.entities.Entity;

public class MedpackEffect implements Effect {
    @Override
    public boolean effect(Entity target) {
        target.addAura(new Aura(target, "heal", 5.0f, 10, new HealAura(50)));
        return true;
    }
}
