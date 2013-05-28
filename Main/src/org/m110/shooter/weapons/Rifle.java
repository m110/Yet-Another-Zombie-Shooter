package org.m110.shooter.weapons;

import org.m110.shooter.actors.Bullet;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.magazines.StandardMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Rifle extends Weapon {

    public Rifle(GameScreen game) {
        super(game, 2, WeaponSlot.RILE, "rifle");
        setBulletsCount(1);
        setDefaultMagazineCapacity(30);
        setBulletVelocity(Bullet.BASE_VELOCITY + 5.0f);
        setCooldown(0.1f);
        setReloadCooldown(0.80f);
        setOffsetFactor(3.5f);
        setDamage(20);

        addMagazine(new StandardMagazine(defaultMagazineCapacity, defaultMagazineCapacity));
    }
}
