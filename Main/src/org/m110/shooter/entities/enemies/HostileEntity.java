package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.ai.entity.AI;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class HostileEntity extends Entity {

    private final EntityProto proto;
    private final int points;

    public HostileEntity(EntityProto proto, TextureRegion texture, String name, float startX, float startY,
                         Sound attackSound, Sound damageSound, Sound deathSound) {
        super(texture, name, startX, startY);
        this.proto = proto;
        this.points = proto.points;

        setBaseHealth(proto.health);
        setAttackDamage(proto.damage);
        setVelocity(proto.velocity);
        setAttackInterval(proto.attackInterval);

        setAttackSound(attackSound);
        setDamageSound(damageSound);
        setDeathSound(deathSound);
    }

    public int getPoints() {
        return points;
    }
}
