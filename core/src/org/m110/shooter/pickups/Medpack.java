package org.m110.shooter.pickups;

import org.m110.shooter.auras.Aura;
import org.m110.shooter.auras.HealAura;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Medpack extends Pickup {

    public Medpack(float x, float y) {
        super("medpack", x, y);
    }

    @Override
    public boolean pickUp(Player player) {
        player.addAura(new Aura(player, "heal", 5.0f, 10, new HealAura(50)));
        return true;
    }
}
