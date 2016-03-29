package org.m110.shooter.entities.bullets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GooBullet extends Bullet {

    private static final TextureRegion texture;

    static {
        texture = Bullet.loadTexture("goo");
    }

    private final float targetX;
    private final float targetY;

    public GooBullet(GameScreen game, Entity me, Entity target) {
        super(game, texture,
                me.getWorldX() + (float)Math.cos(Math.toRadians(me.getRotation()))*(me.getWidth()*0.9f),
                me.getWorldY() + (float)Math.sin(Math.toRadians(me.getRotation()))*(me.getHeight()*0.9f),
                me.angleWith(target), -3.0f, 0, 0, 0.0f, 0.0f, 0.0f);

        targetX = target.getWorldX();
        targetY = target.getWorldY();
    }

    @Override
    public void act(float delta) {
        if (!moving) {
            return;
        }

        super.act(delta);

        if (Math.abs(getX()+getOriginX() - targetX) <= 5.0f &&
            Math.abs(getY()+getOriginY() - targetY) <= 5.0f) {
            moving = false;
        }
    }
}
