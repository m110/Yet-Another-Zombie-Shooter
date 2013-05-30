package org.m110.shooter.entities.bullets;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BulletFactory {
    private BulletFactory() {}

    public static Bullet createBullet(BulletType type, float x, float y, float angle, float velocity, int damage) {
        switch (type) {
            case STANDARD:
                return new StandardBullet(x, y, angle, velocity, damage);
            case BOLD:
                return new BoldBullet(x, y, angle, velocity, damage);
            default:
                throw new IllegalArgumentException("No such BulletType: " + type);
        }
    }
}
