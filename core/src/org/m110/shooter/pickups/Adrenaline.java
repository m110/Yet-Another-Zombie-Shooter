package org.m110.shooter.pickups;

import org.m110.shooter.effects.AdrenalineEffect;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Adrenaline extends Pickup {

    public Adrenaline(float x, float y) {
        super("adrenaline", x, y);
        effect = new AdrenalineEffect();
    }
}
