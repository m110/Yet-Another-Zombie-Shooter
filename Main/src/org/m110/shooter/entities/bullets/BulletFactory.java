package org.m110.shooter.entities.bullets;

import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BulletFactory {
    private BulletFactory() {}

    public static Bullet createBullet(WeaponProto proto, float x, float y, float angle) {
        switch (proto.bulletType) {
            case STANDARD:
                return new StandardBullet(proto, x, y, angle);
            case BOLD:
                return new BoldBullet(proto, x, y, angle);
            case ARROW:
                return new Arrow(proto, x, y, angle);
            default:
                throw new IllegalArgumentException("No such BulletType: " + proto.bulletType);
        }
    }
}
