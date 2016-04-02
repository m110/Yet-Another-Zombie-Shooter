package org.m110.shooter.entities;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class EntityFactory {
    private EntityFactory() {}

    public static CombatEntity createEntity(GameScreen game, MapObject object, float x, float y) {
        return createEntity(game, object.getName(), object.getProperties(), x, y);
    }

    public static CombatEntity createEntity(GameScreen game, EntityProto proto, float x, float y) {
        return createEntity(game, proto.name, new MapProperties(), x, y);
    }

    public static CombatEntity createEntity(GameScreen game, String name, MapProperties properties, float x, float y) {
        return new CombatEntity(game, EntityProto.getByName(name), x, y, properties);
    }
}
