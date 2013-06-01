package org.m110.shooter.weapons;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum WeaponMode {
    SEMI("semi", 1),
    BURST("burst", 2),
    AUTO("auto", 1000);

    public final String name;
    public final int maxShots;

    WeaponMode(String name, int maxShots) {
        this.name = name;
        this.maxShots = maxShots;
    }
}
