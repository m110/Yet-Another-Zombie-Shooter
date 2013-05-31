package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.ai.entity.BoomerAI;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Boomer extends HostileEntity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Sound attackSound;
    private static final Sound damageSound;
    private static final Sound deathSound;

    static {
        name = "boomer";
        texture = Entity.loadTexture(name);
        attackSound = Entity.loadAttackSound(name);
        damageSound = Entity.loadDamageSound(name);
        deathSound = Entity.loadDeathSound(name);
    }

    public Boomer(float startX, float startY) {
        super(EntityProto.BOOMER, texture, name, startX, startY, attackSound, damageSound, deathSound);
        setAI(new BoomerAI(this));
    }
}
