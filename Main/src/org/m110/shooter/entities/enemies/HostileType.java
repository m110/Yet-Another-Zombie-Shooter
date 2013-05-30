package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum HostileType {
    ZOMBIE, BOOMER, CHARGER, SPAWNER;

    private static final List<HostileType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    public static HostileType getRandom()  {
        return VALUES.get(MathUtils.random(VALUES.size()-1));
    }

    public static HostileType getRandomWithoutSpawner()  {
        return VALUES.get(MathUtils.random(VALUES.size()-2));
    }
}
