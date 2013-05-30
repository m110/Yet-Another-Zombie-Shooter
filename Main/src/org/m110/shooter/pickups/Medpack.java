package org.m110.shooter.pickups;

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
        if (player.getHealthPercent() < 1.0f) {
            player.useMedpack(50);
            return true;
        } else {
            return false;
        }
    }
}
