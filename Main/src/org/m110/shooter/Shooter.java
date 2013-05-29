package org.m110.shooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.m110.shooter.screens.GameOverScreen;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.screens.MenuScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shooter extends Game {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private static Shooter INSTANCE = null;

    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private BitmapFont largeFont;

    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;

    private Shooter() {}

    public static Shooter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Shooter();
        }
        return INSTANCE;
    }

    @Override
    public void create() {
        smallFont = new BitmapFont(Gdx.files.internal("fonts/small.fnt"),
                                   Gdx.files.internal("fonts/small.png"), false);

        mediumFont = new BitmapFont(Gdx.files.internal("fonts/medium.fnt"),
                                   Gdx.files.internal("fonts/medium.png"), false);

        largeFont = new BitmapFont(Gdx.files.internal("fonts/large.fnt"),
                                    Gdx.files.internal("fonts/large.png"), false);

        menuScreen = new MenuScreen();
        gameScreen = new GameScreen(1);
        gameScreen.loadObjects();
        gameOverScreen = new GameOverScreen();

        setScreen(gameScreen);
    }

    public void gameOver() {
        setScreen(gameOverScreen);
        gameScreen = null;
    }

    @Override
    public void dispose() {
        smallFont.dispose();
        mediumFont.dispose();
    }

    public GameScreen getGame() {
        return gameScreen;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public BitmapFont getMediumFont() {
        return mediumFont;
    }

    public BitmapFont getLargeFont() {
        return largeFont;
    }
}
