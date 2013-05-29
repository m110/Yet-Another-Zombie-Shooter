package org.m110.shooter.weapons;

import org.m110.shooter.entities.Bullet;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.magazines.StandardMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Pistol extends Weapon {

    public Pistol() {
        super(0, WeaponSlot.PISTOL, "pistol");
        setBulletsCount(1);
        setDefaultMagazineCapacity(12);
        setBulletVelocity(Bullet.BASE_VELOCITY);
        setCooldown(0.6f);
        setReloadCooldown(0.50f);
        setOffsetFactor(2.5f);
        setDamage(15);

        addMagazine(defaultMagazineCapacity);
    }

    @Override
    public boolean addMagazine(int bullets) {
        return addMagazine(new StandardMagazine(defaultMagazineCapacity, bullets));
    }
}
