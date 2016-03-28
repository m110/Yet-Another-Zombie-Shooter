package org.m110.shooter.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Fonts used in the game.
 * @author m1_10sz <m110@m110.pl>
 */
public class Font {
    private Font() {}

    public static final BitmapFont small;
    public static final BitmapFont medium;
    public static final BitmapFont large;
    public static final BitmapFont big;

    static  {
        small = new BitmapFont(Gdx.files.internal(Config.FONTS_DIR + "small.fnt"),
                Gdx.files.internal(Config.FONTS_DIR + "small.png"), false);
        medium = new BitmapFont(Gdx.files.internal(Config.FONTS_DIR + "medium.fnt"),
                Gdx.files.internal(Config.FONTS_DIR + "medium.png"), false);
        large = new BitmapFont(Gdx.files.internal(Config.FONTS_DIR + "large.fnt"),
                Gdx.files.internal(Config.FONTS_DIR + "large.png"), false);
        big = new BitmapFont(Gdx.files.internal(Config.FONTS_DIR + "big.fnt"),
                Gdx.files.internal(Config.FONTS_DIR + "big.png"), false);
    }
}
