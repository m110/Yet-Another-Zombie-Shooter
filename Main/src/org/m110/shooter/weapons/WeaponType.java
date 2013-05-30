package org.m110.shooter.weapons;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum WeaponType {
    PISTOL, SHOTGUN, RIFLE;

    private static final List<WeaponType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    public static WeaponType getByName(String name) {
        switch (name) {
            case "pistol": return PISTOL;
            case "shotgun": return SHOTGUN;
            case "rifle": return RIFLE;
            default: throw new IllegalArgumentException("No such WeaponType: " + name);
        }
    }

    public int getMagazineCapacity() {
        switch (this) {
            case PISTOL: return 12;
            case SHOTGUN: return 8;
            case RIFLE: return 30;
        }
        return 0;
    }

    public static WeaponType getRandom()  {
        return VALUES.get(MathUtils.random(VALUES.size()-1));
    }
}
