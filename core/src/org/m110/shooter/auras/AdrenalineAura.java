package org.m110.shooter.auras;

import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;

public class AdrenalineAura implements AuraEffect {

    @Override
    public void apply(Entity owner) {
        if (!(owner instanceof Player)) {
            return;
        }

        Player player = (Player) owner;
        player.useAdrenaline();
    }

    @Override
    public void wearOff(Entity owner) {
        if (!(owner instanceof Player)) {
            return;
        }

        Player player = (Player) owner;
        player.stopAdrenaline();
    }
}
