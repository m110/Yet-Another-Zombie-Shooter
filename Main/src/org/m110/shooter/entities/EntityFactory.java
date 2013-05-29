package org.m110.shooter.entities;

import org.m110.shooter.entities.enemies.Zombie;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class EntityFactory {
    private EntityFactory() {}

    public static Entity createEntity(String name, float x, float y) {
        switch (name) {
            case "zombie":
                return new Zombie(x ,y);
            default:
                throw new IllegalArgumentException("No such Entity: " + name);
        }
    }
}
