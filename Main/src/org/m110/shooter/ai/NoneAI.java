package org.m110.shooter.ai;

import org.m110.shooter.actors.ShooterActor;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class NoneAI extends AI {

    private NoneAI() {}

    private static NoneAI INSTANCE = null;

    public static NoneAI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoneAI();
        }
        return INSTANCE;
    }

    @Override
    public void act(ShooterActor me, float delta) {
        // Intentionally left blank
    }
}
