package org.m110.shooter.ai.entity;

import org.m110.shooter.entities.CombatEntity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class NoneAI extends AI {

    public NoneAI(CombatEntity me) {
        super(null);
    }

    private static NoneAI INSTANCE = null;

    public static NoneAI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoneAI(null);
        }
        return INSTANCE;
    }
}
