package org.m110.shooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.core.Config;
import org.m110.shooter.core.Map;
import org.m110.shooter.core.MapType;
import org.m110.shooter.core.timers.CountdownTimer;
import org.m110.shooter.core.Font;
import org.m110.shooter.entities.Player;
import org.m110.shooter.input.MainInput;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.screens.HowToPlayScreen;
import org.m110.shooter.screens.MenuScreen;
import org.m110.shooter.screens.OptionsScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author m1_10sz <m110@m110.pl>
 * @version 0.1.13
 */
public class Shooter extends Game {

    public static final String VERSION = "0.1.13";

    private static Shooter INSTANCE = null;

    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private HowToPlayScreen howToPlayScreen;
    private OptionsScreen optionsScreen;

    private InputMultiplexer inputMultiplexer;

    private Player player = null;

    private final Array<Map> campaignMaps;
    private final Array<Map> survivalMaps;
    private final Array<CountdownTimer> timers;

    private Shooter() {
        campaignMaps = new Array<>();
        survivalMaps = new Array<>();
        timers = new Array<>();
    }

    public static Shooter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Shooter();
        }
        return INSTANCE;
    }

    @Override
    public void create() {
        Gdx.input.setCursorCatched(true);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new MainInput());
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Load available maps
        loadMaps();

        // Load screens
        menuScreen = new MenuScreen();
        howToPlayScreen = new HowToPlayScreen();
        optionsScreen = new OptionsScreen();

        // Start first screen
        setScreen(menuScreen);
    }

    public void loadMaps() {
        final File levelsDir = new File(Config.LEVELS_DIR);

        for (final File dir : levelsDir.listFiles()) {
            if (dir.isDirectory()) {
                String mapID = dir.getName();
                MapType mapType = null;
                int maxLevelID = 0;

                if (mapID.contains("cam")) {
                    mapType = MapType.CAMPAIGN;
                } else if (mapID.contains("sur")) {
                    mapType = MapType.SURVIVAL;
                } else {
                    continue;
                }

                for (final File level : dir.listFiles()) {
                    if (level.getName().contains(".tmx")) {
                        maxLevelID++;
                    }
                }

                Properties prop = new Properties();
                String name = "empty";
                try {
                    prop.load(new FileInputStream(Config.LEVELS_DIR + mapID + "/level.properties"));
                    name = prop.getProperty("name", "empty");
                } catch (IOException e) {
                    System.out.println("Failed to open level.properties file for map: " + mapID);
                }

                Map map = new Map(mapType, name, mapID, maxLevelID);
                switch (mapType) {
                    case CAMPAIGN:
                        campaignMaps.add(map);
                        break;
                    case SURVIVAL:
                        survivalMaps.add(map);
                        break;
                }
            }
        }
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

    /**
     * Loads level from given map.
     */
    public void loadLevel(Map map, int level) {
        if (player == null) {
            player = new Player();
        }

        gameScreen = new GameScreen(map, level, player);
        gameScreen.loadLevel();
        setScreen(gameScreen);
    }

    public void showMainMenu() {
        setScreen(menuScreen);

        // End current game
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
            player = null;
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
        Font.small.dispose();
        Font.medium.dispose();
        Font.large.dispose();
        Font.big.dispose();
    }

    public void addTimer(CountdownTimer timer) {
        timers.add(timer);
    }

    public void addInput(InputProcessor inputProcessor) {
        inputMultiplexer.addProcessor(inputProcessor);
    }

    public void removeInput(InputProcessor inputProcessor) {
        inputMultiplexer.removeProcessor(inputProcessor);
    }

    public GameScreen getGame() {
        return gameScreen;
    }

    public Player getPlayer() {
        return player;
    }

    public Array<Map> getCampaignMaps() {
        return campaignMaps;
    }

    public Array<Map> getSurvivalMaps() {
        return survivalMaps;
    }
}
