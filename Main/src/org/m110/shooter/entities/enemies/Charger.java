package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.ai.entity.ChargerAI;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.EntityProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Charger extends CombatEntity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Array<TextureRegion> fleshTextures;
    private static final Sound attackSound;
    private static final Sound damageSound;
    private static final Sound deathSound;

    static {
        name = "charger";
        texture = Entity.loadTexture(name);
        fleshTextures = Entity.loadFleshTextures(texture);
        attackSound = Entity.loadAttackSound(name);
        damageSound = Entity.loadDamageSound(name);
        deathSound = Entity.loadDeathSound(name);
    }

    public Charger(float startX, float startY) {
        super(EntityProto.CHARGER, texture, fleshTextures, name, startX, startY, attackSound, damageSound, deathSound);
        setAI(new ChargerAI(this));
    }
}
