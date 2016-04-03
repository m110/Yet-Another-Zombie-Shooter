package org.m110.shooter.pickups;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.ini4j.Ini;
import org.m110.shooter.core.Config;
import org.m110.shooter.effects.Effect;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class PickupProto {

    public String name;
    public Effect effect;
    public HashMap<String, String> values;

    private HashMap<String, TextureRegion> textures;

    private static HashMap<String, PickupProto> pickups;

    static {
        pickups = new HashMap<>();
        try {
            loadPickups();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public TextureRegion getTexture(String textureName) {
        if (textureName == null) {
            textureName = name;
        }
        return textures.get(textureName);
    }

    private static void loadPickups() throws IOException {
        Ini config = new Ini(new File(Config.Path.PICKUPS_CONFIG));

        for (String name : config.keySet()) {
            if (name.equals("base")) {
                continue;
            }

            Ini.Section section = config.get(name);

            PickupProto proto = new PickupProto(name);

            String effectName = section.get("effect", String.class);
            proto.effect = Effect.valueOf(effectName.toUpperCase());

            String texture = section.get("texture", String.class);

            if (texture.endsWith("/")) {
                File texturesDir = new File(Config.Path.TEXTURES_DIR + "pickups/" + texture);
                for (File file : texturesDir.listFiles()) {
                    String textureName = file.getName().replace(Config.TEXTURE_EXTENSION, "");
                    proto.textures.put(textureName, Pickup.loadTexture(texture + textureName + Config.TEXTURE_EXTENSION));
                }
            } else {
                proto.textures.put(name, Pickup.loadTexture(texture + Config.TEXTURE_EXTENSION));
            }

            for (String key : section.keySet()) {
                if (name.equals("effect") || name.equals("texture")) {
                    continue;
                }
                proto.values.put(key, section.get(key));
            }

            pickups.put(name, proto);
        }
    }

    private PickupProto(String name) {
        this.name = name;
        textures = new HashMap<>();
        values = new HashMap<>();
    }

    public static PickupProto getByName(String name) {
        return pickups.get(name);
    }

    public static PickupProto getRandom()  {
        Random generator = new Random();
        Object[] values = pickups.values().toArray();
        return (PickupProto) values[generator.nextInt(values.length)];
    }
}
