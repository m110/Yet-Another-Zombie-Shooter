package org.m110.shooter.pickups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.m110.shooter.actors.Player;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Pickup extends Actor {

    protected TextureRegion texture;

    protected final String name;

    public Pickup(String name, float x, float y, String texturePostfix) {
        this.name = name;
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/"+name+"_"+texturePostfix+".png")));
        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(x);
        setY(y);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public abstract boolean pickUp(Player player);
}
