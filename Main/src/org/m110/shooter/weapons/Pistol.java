package org.m110.shooter.weapons;

import org.m110.shooter.actors.Bullet;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Pistol extends Weapon {

    public Pistol() {
        super(0, WeaponSlot.PISTOL, "pistol");
        setBulletsCount(1);
        setBulletVelocity(Bullet.BASE_VELOCITY);
        setCooldown(0.6f);
        setOffsetFactor(2.5f);
    }

}
