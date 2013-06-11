package org.m110.shooter.pickups;

import org.m110.shooter.entities.Player;
import org.m110.shooter.weapons.Weapon;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Crate extends Pickup {

    private final int bullets;
    private final Weapon weapon;

    public Crate(String name, float x, float y, int bullets) {
        super("crates/" + name, x, y);
        this.bullets = bullets;
        weapon = Weapon.createInstance(WeaponProto.getByName(name));
        if (bullets > 0) {
            weapon.setActiveMagazineAmmo(bullets);
        }
    }

    @Override
    public boolean pickUp(Player player) {
        return player.addWeapon(weapon);
    }
}