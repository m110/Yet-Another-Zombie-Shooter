package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.ai.BoomerAI;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Boomer extends Entity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Sound attackSound;
    private static final Sound damageSound;
    private static final Sound deathSound;

    static {
        name = "boomer";
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/" + name + ".png")));
        attackSound = Entity.loadSound(name +"_attack");
        damageSound = Entity.loadSound(name + "_damage");
        deathSound = Entity.loadSound(name + "_death");
    }

    public Boomer(float startX, float startY) {
        super(texture, name, startX, startY);

        // Stats
        setVelocity(2.0f);
        setBaseHealth(300);
        setAttackDamage(75);
        setAttackInterval(10.0f);

        // Sounds
        setAttackSound(attackSound);
        setDamageSound(damageSound);
        setDeathSound(deathSound);

        ai = new BoomerAI(this);
    }
}
