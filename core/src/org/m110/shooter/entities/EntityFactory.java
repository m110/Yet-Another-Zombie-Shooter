package org.m110.shooter.entities;

import com.badlogic.gdx.maps.MapObject;
import org.m110.shooter.entities.enemies.*;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class EntityFactory {
    private EntityFactory() {}

    public static CombatEntity createEntity(GameScreen game, MapObject object, float x, float y) {
        String name = object.getName();

        switch (name) {
            case "zombie": return new Zombie(game, x ,y);
            case "boomer": return new Boomer(game, x, y);
            case "charger": return new Charger(game, x, y);
            case "spawner":
                String entity = object.getProperties().get("entity", String.class);
                EntityProto spawnProto = EntityProto.getByName(entity);

                float interval = Float.parseFloat(object.getProperties().get("interval", String.class));
                int maxEntities = Integer.parseInt(object.getProperties().get("max", String.class));
                return new Spawner(game, spawnProto, x, y, interval, maxEntities);
            case "spitter": return new Spitter(game, x, y);
            default:
                throw new IllegalArgumentException("No such Entity: " + name);
        }
    }

    public static CombatEntity createEntity(GameScreen game, EntityProto proto, float x, float y) {
        switch (proto) {
            case ZOMBIE: return new Zombie(game, x, y);
            case BOOMER: return new Boomer(game, x, y);
            case CHARGER: return new Charger(game, x, y);
            case SPITTER: return new Spitter(game, x, y);
            case SPAWNER: return new Zombie(game, x, y); //return new Spawner(x, y,);
            default:
                throw new IllegalArgumentException("No such EntityProto: " + proto);
        }
    }
}
