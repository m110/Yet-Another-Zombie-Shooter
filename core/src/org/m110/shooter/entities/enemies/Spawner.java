package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.ai.entity.SpawnerAI;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.EntityProto;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Spawner extends CombatEntity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Array<TextureRegion> fleshTextures;


    static {
        name = "spawner";
        texture = Entity.loadTexture(name);
        fleshTextures = Entity.loadFleshTextures(texture);
    }

    public Spawner(GameScreen game, EntityProto spawnProto, float startX, float startY, float interval, int maxEntities) {
        super(game, EntityProto.SPAWNER, texture, fleshTextures, name, startX, startY, null, null, null);
        setAI(new SpawnerAI(this, spawnProto, interval, maxEntities));
    }
}
