package org.m110.shooter.pickups;

import org.m110.shooter.effects.MedpackEffect;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Medpack extends Pickup {

    public Medpack(float x, float y) {
        super("medpack", x, y);
        effect = new MedpackEffect();
    }
}
