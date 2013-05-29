package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.ai.BasicAI;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Zombie extends Entity {

    private static final String name;
    private static final TextureRegion texture;
    private static final Sound attackSound;
    private static final Sound damageSound;
    private static final Sound deathSound;

    static {
        name = "zombie";
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/" + name + ".png")));
        attackSound = Entity.loadSound(name +"_attack");
        damageSound = Entity.loadSound(name + "_damage");
        deathSound = Entity.loadSound(name + "_death");
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
