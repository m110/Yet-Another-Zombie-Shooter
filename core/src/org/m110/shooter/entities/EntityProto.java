package org.m110.shooter.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.ini4j.Ini;
import org.m110.shooter.core.Config;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class EntityProto {

    public final String name;
    public int points = 0;
    public int health = 100;
    public int minDamage = 1;
    public int maxDamage = 1;
    public float velocity = 1.0f;
    public float attackInterval = 1.0f;
    public String ai = "NONE";

    // Resources
    public TextureRegion texture;
    public Array<TextureRegion> fleshTextures;
    public Sound attackSound;
    public Sound damageSound;
    public Sound deathSound;

    private static HashMap<String, EntityProto> entities;

    static {
        entities = new HashMap<>();
        try {
            loadEntities();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void loadEntities() throws IOException {
        Ini config = new Ini(new File(Config.Path.ENTITIES_CONFIG));

        for (String name : config.keySet()) {
            if (name.equals("base")) {
                continue;
            }

            Ini.Section section = config.get(name);

            EntityProto proto = new EntityProto(name);

            if (section.containsKey("points")) {
                proto.points = section.get("points", Integer.class);
            }

            if (section.containsKey("health")) {
                proto.health = section.get("health", Integer.class);
            }

            if (section.containsKey("damageMin")) {
                proto.minDamage = section.get("damageMin", Integer.class);
            }

            if (section.containsKey("damageMax")) {
                proto.maxDamage = section.get("damageMax", Integer.class);
            }

            if (section.containsKey("velocity")) {
                proto.velocity = section.get("velocity", Float.class);
            }

            if (section.containsKey("attackInterval")) {
                proto.attackInterval = section.get("attackInterval", Float.class);
            }

            if (section.containsKey("ai")) {
                proto.ai = section.get("ai", String.class).toUpperCase();
            }

            proto.texture = Entity.loadTexture(name);
            proto.fleshTextures = Entity.loadFleshTextures(proto.texture);
            proto.attackSound = Entity.loadAttackSound(name);
            proto.damageSound = Entity.loadDamageSound(name);
            proto.deathSound = Entity.loadDeathSound(name);

            entities.put(name, proto);
        }
    }

    public EntityProto(String name) {
        this.name = name;
    }

    public static EntityProto getByName(String name) {
        return entities.get(name);
    }

    public static EntityProto getRandom()  {
        Random generator = new Random();
        Object[] values = entities.values().toArray();
        return (EntityProto) values[generator.nextInt(values.length)];
    }

    public static EntityProto getRandomWithoutSpawner()  {
        Random generator = new Random();
        Object[] values = entities.values().toArray();
        values = Arrays.stream(values).filter(e -> !((EntityProto)e).name.equals("spawner")).toArray();
        return (EntityProto) values[generator.nextInt(values.length)];
    }

    public static HashMap<String, EntityProto> getEntities() {
        return entities;
    }
}
