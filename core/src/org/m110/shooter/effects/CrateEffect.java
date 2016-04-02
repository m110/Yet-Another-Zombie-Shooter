package org.m110.shooter.effects;

import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.Weapon;
import org.m110.shooter.weapons.WeaponProto;

public class CrateEffect implements Effect {

    private final Weapon weapon;

    public CrateEffect(GameScreen game, WeaponProto proto, int bullets) {
        this.weapon = Weapon.createInstance(game, proto);

        if (bullets > 0) {
            weapon.setActiveMagazineAmmo(bullets);
        }
    }

    @Override
    public boolean effect(Entity target) {
        if (!(target instanceof Player)) {
            return false;
        }

        Player player = (Player) target;
        return player.addWeapon(weapon);
    }
}
