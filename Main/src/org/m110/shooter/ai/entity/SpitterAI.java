package org.m110.shooter.ai.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.bullets.GooBullet;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class SpitterAI extends AI {

    public class Goo {
        private final Entity victim;
        private final float x;
        private final float y;
        private float radius = 1.0f;
        private boolean active = true;

        private final IntervalTimer growTimer;

        private final IntervalTimer damageTimer;
        private int damage = 5;

        public Goo(Entity victim, float x, float y) {
            this.victim = victim;
            this.x = x;
            this.y = y;
            growTimer = new IntervalTimer(0.005f);
            damageTimer = new IntervalTimer(0.5f);
            damageTimer.disable();
        }

        public void draw(SpriteBatch batch, ShapeRenderer renderer) {
            if (!active) {
                return;
            }

            renderer.begin(ShapeRenderer.ShapeType.FilledCircle);
            renderer.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            renderer.filledCircle(x, y, radius);
            renderer.end();
        }

        public void act(float delta) {
            if (!active) {
                return;
            }

            growTimer.update(delta);
            if (growTimer.ready()) {
                radius += 0.5f;

                if (radius > 80.0f) {
                    active = false;
                    return;
                }

                growTimer.reset();
            }

            if (intersects()) {
                damageTimer.update(delta);
                if (damageTimer.ready()) {
                    victim.takenDamage(damage, me);
                    damage += 5;
                    damageTimer.reset();
                }
            } else {
                damage = 5;
                damageTimer.disable();
            }
        }

        private boolean intersects() {
            float distX = Math.abs(x - victim.getWorldX());
            float distY = Math.abs(y - victim.getWorldY());

            if (distX > (victim.getWidth() / 2.0f + radius)) {
                return false;
            }

            if (distY > (victim.getHeight() / 2.0f + radius)) {
                return false;
            }

            if (distX <= (victim.getWidth() / 2.0f)) {
                return true;
            }

            if (distY <= (victim.getHeight() / 2.0f)) {
                return true;
            }

            float cornerDist = (float) Math.pow(distX - victim.getWidth() / 2.0f, 2) +
                               (float) Math.pow(distY - victim.getHeight() / 2.0f, 2);

            return (cornerDist <= (radius*radius));
        }

        public boolean isActive() {
            return active;
        }
    }

    private final Array<GooBullet> bullets;
    private final Array<Goo> goos;

    private final IntervalTimer spitTimer;

    private final ShapeRenderer renderer;

    public SpitterAI(Entity me) {
        super(me);
        bullets = new Array<>();
        goos = new Array<>();
        spitTimer = new IntervalTimer(3.0f);
        renderer = new ShapeRenderer();
    }

    @Override
    public void act(float delta) {
        Iterator<GooBullet> it = bullets.iterator();
        while (it.hasNext()) {
            GooBullet bullet = it.next();
            bullet.act(delta);
            if (!bullet.isMoving()) {
                goos.add(new Goo(me.getVictim(), bullet.getX()+bullet.getOriginX(), bullet.getY()+bullet.getOriginY()));
                bullet.remove();
                it.remove();
            }
        }

        Iterator<Goo> itr = goos.iterator();
        while (itr.hasNext()) {
            Goo goo = itr.next();
            goo.act(delta);
            if (!goo.isActive()) {
                itr.remove();
            }
        }

        if (!updateVictim()) {
            return;
        }

        me.lookAt(me.getVictim());

        spitTimer.update(delta);

        if (spitTimer.ready()) {
            GooBullet bullet = new GooBullet(me, me.getVictim());
            me.getStage().addActor(bullet);
            bullets.add(bullet);
            spitTimer.reset();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.end();
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        for (Goo goo : goos) {
            goo.draw(batch, renderer);
        }
        batch.begin();
    }

    @Override
    public void afterDeath() {
        super.afterDeath();
        goos.add(new Goo(me.getVictim(), me.getWorldX(), me.getWorldY()));
    }
}
