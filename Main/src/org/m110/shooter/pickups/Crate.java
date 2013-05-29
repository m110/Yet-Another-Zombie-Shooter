package org.m110.shooter.pickups;

import org.m110.shooter.entities.Player;
import org.m110.shooter.weapons.Pistol;
import org.m110.shooter.weapons.Rifle;
import org.m110.shooter.weapons.Shotgun;
import org.m110.shooter.weapons.Weapon;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Crate extends Pickup {

    private final int bullets;
    private final Weapon weapon;

    public Crate(String name, float x, float y, int bullets) {
        super(name + "_crate", x, y);
        this.bullets = bullets;
        weapon = Weapon.getByName(name);
    }

    @Override
    public boolean pickUp(Player player) {
        if (bullets > 0) {
            weapon.setActiveMagazineAmmo(bullets);
        }
        return player.addWeapon(weapon);
    }
}