package org.m110.shooter.weapons;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum WeaponSlot {
    MELEE, PISTOL, SHOTGUN, RIFLE;

    public WeaponSlot getNext() {
        return this.ordinal() < WeaponSlot.values().length - 1
                ? WeaponSlot.values()[this.ordinal() + 1]
                : WeaponSlot.values()[0];
    }

    public WeaponSlot getPrevious() {
        return this.ordinal() > 0
                ? WeaponSlot.values()[this.ordinal() - 1]
                : WeaponSlot.values()[WeaponSlot.values().length - 1];
    }

    public static WeaponSlot getByName(String name) {
        switch (name) {
            case "pistol": return PISTOL;
            case "shotgun": return SHOTGUN;
            case "rifle": return RIFLE;
            default: throw new IllegalArgumentException("No such WeaponSlot: " + name);
        }
    }
}
