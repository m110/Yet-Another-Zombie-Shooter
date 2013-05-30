package org.m110.shooter.entities.bullets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BoldBullet extends Bullet {

    private static final TextureRegion texture;

    static {
        texture = Bullet.loadTexture("bold");
    }

    public BoldBullet(float x, float y, float angle, float velocity, int damage) {
        super(texture, x, y, angle, velocity, damage);
    }
}
