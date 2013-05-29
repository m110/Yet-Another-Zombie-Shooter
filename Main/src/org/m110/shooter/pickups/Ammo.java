package org.m110.shooter.pickups;

import org.m110.shooter.entities.Player;
import org.m110.shooter.weapons.Weapon;
import org.m110.shooter.weapons.WeaponSlot;
import org.m110.shooter.weapons.magazines.Magazine;
import org.m110.shooter.weapons.magazines.ShotgunMagazine;
import org.m110.shooter.weapons.magazines.StandardMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Ammo extends Pickup {

    private final WeaponSlot slot;
    private final int bullets;

    public Ammo(String name, float x, float y, int bullets) {
        super(name + "_ammo", x, y);
        this.bullets = bullets;
        slot = WeaponSlot.getByName(name);
    }

    @Override
    public boolean pickUp(Player player) {
        Weapon weapon = player.getWeapon(slot);
        if (weapon != null) {
            return weapon.addMagazine(bullets);
        } else {
            return false;
        }
    }
}