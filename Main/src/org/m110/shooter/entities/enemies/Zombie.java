package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.Gdx;
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

    public Zombie(float startX, float startY) {
        super("zombie", startX, startY);

        // Stats
        setVelocity(4.0f);
        setBaseHealth(40);
        setAttackInterval(0.8f);
        setAttackDamage(10);

        ai = new BasicAI(this);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }
}
