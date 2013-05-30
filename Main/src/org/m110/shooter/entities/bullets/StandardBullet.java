package org.m110.shooter.entities.bullets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class StandardBullet extends Bullet{

    private static final TextureRegion texture;

    public StandardBullet(float x, float y, float angle, float velocity, int damage) {
        super(x, y, angle, velocity, damage);
    }
}
