package org.m110.shooter.core;

import com.badlogic.gdx.math.MathUtils;

import java.util.EnumSet;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class CoreUtils {
    private CoreUtils() {}

    public static Enum getRandomFromEnumSet(EnumSet enumSet) {
        return (Enum) enumSet.toArray()[MathUtils.random(0, enumSet.size() - 1)];
    }
}
