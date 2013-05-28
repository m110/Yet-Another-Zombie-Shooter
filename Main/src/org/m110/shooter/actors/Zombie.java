package org.m110.shooter.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.ai.BasicAI;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Zombie extends ShooterActor {

    public Zombie(GameScreen game, float startX, float startY) {
        super(game);

        texture = new TextureRegion(new Texture(Gdx.files.internal("images/zombie.png")));
        attackSound = Gdx.audio.newSound(Gdx.files.internal("audio/zombie_attack.ogg"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("audio/zombie_damage.ogg"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("audio/zombie_death.ogg"));

        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(startX);
        setY(startY);
        setVelocity(7.0f);

        ai = new BasicAI();
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }
}
