package org.m110.shooter.auras;

import org.m110.shooter.entities.Entity;

public interface AuraPeriodicEffect {
    void effect(Entity owner, int ticks);
}
