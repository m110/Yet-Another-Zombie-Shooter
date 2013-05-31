package org.m110.shooter.weapons;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum WeaponSlot {
    HANDGUN, SMG, SHOTGUN, RIFLE;

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

}
