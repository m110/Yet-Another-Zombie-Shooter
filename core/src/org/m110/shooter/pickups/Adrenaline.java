package org.m110.shooter.pickups;

import org.m110.shooter.effects.AdrenalineEffect;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Adrenaline extends Pickup {

    public Adrenaline(PickupProto proto, float x, float y) {
        super(proto, x, y);

        float duration = Float.parseFloat(proto.values.get("duration"));

        effect = new AdrenalineEffect(duration);
    }
}
