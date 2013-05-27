package org.m110.shooter.weapons;

import org.m110.shooter.actors.Bullet;
import org.m110.shooter.weapons.magazines.StandardMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Rifle extends Weapon {

    public Rifle() {
        super(2, WeaponSlot.RILE, "rifle");
        setBulletsCount(1);
        setBulletVelocity(Bullet.BASE_VELOCITY + 5.0f);
        setCooldown(0.25f);
        setOffsetFactor(3.5f);

        addMagazine(new StandardMagazine(30, 30));
        addMagazine(new StandardMagazine(30, 30));
        addMagazine(new StandardMagazine(30, 30));
        addMagazine(new StandardMagazine(30, 30));
    }
}
