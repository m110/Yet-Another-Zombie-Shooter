package org.m110.shooter.weapons;

import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.entities.bullets.BulletType;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public enum WeaponProto {
    PISTOL(new Builder(0, "pistol", WeaponSlot.HANDGUN).magazineCapacity(12).cooldown(0.6f).reloadCooldown(0.5f).
            recoilFactor(2.5f).damage(4, 6).maxMagazines(5)),
    SHOTGUN(new Builder(1, "shotgun", WeaponSlot.SHOTGUN).bulletsCount(8).magazineCapacity(8).
            bulletVelocity(1.0f).cooldown(0.8f).reloadCooldown(0.7f).recoilFactor(16.0f).
            maxMagazines(10).damage(5, 7).bulletType(BulletType.BOLD)),
    RIFLE(new Builder(2, "rifle", WeaponSlot.RIFLE).magazineCapacity(30).bulletVelocity(5.0f).cooldown(0.2f).
            reloadCooldown(0.8f).recoilFactor(3.5f).damage(13, 18).mode(WeaponMode.BURST).mode(WeaponMode.AUTO).
            maxMagazines(4)),
    MP5(new Builder(3, "mp5", WeaponSlot.SMG, WeaponMode.BURST_3).magazineCapacity(32).bulletVelocity(4.5f).cooldown(0.1f).
            reloadCooldown(0.75f).recoilFactor(3.0f).damage(6, 8).mode(WeaponMode.AUTO).maxMagazines(5)),
    M40(new Builder(4, "m40", WeaponSlot.SNIPER_RIFLE).magazineCapacity(10).bulletVelocity(7.0f).cooldown(1.2f).
            reloadCooldown(1.5f).recoilFactor(0.5f).damage(40, 50).maxMagazines(3));

    public final int textureID;
    public final String name;
    public final WeaponSlot slot;
    public final EnumSet<WeaponMode> modes;
    public final int magazineCapacity;
    public final int maxMagazines;
    public final float cooldown;
    public final float reloadCooldown;
    public final float recoilFactor;
    public final int minDamage;
    public final int maxDamage;
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
        private EnumSet<WeaponMode> modes;
        private int magazineCapacity = 10;
        private int maxMagazines = 5;
        private float cooldown = 0.5f;
        private float reloadCooldown = 0.5f;
        private float recoilFactor = 2.5f;
        private int minDamage = 1;
        private int maxDamage = 1;

        // Bullet specific stats
        private BulletType bulletType = BulletType.STANDARD;
        private float bulletVelocity = 0.0f;
        private int bulletsCount = 1;

        public Builder(int textureID, String name, WeaponSlot slot) {
            this.textureID = textureID;
            this.name = name;
            this.slot = slot;
            modes = EnumSet.of(WeaponMode.SEMI);
        }

        public Builder(int textureID, String name, WeaponSlot slot, WeaponMode defaultMode) {
           this.textureID = textureID;
            this.name = name;
            this.slot = slot;
            modes = EnumSet.of(defaultMode);
        }

        public Builder mode(WeaponMode mode) {
            modes.add(mode);
            return this;
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

        public Builder damage(int minDamage, int maxDamage) {
            this.minDamage = minDamage;
            this.maxDamage = maxDamage;
            return this;
        }

        public Builder damage(int damage) {
            return damage(damage, damage);
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
        modes = builder.modes;
        magazineCapacity = builder.magazineCapacity;
        maxMagazines = builder.maxMagazines;
        cooldown = builder.cooldown;
        reloadCooldown = builder.reloadCooldown;
        recoilFactor = builder.recoilFactor;
        minDamage = builder.minDamage;
        maxDamage = builder.maxDamage;
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
