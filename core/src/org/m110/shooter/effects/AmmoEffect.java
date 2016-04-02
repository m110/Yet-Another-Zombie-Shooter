package org.m110.shooter.effects;

import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;
import org.m110.shooter.weapons.WeaponProto;

public class AmmoEffect implements Effect {

    private final WeaponProto proto;
    private final int bullets;

    public AmmoEffect(WeaponProto proto, int bullets) {
        this.proto = proto;

        if (bullets == 0) {
            this.bullets = proto.magazineCapacity;
        } else {
            this.bullets = bullets;
        }
    }

    @Override
    public boolean effect(Entity target) {
        if (!(target instanceof Player)) {
            return false;
        }

        Player player = (Player) target;
        return player.addAmmo(proto, bullets);
    }
}
