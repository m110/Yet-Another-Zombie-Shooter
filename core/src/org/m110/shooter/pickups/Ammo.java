package org.m110.shooter.pickups;

import org.m110.shooter.effects.AmmoEffect;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Ammo extends Pickup {

    public Ammo(String name, float x, float y, int bullets) {
        super("ammo/" + name, x, y);
        WeaponProto proto = WeaponProto.getByName(name);

        effect = new AmmoEffect(proto, bullets);
    }
}
