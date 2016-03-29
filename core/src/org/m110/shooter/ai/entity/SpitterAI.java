package org.m110.shooter.ai.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.core.timers.RandomIntervalTimer;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.bullets.GooBullet;
import org.m110.shooter.entities.enemies.CombatEntity;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class SpitterAI extends CombatAI {

    public class Goo extends Actor {
        private final Entity victim;
        private float radius = 1.0f;
        private boolean active = true;
        private boolean growing = true;
        private float growStep = 0.5f;

        private final IntervalTimer growTimer;
        private final IntervalTimer damageTimer;
        private int damage = me.getDamage();

        public Goo(Entity victim, float x, float y) {
            this.victim = victim;
            setX(x);
            setY(y);
            growTimer = new IntervalTimer(0.005f);
            damageTimer = new IntervalTimer(0.5f);
            damageTimer.disable();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (!active) {
                return;
            }

            batch.end();
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            renderer.circle(getX(), getY(), radius);
            renderer.end();
            batch.begin();
        }

        @Override
        public void act(float delta) {
            if (!active) {
                return;
            }

            growTimer.update(delta);
            if (growTimer.ready()) {
                if (growing) {
                    radius += growStep;
                } else {
                    radius -= growStep;
                }

                if (radius < 0.0f) {
                    active = false;
                    return;
                }

                if (radius > 80.0f) {
                    growStep = 0.2f;
                    growing = false;
                }

                growTimer.reset();
            }

            if (intersects()) {
                damageTimer.update(delta);
                if (damageTimer.ready()) {
                    victim.takenDamage(damage, me);
                    damage += me.getDamage();
                    damageTimer.reset();
                }
            } else {
                damage = me.getDamage();
                damageTimer.disable();
            }
        }

        private boolean intersects() {
            float distX = Math.abs(getX() - victim.getWorldX());
            float distY = Math.abs(getY() - victim.getWorldY());

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

    private final RandomIntervalTimer spitTimer;

    private final ShapeRenderer renderer;

    public SpitterAI(CombatEntity me) {
        super(me);
        bullets = new Array<>();
        goos = new Array<>();
        spitTimer = new RandomIntervalTimer(1.5f, 3.0f);
        renderer = new ShapeRenderer();
    }

    @Override
    public void act(float delta) {
        Iterator<GooBullet> it = bullets.iterator();
        while (it.hasNext()) {
            GooBullet bullet = it.next();
            if (!bullet.isMoving()) {
                spawnGoo(bullet.getX()+bullet.getOriginX(), bullet.getY()+bullet.getOriginY());
                bullet.remove();
                it.remove();
            }
        }

        Iterator<Goo> itr = goos.iterator();
        while (itr.hasNext()) {
            Goo goo = itr.next();
            if (!goo.isActive()) {
                goo.remove();
                itr.remove();
            }
        }

        if (!updateVictim()) {
            return;
        }

        me.lookAt(me.getVictim());

        spitTimer.update(delta);

        if (spitTimer.ready()) {
            GooBullet bullet = new GooBullet(game, me, me.getVictim());
            me.getStage().addActor(bullet);
            bullets.add(bullet);
            spitTimer.reset();
        } else {
            me.moveForward();
        }
    }

    private Goo spawnGoo(float x, float y) {
        Goo goo = new Goo(me.getVictim(), x, y);
        goos.add(goo);
        me.getGame().addBackgroundActor(goo);
        return goo;
    }

}
