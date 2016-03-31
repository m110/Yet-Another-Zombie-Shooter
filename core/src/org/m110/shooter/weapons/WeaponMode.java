package org.m110.shooter.weapons;

/**
 * @author m1_10sz <m110@m110.pl>
 */

public enum WeaponMode {
    AUTO("auto", 1000),
    BURST2("burst", 2),
    BURST3("burst", 3),
    SEMI("semi", 1);

    public final String name;
    public final int maxShots;

    WeaponMode(String name, int maxShots) {
        this.name = name;
        this.maxShots = maxShots;
    }
}
