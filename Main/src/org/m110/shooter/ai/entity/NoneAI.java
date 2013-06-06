package org.m110.shooter.ai.entity;

import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class NoneAI extends AI {

    private NoneAI() {
        super(null);
    }

    private static NoneAI INSTANCE = null;

    public static NoneAI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoneAI();
        }
        return INSTANCE;
    }
}
