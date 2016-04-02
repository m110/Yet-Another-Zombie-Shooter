package org.m110.shooter.pickups;

import org.m110.shooter.effects.CrateEffect;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Crate extends Pickup {

    public Crate(GameScreen game, String name, float x, float y, int bullets) {
        super("crates/" + name, x, y);
        WeaponProto proto = WeaponProto.getByName(name);
        effect = new CrateEffect(game, proto, bullets);
    }
}