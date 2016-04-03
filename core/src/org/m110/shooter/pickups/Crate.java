package org.m110.shooter.pickups;

import org.m110.shooter.effects.CrateEffect;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Crate extends Pickup {

    public Crate(PickupProto proto, float x, float y, GameScreen game, String weapon, int bullets) {
        super(proto, x, y, weapon);

        WeaponProto weaponProto = WeaponProto.getByName(weapon);
        effect = new CrateEffect(game, weaponProto, bullets);
    }
}