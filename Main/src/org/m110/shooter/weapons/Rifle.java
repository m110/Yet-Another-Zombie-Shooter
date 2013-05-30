package org.m110.shooter.weapons;

import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.weapons.magazines.StandardMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Rifle extends Weapon {

    public Rifle() {
        super(2, WeaponSlot.RIFLE, "rifle");
        setBulletsCount(1);
        setDefaultMagazineCapacity(30);
        setBulletVelocity(Bullet.BASE_VELOCITY + 5.0f);
        setCooldown(0.1f);
        setReloadCooldown(0.80f);
        setOffsetFactor(3.5f);
        setDamage(20);

        addMagazine(defaultMagazineCapacity);
    }

    @Override
    public boolean addMagazine(int bullets) {
        return addMagazine(new StandardMagazine(defaultMagazineCapacity, bullets));
    }
}
