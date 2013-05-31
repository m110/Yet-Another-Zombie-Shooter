package org.m110.shooter.pickups;

import org.m110.shooter.entities.Player;
import org.m110.shooter.weapons.Weapon;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Ammo extends Pickup {

    private final WeaponProto proto;
    private final int bullets;

    public Ammo(String name, float x, float y, int bullets) {
        super(name + "_ammo", x, y);
        proto = WeaponProto.getByName(name);

        if (bullets == 0) {
            this.bullets = proto.magazineCapacity;
        } else {
            this.bullets = bullets;
        }
    }

    @Override
    public boolean pickUp(Player player) {
        Weapon weapon = player.getWeapon(proto.slot);
        if (weapon != null) {
            return weapon.addMagazine(bullets);
        } else {
            return false;
        }
    }
}
