package org.m110.shooter.weapons;

import org.m110.shooter.actors.Bullet;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.magazines.StandardMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Pistol extends Weapon {

    public Pistol(GameScreen game) {
        super(game, 0, WeaponSlot.PISTOL, "pistol");
        setBulletsCount(1);
        setBulletVelocity(Bullet.BASE_VELOCITY);
        setCooldown(0.6f);
        setReloadCooldown(0.50f);
        setOffsetFactor(2.5f);

        addMagazine(new StandardMagazine(12, 12));
        addMagazine(new StandardMagazine(12, 12));
        addMagazine(new StandardMagazine(12, 12));
    }

}
