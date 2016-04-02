package org.m110.shooter.ai;

import org.m110.shooter.ai.entity.*;

public enum EntityAI {
    BOOMER(BoomerAI.class),
    CHARGER(ChargerAI.class),
    CHASE(ChaseAI.class),
    COMBAT(CombatAI.class),
    NONE(NoneAI.class),
    SPAWNER(SpawnerAI.class),
    SPITTER(SpitterAI.class);

    public Class AIClass;

    EntityAI(Class AIClass) {
        this.AIClass = AIClass;
    }
}
