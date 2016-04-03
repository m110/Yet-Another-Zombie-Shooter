package org.m110.shooter.effects;

public enum Effect {
    ADRENALINE(AdrenalineEffect.class),
    AMMO(AmmoEffect.class),
    CRATE(CrateEffect.class),
    HEAL(HealEffect.class),
    HEALOVERTIME(HealOverTimeEffect.class);

    public Class effectClass;

    Effect(Class effectClass) {
        this.effectClass = effectClass;
    }
}
