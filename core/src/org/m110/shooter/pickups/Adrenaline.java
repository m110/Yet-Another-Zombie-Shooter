package org.m110.shooter.pickups;

import org.m110.shooter.auras.AdrenalineAura;
import org.m110.shooter.auras.Aura;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Adrenaline extends Pickup {

    public Adrenaline(float x, float y) {
        super("adrenaline", x, y);
    }

    @Override
    public boolean pickUp(Player player) {
        player.addAura(new Aura(player, "adrenaline", 5.0f, new AdrenalineAura()));
        return true;
    }
}
