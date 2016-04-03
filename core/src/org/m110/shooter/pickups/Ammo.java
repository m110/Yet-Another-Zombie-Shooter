package org.m110.shooter.pickups;

import org.m110.shooter.effects.AmmoEffect;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Ammo extends Pickup {

    public Ammo(PickupProto proto, float x, float y, String weapon, int bullets) {
        super(proto, x, y, weapon);

        WeaponProto weaponProto = WeaponProto.getByName(weapon);
        effect = new AmmoEffect(weaponProto, bullets);
    }
}
