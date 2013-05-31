package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum EntityProto {
    ZOMBIE(new Builder().points(1).health(40).damage(10).velocity(4.0f).attackInterval(0.8f)),
    BOOMER(new Builder().points(10).health(300).damage(75).velocity(2.0f).attackInterval(10.0f)),
    CHARGER(new Builder().points(5).health(150).damage(40).velocity(1.0f).attackInterval(2.0f)),
    SPAWNER(new Builder().points(15).health(300).damage(0).velocity(0.0f));

    public final int points;
    public final int health;
    public final int damage;
    public final float velocity;
    public final float attackInterval;

    public static class Builder {

        private int points = 1;
        private int health = 100;
        private int damage = 10;
        private float velocity = 1.0f;
        private float attackInterval = 1.0f;

        public Builder points(int points) {
            this.points = points;
            return this;
        }

        public Builder health(int health) {
            this.health = health;
            return this;
        }

        public Builder damage(int damage) {
            this.damage = damage;
            return this;
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
        points = builder.points;
        health = builder.health;
        damage = builder.damage;
        velocity = builder.velocity;
        attackInterval = builder.attackInterval;
    }

    private static final List<EntityProto> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    public static EntityProto getRandom()  {
        return VALUES.get(MathUtils.random(VALUES.size()-1));
    }

    public static EntityProto getRandomWithoutSpawner()  {
        return VALUES.get(MathUtils.random(VALUES.size()-2));
    }
}
