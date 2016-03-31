package org.m110.shooter.weapons;

import com.badlogic.gdx.math.MathUtils;
import org.ini4j.Ini;
import org.m110.shooter.core.Config;
import org.m110.shooter.entities.bullets.BulletType;
import org.m110.shooter.weapons.magazines.MagazineType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WeaponProto {

    public final String name;
    public final int textureID;
    public final WeaponSlot slot;

    // Weapon specific stats
    public ArrayList<WeaponMode> modes;
    public MagazineType magazineType = MagazineType.STANDARD;
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
    public boolean laserSight = false;

    // Bullet specific stats
    public BulletType bulletType = BulletType.STANDARD;
    public float bulletVelocity = 0.0f;
    public int bulletsCount = 1;

    private static HashMap<String, WeaponProto> weapons;

    public WeaponProto(String name, int textureID, WeaponSlot slot) {
        this.name = name;
        this.textureID = textureID;
        this.slot = slot;
        modes =  new ArrayList<>();
    }

    public void addMode(WeaponMode mode) {
        modes.add(mode);
    }

    public static WeaponProto getByName(String name) {
        return weapons.get(name);
    }

    public static WeaponProto getRandom()  {
        Random generator = new Random();
        Object[] values = weapons.values().toArray();
        return (WeaponProto) values[generator.nextInt(values.length)];
    }

    public static void load() {
        if (weapons == null) {
            try {
                loadWeapons();
            } catch (IOException exc) {
                System.out.println("Loading weapons config failed!");
            }
        }
    }

    private static void loadWeapons() throws IOException {
        Ini config = new Ini(new File(Config.WEAPONS_CONFIG));
        weapons = new HashMap<>();

        for (String name : config.keySet()) {
            Ini.Section section = config.get(name);

            int textureID = section.get("texture", Integer.class);
            String slotName = section.get("slot");
            WeaponSlot slot = WeaponSlot.valueOf(slotName.toUpperCase());

            WeaponProto proto = new WeaponProto(name, textureID, slot);

            proto.magazineCapacity = section.get("capacity", Integer.class);
            proto.maxMagazines = section.get("magazines", Integer.class);
            proto.cooldown = section.get("cooldown", Float.class);
            proto.reloadCooldown = section.get("reload", Float.class);
            proto.recoilFactor = section.get("recoil", Float.class);
            proto.minDamage = section.get("damageMin", Integer.class);
            proto.maxDamage = section.get("damageMax", Integer.class);

            if (section.containsKey("magazineType")) {
                proto.magazineType = MagazineType.valueOf(section.get("magazineType").toUpperCase());
            }

            if (section.containsKey("pierceChance")) {
                proto.pierceChance = section.get("pierceChance", Float.class);
            }
            if (section.containsKey("pierceDamage")) {
                proto.pierceDamageFactor = section.get("pierceDamage", Float.class);
            }

            if (section.containsKey("range")) {
                proto.maxRange = section.get("range", Float.class);
            }

            if (section.containsKey("velocity")) {
                proto.bulletVelocity = section.get("velocity", Float.class);
            }

            if (section.containsKey("bullets")) {
                proto.bulletsCount = section.get("bullets", Integer.class);
            }

            if (section.containsKey("bulletType")) {
                proto.bulletType = BulletType.valueOf(section.get("bulletType").toUpperCase());
            }

            if (section.containsKey("laserSight")) {
                proto.laserSight = section.get("laserSight", Boolean.class);
            }

            if (section.get("mode") != null) {
                for (String mode : section.getAll("mode", String[].class)) {
                    proto.addMode(WeaponMode.valueOf(mode.toUpperCase()));
                }
            } else {
                proto.addMode(WeaponMode.SEMI);
            }

            weapons.put(name, proto);
        }
    }
}
