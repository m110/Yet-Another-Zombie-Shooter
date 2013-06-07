package org.m110.shooter.entities;

import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.core.CoreUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum EntityProto {
    ZOMBIE(new Builder("zombie").points(1).health(20).damage(1, 5).velocity(4.0f).attackInterval(0.8f)),
    BOOMER(new Builder("boomer").points(10).health(200).damage(50, 75).velocity(2.0f).attackInterval(0.0f)),
    CHARGER(new Builder("charger").points(5).health(140).damage(10, 20).velocity(1.0f).attackInterval(0.0f)),
    SPITTER(new Builder("spitter").points(5).health(120).damage(5).velocity(0.0f)),
    SPAWNER(new Builder("spawner").points(15).health(150).damage(0).velocity(0.0f));

    public final String name;
    public final int points;
    public final int health;
    public final int minDamage;
    public final int maxDamage;
    public final float velocity;
    public final float attackInterval;

    public static class Builder {

        private final String name;
        private int points = 1;
        private int health = 100;
        private int minDamage = 1;
        private int maxDamage = 1;
        private float velocity = 1.0f;
        private float attackInterval = 1.0f;

        public Builder(String name) {
            this.name = name;
        }

        public Builder points(int points) {
            this.points = points;
            return this;
        }

        public Builder health(int health) {
            this.health = health;
            return this;
        }

        public Builder damage(int minDamage, int maxDamage) {
            this.minDamage = minDamage;
            this.maxDamage = maxDamage;
            return this;
        }

        public Builder damage(int damage) {
            return damage(damage, damage);
        }

        public Builder velocity(float velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder attackInterval(float attackInterval) {
            this.attackInterval = attackInterval;
            return this;
        }
    }

    EntityProto(Builder builder) {
        name = builder.name;
        points = builder.points;
        health = builder.health;
        minDamage = builder.minDamage;
        maxDamage = builder.maxDamage;
        velocity = builder.velocity;
        attackInterval = builder.attackInterval;
    }

    private static final List<EntityProto> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    public static EntityProto getByName(String name) {
        for (EntityProto entityProto : values()) {
            if (entityProto.name.equalsIgnoreCase(name)) {
                return entityProto;
            }
        }
        throw new IllegalArgumentException("No such EntityProto: " + name);
    }

    public static EntityProto getRandom()  {
        return VALUES.get(MathUtils.random(VALUES.size()-1));
    }

    public static EntityProto getRandomWithoutSpawner()  {
        return getRandom(ZOMBIE, BOOMER, CHARGER, SPITTER);
    }

    public static EntityProto getRandom(EntityProto... entityProtos) {
        return entityProtos[MathUtils.random(entityProtos.length-1)];
    }
}
