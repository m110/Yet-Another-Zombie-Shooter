package org.m110.shooter.entities.bullets;

import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BulletFactory {
    private BulletFactory() {}

    public static Bullet createBullet(GameScreen game, WeaponProto proto, float x, float y, float angle) {
        switch (proto.bulletType) {
            case STANDARD:
                return new StandardBullet(game, proto, x, y, angle);
            case BOLD:
                return new BoldBullet(game, proto, x, y, angle);
            case ARROW:
                return new Arrow(game, proto, x, y, angle);
            default:
                throw new IllegalArgumentException("No such BulletType: " + proto.bulletType);
        }
    }
}
