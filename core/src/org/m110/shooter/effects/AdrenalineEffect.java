package org.m110.shooter.effects;

import org.m110.shooter.auras.AdrenalineAura;
import org.m110.shooter.auras.Aura;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;

public class AdrenalineEffect implements Effect {
    @Override
    public boolean effect(Entity target) {
        if (!(target instanceof Player)) {
            return false;
        }

        Player player = (Player) target;
        player.addAura(new Aura(player, "adrenaline", 5.0f, new AdrenalineAura()));
        return true;
    }
}
