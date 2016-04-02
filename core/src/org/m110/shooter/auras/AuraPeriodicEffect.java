package org.m110.shooter.auras;

import org.m110.shooter.entities.Entity;

public interface AuraPeriodicEffect {
    void apply(Entity owner);
    void tick(Entity owner, int ticks);
    void wearOff(Entity owner);
}
