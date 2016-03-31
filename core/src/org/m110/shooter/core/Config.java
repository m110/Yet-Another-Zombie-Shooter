package org.m110.shooter.core;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Config {
    public static final String ASSETS_DIR = "core/assets/";
    public static final String AUDIO_DIR = ASSETS_DIR + "audio/";
    public static final String FONTS_DIR = ASSETS_DIR + "fonts/";
    public static final String LEVELS_DIR = ASSETS_DIR + "levels/";
    public static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
    public static final String GAME_PROPERTIES = ASSETS_DIR + "game.properties";

    public static final String CONFIG_DIR = ASSETS_DIR + "config/";
    public static final String WEAPONS_CONFIG = CONFIG_DIR + "weapons.ini";

    public static final String LEVEL_PROPERTIES = "/level.properties";

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
}
