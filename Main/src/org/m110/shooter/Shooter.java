package org.m110.shooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.core.timers.CountdownTimer;
import org.m110.shooter.entities.Player;
import org.m110.shooter.input.MainInput;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.screens.HowToPlayScreen;
import org.m110.shooter.screens.MenuScreen;
import org.m110.shooter.screens.OptionsScreen;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 * @version 0.1.11
 */
public class Shooter extends Game {

    public static final String VERSION = "0.1.11";

    private static Shooter INSTANCE = null;

    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private BitmapFont largeFont;

    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private HowToPlayScreen howToPlayScreen;
    private OptionsScreen optionsScreen;

    private InputMultiplexer inputMultiplexer;

    private Player player;

    private Array<CountdownTimer> timers;

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

        Gdx.input.setCursorCatched(true);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new MainInput());
        Gdx.input.setInputProcessor(inputMultiplexer);

        player = new Player();

        timers = new Array<>();

        // Load screens
        menuScreen = new MenuScreen();
        howToPlayScreen = new HowToPlayScreen();
        optionsScreen = new OptionsScreen();

        // Start first screen
        setScreen(menuScreen);
    }

    @Override
    public void render() {
        // Update all countdown timers
        Iterator<CountdownTimer> it = timers.iterator();
        while (it.hasNext()) {
            CountdownTimer timer = it.next();
            timer.update(Gdx.graphics.getDeltaTime());
            if (timer.ready()) {
                it.remove();
            }
        }
        super.render();
    }

    public void loadLevel(String levelID) {
        gameScreen = new GameScreen(levelID, player);
        gameScreen.loadLevel();
        setScreen(gameScreen);
    }

    public void showMainMenu() {
        setScreen(menuScreen);

        // End current game
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
    }

    public void showHowToPlay() {
        setScreen(howToPlayScreen);
    }

    public void showOptions() {
        setScreen(optionsScreen);
    }

    public void resumeGame() {
        if (gameScreen != null) {
            setScreen(gameScreen);
        } else {
            setScreen(menuScreen);
        }
    }

    public void gameOver() {
        player = new Player();
        showMainMenu();
    }

    public void exitWithDelay(float delay) {
        Shooter.getInstance().addTimer(new CountdownTimer(delay) {
            @Override
            protected void action() {
                super.action();
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void dispose() {
        smallFont.dispose();
        mediumFont.dispose();
    }

    public void addTimer(CountdownTimer timer) {
        timers.add(timer);
    }

    public GameScreen getGame() {
        return gameScreen;
    }

    public Player getPlayer() {
        return player;
    }

    public void addInput(InputProcessor inputProcessor) {
        inputMultiplexer.addProcessor(inputProcessor);
    }

    public void removeInput(InputProcessor inputProcessor) {
        inputMultiplexer.removeProcessor(inputProcessor);
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
