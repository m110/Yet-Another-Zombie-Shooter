package org.m110.shooter.entities;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.ai.EntityAI;
import org.m110.shooter.ai.entity.AI;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.screens.GameScreen;

import java.lang.reflect.Constructor;

/**
 * @author m1_10sz <m110@m110.pl>
 */

public class CombatEntity extends Entity {

    private final EntityProto proto;
    private MapProperties properties;
    private final int points;

    private Entity victim = null;

    // Combat
    private final IntervalTimer attackTimer;

    public CombatEntity(GameScreen game, EntityProto proto, float startX, float startY, MapProperties properties) {
        super(game, proto.texture, proto.fleshTextures, proto.name, startX, startY);
        this.proto = proto;
        this.points = proto.points;
        this.properties = properties;

        setBaseHealth(proto.health);
        setVelocity(proto.velocity);

        setAttackSound(proto.attackSound);
        setDamageSound(proto.damageSound);
        setDeathSound(proto.deathSound);

        try {
            Class<?> aiClass = EntityAI.valueOf(proto.ai).AIClass;
            Constructor<?> constructor = aiClass.getConstructor(CombatEntity.class);
            AI ai = (AI) constructor.newInstance(this);
            setAI(ai);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }

        attackTimer = new IntervalTimer(proto.attackInterval);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (inCombat()) {
            if (victim.isDead()) {
                stopCombat();
            }
        }

        attackTimer.update(delta);
    }

    @Override
    public boolean isInMeleeRange(Entity who) {
        return inCombat() && super.isInMeleeRange(who);

    }

    public void dealDamage(Entity victim) {
        dealDamage(victim, getDamage());
    }

    public void dealDamage(Entity victim, int damage) {
        if (attackTimer.ready()) {
            playAttackSound();
            victim.takenDamage(damage, this);
            attackTimer.reset();
        }
    }

    public int getDamage() {
        if (proto.minDamage == proto.maxDamage) {
            return proto.minDamage;
        }

        return MathUtils.random(proto.minDamage, proto.maxDamage);
    }

    public void attackChase() {
        if (isInMeleeRange(victim)) {
            dealDamage(victim);
        } else {
            moveChase();
        }
    }

    public void moveChase() {
        moveChase(victim.getWorldX(), victim.getWorldY());
    }

    public void moveChase(float x, float y) {
        lookAt(x, y);
        moveForward();
    }

    public int getPoints() {
        return points;
    }

    public Entity getVictim() {
        return victim;
    }

    public void startCombat(Entity victim) {
        this.victim = victim;
    }

    public void stopCombat() {
        victim = null;
    }

    public boolean inCombat() {
        return victim != null;
    }

    public MapProperties getProperties() {
        return properties;
    }
}
