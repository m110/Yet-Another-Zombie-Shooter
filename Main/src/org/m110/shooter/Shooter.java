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
import org.m110.shooter.screens.*;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author m1_10sz <m110@m110.pl>
 * @version 0.1.13
 */
public class Shooter extends Game {

    public static final String VERSION = "0.1.13";

    private static Shooter INSTANCE = null;

    private final Properties properties;

    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private HowToPlayScreen howToPlayScreen;
    private OptionsScreen optionsScreen;
    private EnterNameScreen enterNameScreen;
    private HighscoresScreen highscoresScreen;

    private InputMultiplexer inputMultiplexer;

    private String playerID;
    private Player player = null;

    private final Array<Map> campaignMaps;
    private final Array<Map> survivalMaps;
    private final Array<CountdownTimer> timers;

    private Shooter() {
        properties = new Properties();
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

        // Try to load properties file
        File propertiesFile = new File("assets/game.properties");
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream("assets/game.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new MainInput());
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Load available maps
        loadMaps();

        // Load screens
        menuScreen = new MenuScreen();
        howToPlayScreen = new HowToPlayScreen();
        optionsScreen = new OptionsScreen();
        enterNameScreen = new EnterNameScreen();
        highscoresScreen = new HighscoresScreen();

        // Start first screen
        if (properties.getProperty("player") != null) {
            playerID = properties.getProperty("player");
            setScreen(menuScreen);
        } else {
            setScreen(enterNameScreen);
        }
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

    public void restartLevel(Map map, int level) {
        showMainMenu();
        loadLevel(map, level);
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

    public void showHighscores() {
        highscoresScreen.updateScores(gameScreen.getMap().getMapID());
        setScreen(highscoresScreen);
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

    public void setPlayerName(String name) {
        properties.setProperty("player", name);
        playerID = name;
        storeProperties();
    }

    public void storeProperties() {
        try {
            properties.store(new FileOutputStream("assets/game.properties"), null);
        } catch (IOException e) {
            System.out.println("Could not save game.properties file!");
        }
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

    public String getPlayerID() {
        return playerID;
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
