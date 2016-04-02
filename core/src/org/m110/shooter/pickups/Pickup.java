package org.m110.shooter.pickups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.m110.shooter.core.Config;
import org.m110.shooter.effects.Effect;
import org.m110.shooter.entities.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Pickup extends Actor {

    protected TextureRegion texture;
    protected Effect effect;

    protected final String name;

    public Pickup(String name, float x, float y) {
        this.name = name;
        texture = new TextureRegion(new Texture(Gdx.files.internal(Config.Path.TEXTURES_DIR + "pickups/" + name + ".png")));
        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(x);
        setY(y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public boolean pickUp(Player player) {
        return effect.effect(player);
    }
}
