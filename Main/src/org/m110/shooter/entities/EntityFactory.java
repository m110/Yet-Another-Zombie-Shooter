package org.m110.shooter.entities;

import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import org.m110.shooter.entities.enemies.*;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class EntityFactory {
    private EntityFactory() {}

    public static HostileEntity createEntity(TiledObject object, float x, float y) {
        switch (object.name) {
            case "zombie": return new Zombie(x ,y);
            case "boomer": return new Boomer(x, y);
            case "charger": return new Charger(x, y);
            case "spawner":
                String entity = object.properties.get("entity");
                float interval = Float.parseFloat(object.properties.get("interval"));
                int maxEntities = Integer.parseInt(object.properties.get("max"));
                return new Spawner(x, y, entity, interval, maxEntities);
            default:
                throw new IllegalArgumentException("No such Entity: " + object.name);
        }
    }

    public static HostileEntity createRandomEntity(float x, float y) {
        EntityProto type = EntityProto.getRandom();
        switch (type) {
            case ZOMBIE:
            case SPAWNER: // Just to increase chances...
                return new Zombie(x ,y);
            case BOOMER: return new Boomer(x, y);
            case CHARGER: return new Charger(x, y);
            default:
                return null;
        }
    }
}
