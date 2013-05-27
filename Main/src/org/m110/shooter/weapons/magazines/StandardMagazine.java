package org.m110.shooter.weapons.magazines;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class StandardMagazine extends Magazine {

    public StandardMagazine(int maxBullets, int bullets) {
        super(MagazineSlot.STANDARD, maxBullets, bullets);
    }
}
