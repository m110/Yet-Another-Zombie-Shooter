package org.m110.shooter.pickups;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum PickupProto {
    CRATE, AMMO, MEDPACK, ADRENALINE;

    private static final List<PickupProto> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));

    public static PickupProto getRandom()  {
        return VALUES.get(MathUtils.random(VALUES.size()-1));
    }

    public static PickupProto getRandom(PickupProto... pickupProtos) {
        return pickupProtos[MathUtils.random(pickupProtos.length-1)];
    }
}
