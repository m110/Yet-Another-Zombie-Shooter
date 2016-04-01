package org.m110.shooter.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Config {

    public static String VERSION;

    public static class Game {
        public static final float CAMPAIGN_AGGRO_RANGE = 350.0f;
        public static final float SURVIVAL_AGGRO_RANGE = 5000.0f;
    }

    public static class Player {
        public static final int BASE_HEALTH = 100;

        public static final int BASE_STAMINA = 100;
        public static final float STAMINA_USE_TIME = 0.01f;
        public static final float STAMINA_REGEN_TIME = 0.1f;

        public static final float BASE_VELOCITY = 8.0f;
        public static final float SPRINT_VELOCITY_BONUS = 4.0f;

        public static final float WEAPON_CHANGE_TIME = 0.4f;

        /**
         * Time in seconds between steps sound.
         */
        public static final float STEP_TIME = 0.35f;
        public static final int STEPS = 3;
    }

    public static class Weapon {
        public static final float BASE_VELOCITY = 10.0f;
        public static final float BURST_FACTOR = 0.2f;
    }

    public static class Path {
        public static final String ASSETS_DIR = "core/assets/";
        public static final String AUDIO_DIR = ASSETS_DIR + "audio/";
        public static final String FONTS_DIR = ASSETS_DIR + "fonts/";
        public static final String LEVELS_DIR = ASSETS_DIR + "levels/";
        public static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
        public static final String GAME_PROPERTIES = ASSETS_DIR + "game.properties";

        public static final String CONFIG_DIR = ASSETS_DIR + "config/";
        public static final String WEAPONS_CONFIG = CONFIG_DIR + "weapons.ini";

        public static final String LEVEL_PROPERTIES = "/level.properties";
        public static final String VERSION = ASSETS_DIR + "/version";
    }

    public static void load() {
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
}
