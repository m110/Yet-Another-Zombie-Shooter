package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.ai.entity.ChargerAI;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Charger extends HostileEntity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Sound attackSound;
    private static final Sound damageSound;
    private static final Sound deathSound;

    static {
        name = "charger";
        texture = Entity.loadTexture(name);
        attackSound = Entity.loadAttackSound(name);
        damageSound = Entity.loadDamageSound(name);
        deathSound = Entity.loadDeathSound(name);
    }

    public Charger(float startX, float startY) {
        super(EntityProto.CHARGER, texture, name, startX, startY, attackSound, damageSound, deathSound);
        setAI(new ChargerAI(this));
    }
}
