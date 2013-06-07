package org.m110.shooter.pickups;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum PickupType {
    CRATE, AMMO, MEDPACK, ADRENALINE;

    private static final List<PickupType> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));

    public static PickupType getRandom()  {
        return VALUES.get(MathUtils.random(VALUES.size()-1)); // todo zmien to
    }
}
