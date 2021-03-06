package org.m110.shooter.ai.game;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class NoneAI extends GameAI {

    private static NoneAI INSTANCE = null;

    public static NoneAI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoneAI();
        }
        return INSTANCE;
    }

    private NoneAI() {
        super(null);
    }
}
