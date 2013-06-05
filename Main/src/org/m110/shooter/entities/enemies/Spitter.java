package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.ai.entity.SpitterAI;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Spitter extends HostileEntity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Array<TextureRegion> fleshTextures;
    private static final Sound attackSound;
    private static final Sound damageSound;
    private static final Sound deathSound;

    static {
        name = "spitter";
        texture = Entity.loadTexture(name);
        fleshTextures = Entity.loadFleshTextures(texture);
        attackSound = Entity.loadAttackSound(name);
        damageSound = Entity.loadDamageSound(name);
        deathSound = Entity.loadDeathSound(name);
    }

    public Spitter(float startX, float startY) {
        super(EntityProto.SPITTER, texture, fleshTextures, name, startX, startY, attackSound, damageSound, deathSound);
        setAI(new SpitterAI(this));
    }
}
