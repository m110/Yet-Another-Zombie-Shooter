package org.m110.shooter.weapons;

import org.m110.shooter.entities.bullets.BulletType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author m1_10sz <m110@m110.pl>
 */

public class WeaponStat {

    public final String name;
    public final int textureID;
    public final WeaponSlot slot;

    // Weapon specific stats
    public ArrayList<WeaponMode> modes;
    public int magazineCapacity = 10;
    public int maxMagazines = 5;
    public float cooldown = 0.5f;
    public float reloadCooldown = 0.5f;
    public float recoilFactor = 2.5f;
    public int minDamage = 1;
    public int maxDamage = 1;
    public float pierceChance = 0.0f;
    public float pierceDamageFactor = 1.0f;
    public float maxRange = 0.0f;

    // Bullet specific stats
    public BulletType bulletType = BulletType.STANDARD;
    public float bulletVelocity = 0.0f;
    public int bulletsCount = 1;

    public WeaponStat(String name, int textureID, WeaponSlot slot) {
        this.name = name;
        this.textureID = textureID;
        this.slot = slot;
        modes =  new ArrayList<>();
    }

    public void addMode(WeaponMode mode) {
        modes.add(mode);
    }
}
