package org.m110.shooter.ai.entity;

import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.EntityProto;
import org.m110.shooter.entities.CombatEntity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;


public class SpawnerAI extends CombatAI {
    private final EntityProto spawnProto;
    private final int maxEntities;
    private IntervalTimer spawnTimer;

    private Array<Entity> entities;

    public SpawnerAI(CombatEntity me) {
        super(me);

        String entity = me.getProperties().get("entity", String.class);
        this.spawnProto = EntityProto.getByName(entity);

        this.maxEntities = Integer.parseInt(me.getProperties().get("max", String.class));

        float interval = Float.parseFloat(me.getProperties().get("interval", String.class));
        spawnTimer = new IntervalTimer(interval);

        entities = new Array<>();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (me.isDead()) {
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

    private void spawnEntity() {
        float dist = me.getWidth() * 3.0f;
        float x = me.getWorldX() + (float) Math.cos(Math.toRadians(MathUtils.random(0.0f, 360.0f))) * dist;
        float y = me.getWorldY() + (float) Math.sin(Math.toRadians(MathUtils.random(0.0f, 360.0f))) * dist;
        entities.add(game.spawnEntity(spawnProto, x, y));
    }
}
