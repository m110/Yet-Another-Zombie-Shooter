package org.m110.shooter.core;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Config {

    public static String VERSION;
    public static String TEXTURE_EXTENSION = ".png";

    public static class Game {
        public static final float CAMPAIGN_AGGRO_RANGE;
        public static final float SURVIVAL_AGGRO_RANGE;
        public static final boolean DRAW_DAMAGE;

        static {
            Ini config = loadIni("game.ini");
            Ini.Section section = config.get("game");

            CAMPAIGN_AGGRO_RANGE = section.get("campaignAggroRange", Float.class);
            SURVIVAL_AGGRO_RANGE = section.get("survivalAggroRange", Float.class);
            DRAW_DAMAGE = section.get("drawDamage", Boolean.class);
        }
    }

    public static class Player {
        public static final int BASE_HEALTH;

        public static final int BASE_STAMINA;
        public static final float STAMINA_USE_TIME;
        public static final float STAMINA_REGEN_TIME;

        public static final float BASE_VELOCITY;
        public static final float SPRINT_VELOCITY_BONUS;

        public static final float WEAPON_CHANGE_TIME;

        /**
         * Time in seconds between steps sound.
         */
        public static final float STEP_TIME;
        public static final int STEPS;

        static {
            Ini config = loadIni("player.ini");
            Ini.Section section = config.get("player");

            BASE_HEALTH = section.get("baseHealth", Integer.class);
            BASE_STAMINA = section.get("baseStamina", Integer.class);
            STAMINA_USE_TIME = section.get("staminaUseTime", Float.class);
            STAMINA_REGEN_TIME = section.get("staminaRegenTime", Float.class);
            BASE_VELOCITY = section.get("baseVelocity", Float.class);
            SPRINT_VELOCITY_BONUS = section.get("sprintVelocityBonus", Float.class);
            WEAPON_CHANGE_TIME = section.get("weaponChangeTime", Float.class);
            STEP_TIME = section.get("stepTime", Float.class);
            STEPS = section.get("steps", Integer.class);
        }
    }

    public static class Weapon {
        public static final float BASE_VELOCITY;
        public static final float BURST_FACTOR;

        static {
            Ini.Section section = loadIni("weapons.ini").get("base");

            BASE_VELOCITY = section.get("baseVelocity", Float.class);
            BURST_FACTOR = section.get("burstFactor", Float.class);
        }
    }

    public static class Path {
        public static final String ASSETS_DIR = "assets/";
        public static final String AUDIO_DIR = ASSETS_DIR + "audio/";
        public static final String FONTS_DIR = ASSETS_DIR + "fonts/";
        public static final String LEVELS_DIR = ASSETS_DIR + "levels/";
        public static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
        public static final String GAME_PROPERTIES = ASSETS_DIR + "game.properties";

        public static final String CONFIG_DIR = ASSETS_DIR + "config/";
        public static final String WEAPONS_CONFIG = CONFIG_DIR + "weapons.ini";
        public static final String ENTITIES_CONFIG = CONFIG_DIR + "entities.ini";
        public static final String PICKUPS_CONFIG = CONFIG_DIR + "pickups.ini";

        public static final String LEVEL_PROPERTIES = "/level.properties";
        public static final String VERSION = ASSETS_DIR + "/version";
    }

    static {
        loadVersion();
    }

    private static void loadVersion() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(Path.VERSION));
            String version = new String(bytes, StandardCharsets.UTF_8);
            VERSION = version.trim();
        } catch (IOException e) {
            VERSION = "unknown";
        }
    }

    private static Ini loadIni(String fileName) {
        try {
            return new Ini(new File(Path.CONFIG_DIR + fileName));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
