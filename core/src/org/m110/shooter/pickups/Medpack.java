package org.m110.shooter.pickups;

import org.m110.shooter.effects.Effect;
import org.m110.shooter.effects.HealEffect;
import org.m110.shooter.effects.HealOverTimeEffect;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Medpack extends Pickup {

    public Medpack(PickupProto proto, float x, float y) {
        super(proto, x, y);

        int heal = Integer.parseInt(proto.values.get("heal"));

        if (proto.effect == Effect.HEAL) {
            effect = new HealEffect(heal);
        } else if (proto.effect == Effect.HEALOVERTIME) {
            float duration = Float.parseFloat(proto.values.get("duration"));
            int ticks = Integer.parseInt(proto.values.get("ticks"));

            effect = new HealOverTimeEffect(heal, duration, ticks);
        }
    }
}
