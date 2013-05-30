package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.ai.entity.BasicAI;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Zombie extends HostileEntity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Sound attackSound;
    private static final Sound damageSound;
    private static final Sound deathSound;

    static {
        name = "zombie";
        texture = Entity.loadTexture(name);
        attackSound = Entity.loadAttackSound(name);
        damageSound = Entity.loadDamageSound(name);
        deathSound = Entity.loadDeathSound(name);
    }

    public Zombie(float startX, float startY) {
        super(texture, name, startX, startY);

        // Stats
        setVelocity(4.0f);
        setBaseHealth(40);
        setAttackInterval(0.8f);
        setAttackDamage(10);

        // Sounds
        setAttackSound(attackSound);
        setDamageSound(damageSound);
        setDeathSound(deathSound);

        ai = new BasicAI(this);
    }
}
