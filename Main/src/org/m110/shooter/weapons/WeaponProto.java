package org.m110.shooter.weapons;

import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.entities.bullets.BulletType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum WeaponProto {
    PISTOL(new Builder(0, "pistol", WeaponSlot.HANDGUN).magazineCapacity(12).cooldown(0.6f).reloadCooldown(0.5f).
            recoilFactor(2.5f).damage(10)),
    SHOTGUN(new Builder(1, "shotgun", WeaponSlot.SHOTGUN).bulletsCount(8).magazineCapacity(8).
            bulletVelocity(1.0f).cooldown(0.8f).reloadCooldown(0.7f).recoilFactor(16.0f).
            maxMagazines(10).damage(10).bulletType(BulletType.BOLD)),
    RIFLE(new Builder(2, "rifle", WeaponSlot.RIFLE).magazineCapacity(30).bulletVelocity(5.0f).cooldown(0.1f).
            reloadCooldown(0.8f).recoilFactor(4.5f).damage(20));

    public final int textureID;
    public final String name;
    public final WeaponSlot slot;
    public final int magazineCapacity;
    public final int maxMagazines;
    public final float cooldown;
    public final float reloadCooldown;
    public final float recoilFactor;
    public final int damage;
    public final BulletType bulletType;
    public final float bulletVelocity;
    public final int bulletsCount;
    private static final List<WeaponProto> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    public static class Builder {

        private final int textureID;
        private final String name;
        private final WeaponSlot slot;

        // Weapon specific stats
        private int magazineCapacity = 10;
        private int maxMagazines = 5;
        private float cooldown = 0.5f;
        private float reloadCooldown = 0.5f;
        private float recoilFactor = 2.5f;
        private int damage = 10;

        // Bullet specific stats
        private BulletType bulletType = BulletType.STANDARD;
        private float bulletVelocity = 0.0f;
        private int bulletsCount = 1;

        public Builder(int textureID, String name, WeaponSlot slot) {
            this.textureID = textureID;
            this.name = name;
            this.slot = slot;
        }

        public Builder magazineCapacity(int magazineCapacity) {
            this.magazineCapacity = magazineCapacity;
            return this;
        }

        public Builder maxMagazines(int maxMagazines) {
            this.maxMagazines = maxMagazines;
            return this;
        }

        public Builder cooldown(float cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Builder reloadCooldown(float reloadCooldown) {
            this.reloadCooldown = reloadCooldown;
            return this;
        }

        public Builder recoilFactor(float recoilFactor) {
            this.recoilFactor = recoilFactor;
            return this;
        }

        public Builder damage(int damage) {
            this.damage = damage;
            return this;
        }

        public Builder bulletType(BulletType bulletType) {
            this.bulletType = bulletType;
            return this;
        }

        public Builder bulletVelocity(float bulletVelocity) {
            this.bulletVelocity = bulletVelocity;
            return this;
        }

        public Builder bulletsCount(int bulletsCount) {
            this.bulletsCount = bulletsCount;
            return this;
        }
    }

    WeaponProto(Builder builder) {
        textureID = builder.textureID;
        name = builder.name;
        slot = builder.slot;
        magazineCapacity = builder.magazineCapacity;
        maxMagazines = builder.maxMagazines;
        cooldown = builder.cooldown;
        reloadCooldown = builder.reloadCooldown;
        recoilFactor = builder.recoilFactor;
        damage = builder.damage;
        bulletType = builder.bulletType;
        bulletVelocity = builder.bulletVelocity;
        bulletsCount = builder.bulletsCount;
    }

    public static WeaponProto getByName(String name) {
        for (WeaponProto weaponProto : values()) {
            if (weaponProto.name.equals(name)) {
                return weaponProto;
            }
        }
        throw new IllegalArgumentException("No such WeaponProto: " + name);
    }

    public static WeaponProto getRandom()  {
        return VALUES.get(MathUtils.random(VALUES.size()-1));
    }
}
