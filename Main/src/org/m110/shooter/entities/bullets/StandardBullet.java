package org.m110.shooter.entities.bullets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class StandardBullet extends Bullet{

    private static final TextureRegion texture;

    static {
        texture = Bullet.loadTexture("standard");
    }

    public StandardBullet(WeaponProto proto, float x, float y, float angle) {
        super(texture, proto, x, y, angle);
    }
}
