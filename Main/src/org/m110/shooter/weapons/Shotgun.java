package org.m110.shooter.weapons;

import org.m110.shooter.actors.Bullet;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shotgun extends Weapon {

    public Shotgun() {
        super(1, WeaponSlot.SHOTGUN, "shotgun");
        setBulletsCount(6);
        setBulletVelocity(Bullet.BASE_VELOCITY + 1.0f);
        setCooldown(0.75f);
        setOffsetFactor(6.5f);
    }
}
