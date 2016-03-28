package org.m110.shooter.ai.entity;

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
