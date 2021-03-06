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
import org.m110.shooter.weapons.WeaponProto;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shooter extends Game {

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

    public Shooter() {
        properties = new Properties();
        campaignMaps = new Array<>();
        survivalMaps = new Array<>();
        timers = new Array<>();
    }

    @Override
    public void create() {
        Gdx.input.setCursorCatched(true);

        // Try to load properties file
        File propertiesFile = new File(Config.Path.GAME_PROPERTIES);
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(Config.Path.GAME_PROPERTIES));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new MainInput());
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Load available maps
        loadMaps();

        WeaponProto.load();

        // Load screens
        menuScreen = new MenuScreen(this);
        howToPlayScreen = new HowToPlayScreen(this);
        optionsScreen = new OptionsScreen(this);
        enterNameScreen = new EnterNameScreen(this);
        highscoresScreen = new HighscoresScreen(this);

        // Start first screen
        if (properties.getProperty("player") != null) {
            playerID = properties.getProperty("player");
        }

        setScreen(menuScreen);
    }

    public void loadMaps() {
        final File levelsDir = new File(Config.Path.LEVELS_DIR);

        for (final File dir : levelsDir.listFiles()) {
            if (dir.isDirectory()) {
                String mapID = dir.getName();
                int maxLevelID = 0;

                MapType mapType;
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
                    prop.load(new FileInputStream(Config.Path.LEVELS_DIR + mapID + Config.Path.LEVEL_PROPERTIES));
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
            player = new Player(gameScreen);
        }

        // End current game
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }

        gameScreen = new GameScreen(this, map, level, player);
        gameScreen.loadLevel();
        setScreen(gameScreen);
    }

    public void restartLevel(Map map, int level) {
        showMainMenu();
        loadLevel(map, level);
    }

    public void showMainMenu() {
        setScreen(menuScreen);

        if (gameScreen != null) {
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
        setScreen(highscoresScreen);
    }

    public void exitWithDelay(float delay) {
        addTimer(new CountdownTimer(delay) {
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
            properties.store(new FileOutputStream(Config.Path.GAME_PROPERTIES), null);
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
