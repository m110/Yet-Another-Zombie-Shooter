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
        small = new BitmapFont(Gdx.files.internal("fonts/small.fnt"),
                Gdx.files.internal("fonts/small.png"), false);
        medium = new BitmapFont(Gdx.files.internal("fonts/medium.fnt"),
                Gdx.files.internal("fonts/medium.png"), false);
        large = new BitmapFont(Gdx.files.internal("fonts/large.fnt"),
                Gdx.files.internal("fonts/large.png"), false);
        big = new BitmapFont(Gdx.files.internal("fonts/big.fnt"),
                Gdx.files.internal("fonts/big.png"), false);
    }
}
