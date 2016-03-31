package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.EntityProto;
import org.m110.shooter.screens.GameScreen;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Spawner extends CombatEntity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Array<TextureRegion> fleshTextures;

    private final EntityProto spawnProto;
    private final int maxEntities;
    private IntervalTimer spawnTimer;

    private Array<Entity> entities;

    static {
        name = "spawner";
        texture = Entity.loadTexture(name);
        fleshTextures = Entity.loadFleshTextures(texture);
    }

    public Spawner(GameScreen game, EntityProto spawnProto, float startX, float startY, float interval, int maxEntities) {
        super(game, EntityProto.SPAWNER, texture, fleshTextures, name, startX, startY, null, null, null);
        this.spawnProto = spawnProto;
        this.maxEntities = maxEntities;
        spawnTimer = new IntervalTimer(interval);
        entities = new Array<>();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isDead()) {
            return;
        }

        spawnTimer.update(delta);

        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity entity = it.next();
            if (entity.isDead()) {
                it.remove();
            }
        }

        if (spawnTimer.ready() && entities.size < maxEntities) {
            spawnEntity();
            spawnTimer.reset();
        }
    }

    protected void spawnEntity() {
        float dist = getWidth() * 3.0f;
        float x = getWorldX() + (float) Math.cos(Math.toRadians(MathUtils.random(0.0f, 360.0f))) * dist;
        float y = getWorldY() + (float) Math.sin(Math.toRadians(MathUtils.random(0.0f, 360.0f))) * dist;
        entities.add(game.spawnEntity(spawnProto, x, y));
    }
}
