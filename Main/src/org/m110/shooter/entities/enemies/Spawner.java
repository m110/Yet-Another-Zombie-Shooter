package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Spawner extends HostileEntity {

    private static final String name;
    private static final TextureRegion texture;

    private final String entity;
    private final int maxEntities;
    private IntervalTimer spawnTimer;

    private Array<Entity> entities;

    static {
        name = "spawner";
        texture = Entity.loadTexture(name);
    }

    public Spawner(float startX, float startY, String entity, float interval, int maxEntities) {
        super(texture, name, startX, startY);
        this.entity = entity;
        this.maxEntities = maxEntities;
        spawnTimer = new IntervalTimer(interval);
        entities = new Array<>();

        // Stats
        setBaseHealth(300);
    }

    @Override
    public void act(float delta) {
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
        TiledObject object = new TiledObject();
        object.name = entity;
        entities.add(Shooter.getInstance().getGame().spawnEntity(object, x, y));
    }
}
