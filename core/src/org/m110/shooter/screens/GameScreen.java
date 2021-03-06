package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.ai.game.GameAI;
import org.m110.shooter.ai.game.NoneAI;
import org.m110.shooter.ai.game.SurvivalAI;
import org.m110.shooter.auras.Aura;
import org.m110.shooter.core.*;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.EntityFactory;
import org.m110.shooter.entities.EntityProto;
import org.m110.shooter.entities.Player;
import org.m110.shooter.entities.CombatEntity;
import org.m110.shooter.entities.terrain.Dummy;
import org.m110.shooter.entities.terrain.Fence;
import org.m110.shooter.input.GameInput;
import org.m110.shooter.input.GameOverInput;
import org.m110.shooter.pickups.Pickup;
import org.m110.shooter.pickups.PickupFactory;
import org.m110.shooter.screens.menu.Menu;
import org.m110.shooter.weapons.Weapon;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameScreen extends ShooterScreen {

    private final Map map;
    private final int level;

    /**
     * Scene2D Stage, handles entities.
     */

    private final Stage stage;
    private Player player = null;
    private GameInput inputListener;

    private final OrthographicCamera camera;

    /**
     * Tiled map related stuff.
     */
    private final OrthogonalTiledMapRenderer tileMapRenderer;
    private final TiledMap tiledMap;

    private final ShapeRenderer renderer;
    private final SpriteBatch batch;

    private final Collision collision;

    // Pause
    private boolean paused = false;
    private final Menu pauseMenu;
    private Sound pauseSound;

    private Texture crosshair;
    private final Texture leftHUD;
    private final Texture rightHUD;
    private final Texture topHUD;

    private final Group entitiesGroup;
    private final Group pickupsGroup;
    private final Group backgroundGroup;

    private Dummy startNode;
    private final Array<Dummy> endNodes;
    private final Array<CombatEntity> entities;
    private final Array<Pickup> pickups;
    private final Array<Fence> fences;
    private final Array<Dummy> dummies;

    private GameAI ai = NoneAI.getInstance();

    private float gameTime = 0.0f;
    private int score = 0;
    private boolean running = true;
    private final IntervalTimer bloodScreenTimer;

    private final Stats stats;

    private float aggroRange = Config.Game.CAMPAIGN_AGGRO_RANGE;

    private final int[] backgroundLayer = new int[]{0};
    private final int[] wallsLayer = new int[]{1};

    public GameScreen(final Shooter shooter, final Map map, final int level, Player player) {
        super(shooter);
        this.map = map;
        this.level = level;
        this.player = player;

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        // Rendering objects
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        // Pause menu
        pauseMenu = new Menu(0.0f, Gdx.graphics.getHeight() * 0.6f);
        pauseMenu.addMenuItem("Resume", () -> {
            setPaused(false);
            Gdx.input.setCursorCatched(true);
        });
        pauseMenu.addMenuItem("Restart", () -> shooter.restartLevel(map, level));
        pauseMenu.addMenuItem("How to play", shooter::showHowToPlay);
        pauseMenu.addMenuItem("Quit", shooter::showMainMenu);
        pauseMenu.alignToCenter();
        pauseSound = Gdx.audio.newSound(Gdx.files.internal(Config.Path.AUDIO_DIR + "pause.ogg"));

        // Textures
        crosshair = new Texture(Gdx.files.internal(Config.Path.TEXTURES_DIR + "hud/crosshair.png"));
        leftHUD = new Texture(Gdx.files.internal(Config.Path.TEXTURES_DIR + "hud/left.png"));
        rightHUD = new Texture(Gdx.files.internal(Config.Path.TEXTURES_DIR + "hud/right.png"));
        topHUD = new Texture(Gdx.files.internal(Config.Path.TEXTURES_DIR + "hud/top.png"));

        // Tile map
        tiledMap = map.getTiledMap(level);
        tileMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f);

        collision = new Collision(this, (TiledMapTileLayer)tiledMap.getLayers().get("walls"));

        stage = new Stage();
        stage.getViewport().setCamera(camera);

        entitiesGroup = new Group();
        pickupsGroup = new Group();
        backgroundGroup = new Group();
        stage.addActor(entitiesGroup);
        stage.addActor(pickupsGroup);
        stage.addActor(backgroundGroup);
        backgroundGroup.setZIndex(0);
        pickupsGroup.setZIndex(1);

        endNodes = new Array<>();
        entities = new Array<>();
        pickups = new Array<>();
        fences = new Array<>();
        dummies = new Array<>();

        bloodScreenTimer = new IntervalTimer(0.7f);
        bloodScreenTimer.disable();

        stats = new Stats();
    }

    public void loadLevel() {
        player.updateGame(this);

        loadLevelAI();
        loadMapObjects();

        // To ensure player will appear on top...
        entitiesGroup.addActor(player);

        ai.start();
    }

    private void loadLevelAI() {
        switch (map.getMapType()) {
            case SURVIVAL:
                ai = new SurvivalAI(this);
                break;
        }
    }

    private void loadMapObjects() {
        for (MapObject object : tiledMap.getLayers().get("actors").getObjects()) {
            final float objX = object.getProperties().get("x", Float.class);
            final float objY = object.getProperties().get("y", Float.class);

            String type = object.getProperties().get("type", String.class);
            String name = object.getName();

            switch (type) {
                case "node":
                    if (name.equals("start")) {
                        startNode = new Dummy(objX, objY, getTileWidth(), getTileHeight());
                        player.setPosition(objX, objY);
                        // Update rotation
                        if (object.getProperties().containsKey("rotation")) {
                            player.setRotation(Integer.parseInt(object.getProperties().get("rotation", String.class)));
                        }
                        inputListener = new GameInput(player);
                        stage.addListener(inputListener);
                    } else if (name.equals("end")) {
                            endNodes.add(new Dummy(objX, objY, getTileWidth(), getTileHeight()));
                    }
                    break;
                case "entity":
                case "spawner":
                    Entity enemy = spawnEntity(object, objX, objY);
                    // Update rotation
                    if (object.getProperties().containsKey("rotation")) {
                        enemy.setRotation(Integer.parseInt(object.getProperties().get("rotation", String.class)));
                    }
                    break;
                case "crate":
                case "ammo":
                case "medpack":
                case "adrenaline":
                    spawnPickup(object, objX, objY);
                    break;
                case "terrain":
                    if (name.equals("fence")) {
                        fences.add(new Fence(objX, objY));
                    } else if (name.equals("dummy")) {
                        dummies.add(new Dummy(objX, objY));
                    }
                    break;
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update game logic
        update(delta);

        centerCamera();

        tileMapRenderer.setView(camera);
        // Render background
        tileMapRenderer.render(backgroundLayer);
        // Render stage
        stage.draw();
        // Render walls
        tileMapRenderer.render(wallsLayer);
        // Draw HUD
        batch.begin();

        float scoreX = -70.0f;
        float scoreY = Gdx.graphics.getHeight() - 40.0f;
        batch.draw(rightHUD, scoreX, scoreY);
        Font.large.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        Font.large.draw(batch, String.format("%08d", score), 10.0f, scoreY + Font.large.getLineHeight() + 5.0f);

        float topX = Gdx.graphics.getWidth() - 175;
        float topY = Gdx.graphics.getHeight() - 90;
        batch.draw(topHUD, topX, topY);
        Font.medium.setColor(Color.WHITE);
        Font.medium.draw(batch, "Map: " + map.getName(), topX + 15.0f, topY + 65.0f);
        Font.medium.draw(batch, "Level: " + level, topX + 15.0f, topY + 45.0f);
        Font.medium.draw(batch, "Time: " + getTimeString(), topX + 15.0f, topY + 25.0f);

        float leftX = 20;
        float leftY = 10;
        batch.draw(leftHUD, leftX, leftY);
        batch.end();

        Array<Aura> auras = player.getAuras();
        float auraX = leftX + 20.0f;
        for (Aura aura : auras) {
            batch.begin();
            batch.draw(aura.getTexture(), auraX, leftY + leftHUD.getHeight() + 10.0f);
            batch.end();
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderer.setColor(0.25f, 0.25f, 0.25f, 0.6f);
            renderer.rect(auraX, leftY + leftHUD.getHeight() + 10.0f, aura.getTexture().getRegionWidth(),
                    aura.getTexture().getRegionHeight() * aura.getDonePercent());
            renderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            auraX += aura.getTexture().getRegionWidth() + 5.0f;
        }

        batch.begin();
        // Health and stamina
        Font.medium.draw(batch, "" + player.getHealth(), leftX + 210, leftY + 62);
        Font.medium.draw(batch, "" + player.getStamina(), leftX + 210, leftY + 32);

        // Health and stamina bars
        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.RED);
        renderer.rect(leftX+41, leftY+46, player.getHealthPercent() * 158, 9);
        renderer.setColor(Color.YELLOW);
        renderer.rect(leftX+41, leftY+16, player.getStaminaPercent() * 158, 9);
        renderer.end();
        batch.begin();

        float rightX = Gdx.graphics.getWidth() - 260;
        float rightY = 10;
        batch.draw(rightHUD, rightX, rightY);

        Weapon weapon = player.getActiveWeapon();
        if (weapon != null) {
            batch.draw(weapon.getTexture(), rightX + 15.0f, rightY + 15.0f);
            Font.medium.draw(batch, weapon.getMode().name, rightX + 18.0f, rightY + 75.0f);
            batch.end();

            // Draw magazines
            weapon.drawMagazines(rightX + 80.0f, rightY + 13.0f, renderer, batch);

            batch.begin();
        }
        batch.end();

        // Draw the "laser" sight
        if (player.isAlive() && player.getActiveWeapon() != null &&
            player.getActiveWeapon().getProto().laserSight) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderer.setColor(1.0f, 0.0f, 0.0f, 0.3f);
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            float x = player.getWorldX() + (float)Math.cos(Math.toRadians(player.getRotation()))*(player.getWidth()/2.0f);
            float y = player.getWorldY() + (float)Math.sin(Math.toRadians(player.getRotation()))*(player.getHeight()/2.0f);
            renderer.line(x, y, camera.position.x + Gdx.input.getX() - Gdx.graphics.getWidth() / 2.0f,
                                camera.position.y - Gdx.input.getY() + Gdx.graphics.getHeight() / 2.0f);
            renderer.end();
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        if (!bloodScreenTimer.ready()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(new Color(1.0f, 0.0f, 0.0f, 0.2f * bloodScreenTimer.getTimeLeft()));
            renderer.rect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        // Game Over?
        if (!running) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
            renderer.rect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
            Font.large.setColor(1.0f, 1.0f, 1.0f, 0.7f);
            if (player.isDead()) {
                Font.large.draw(batch, "G A M E   O V E R", 260, 450);
            } else {
                Font.large.draw(batch, "L E V E L   C O M P L E T E", 200, 450);
            }

            Font.medium.setColor(Color.WHITE);
            Font.medium.draw(batch, "Time: " + getTimeString(), 330, 390);
            Font.medium.draw(batch, "Score: " + score, 330, 370);
            Font.medium.draw(batch, "Total kills: " + stats.getKills(), 330, 340);
            Font.medium.draw(batch, "Press ENTER to continue...", 280, 290);
            batch.end();
        }

        if (paused) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.6f));
            renderer.rect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            pauseMenu.draw(batch, delta);
        }

        // Has to be done here, not in the update()!
        updateCrosshair();
    }

    private void updateCrosshair() {
        float crossX = Gdx.input.getX();
        float crossY = Gdx.input.getY();
        int newCrossX = (int) crossX;
        int newCrossY = (int) crossY;

        if (crossX > Gdx.graphics.getWidth()) {
            newCrossX = Gdx.graphics.getWidth();
        }
        if (crossX < 0) {
            newCrossX = 0;
        }
        if (crossY > Gdx.graphics.getHeight()) {
            newCrossY = Gdx.graphics.getHeight();
        }
        if (crossY < 0) {
            newCrossY = 0;
        }

        if (crossX != newCrossX || crossY != newCrossY) {
            Gdx.input.setCursorPosition(newCrossX, newCrossY);
        }

        // Draw crosshair
        batch.begin();
        batch.draw(crosshair, Gdx.input.getX() - crosshair.getWidth() / 2,
                   Gdx.graphics.getHeight() - Gdx.input.getY() - crosshair.getHeight() / 2);
        batch.end();
    }

    protected void update(float delta) {
        if (!running || paused) {
            return;
        }

        gameTime += delta;

        // Is player dead? (Game Over)
        if (player.isDead()) {
            gameOver();
            return;
        }

        // Check collision with end nodes
        for (Dummy endNode : endNodes) {
            if (collision.check(player, endNode)) {
                gameOver();
                return;
            }
        }

        // Check collision with pickups
        Iterator<Pickup> pit = pickups.iterator();
        while (pit.hasNext()) {
            Pickup pickup = pit.next();
            if (collision.check(player, pickup)) {
                if (pickup.pickUp(player)) {
                    pickup.remove();
                    pit.remove();
                } else {
                    // Can't pick up that object
                    // TODO Tell player ammo is full or something similar
                }
            }
        }

        // Check dead entities
        Iterator<CombatEntity> it = entities.iterator();
        while (it.hasNext()) {
            CombatEntity enemy = it.next();
            if (enemy.isDead()) {
                int points = enemy.getPoints();

                score += points;
                stats.addKill();
                it.remove();
            }
        }

        stage.act(delta);
        ai.act(delta);

        bloodScreenTimer.update(delta);
    }

    protected void centerCamera() {
        float x = player.getWorldX();
        float y = player.getWorldY();

        float halfW = Gdx.graphics.getWidth() / 2.0f;
        float halfH = Gdx.graphics.getHeight() / 2.0f;

        float mapW = getMapWidth();
        float mapH = getMapHeight();

        if (x < halfW) {
            x = halfW;
        } else if (x > mapW - halfW) {
            x = mapW - halfW;
        }

        if (y < halfH) {
            y = halfH;
        } else if (y > mapH - halfH) {
            y = mapH - halfH;
        }

        camera.position.set(x, y, 0);
        camera.update();
    }

    protected void gameOver() {
        running = false;
        stage.removeListener(inputListener);
        stage.addListener(new GameOverInput(shooter));

        // TODO save scores if survival, in local storage
    }

    /**
     * Spawns new entity.
     * @return new created entity.
     */
    public Entity spawnEntity(MapObject object, float x, float y) {
        CombatEntity entity = EntityFactory.createEntity(this, object, x, y);
        addEntity(entity);
        return entity;
    }

    public Entity spawnEntity(EntityProto proto, float x, float y) {
        CombatEntity entity = EntityFactory.createEntity(this, proto, x, y);
        addEntity(entity);
        return entity;
    }

    public Entity spawnRandomEntity(float x, float y) {
        CombatEntity entity = EntityFactory.createEntity(this, EntityProto.getRandomWithoutSpawner(), x, y);
        addEntity(entity);
        return entity;
    }

    public void addEntity(CombatEntity entity) {
        entities.add(entity);
        entitiesGroup.addActor(entity);
    }

    public Pickup spawnPickup(MapObject object, float x, float y) {
        Pickup pickup = PickupFactory.createPickup(this, object, x, y);
        addPickup(pickup);
        return pickup;
    }

    public Pickup spawnRandomPickup(float x, float y) {
        Pickup pickup = PickupFactory.createRandomPickup(this, x, y);
        addPickup(pickup);
        return pickup;
    }

    public void addPickup(Pickup pickup) {
        pickups.add(pickup);
        pickupsGroup.addActor(pickup);
        pickup.setZIndex(0);
    }

    public void addBackgroundActor(Actor actor) {
        backgroundGroup.addActor(actor);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setWorldSize(width, height);
    }

    @Override
    public void show() {
        if (paused) {
            shooter.addInput(pauseMenu);
        } else {
            shooter.addInput(stage);
        }
    }

    @Override
    public void hide() {
        if (paused) {
            shooter.removeInput(pauseMenu);
        } else {
            shooter.removeInput(stage);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
        batch.dispose();
        tiledMap.dispose();
        renderer.dispose();
        crosshair.dispose();
        leftHUD.dispose();
        rightHUD.dispose();

        entities.clear();
        entitiesGroup.clear();
        pickups.clear();
        fences.clear();
    }

    public void setPaused(boolean paused) {
        if (this.paused == paused) {
            return;
        }

        pauseSound.play();
        this.paused = paused;
        if (paused) {
            shooter.removeInput(stage);
            shooter.addInput(pauseMenu);
        } else {
            shooter.removeInput(pauseMenu);
            shooter.addInput(stage);
        }
    }

    public void afterPlayerDamage() {
        bloodScreenTimer.reset();
    }

    public Array<Dummy> getDummies() {
        return dummies;
    }

    public Map getMap() {
        return map;
    }

    public int getLevel() {
        return level;
    }

    // TODO Move those two where they are being used
    public float getTileWidth() {
        return ((TiledMapTileLayer)tiledMap.getLayers().get("walls")).getTileWidth();
    }

    public float getTileHeight() {
        return ((TiledMapTileLayer)tiledMap.getLayers().get("walls")).getTileHeight();
    }

    public float getMapWidth() {
        TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get("walls");
        return layer.getTileWidth() * layer.getWidth();
    }

    public float getMapHeight() {
        TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get("walls");
        return layer.getTileWidth() * layer.getWidth();
    }

    public float getAggroRange() {
        return aggroRange;
    }

    public void setAggroRange(float aggroRange) {
        this.aggroRange = aggroRange;
    }

    public Dummy getStartNode() {
        return startNode;
    }

    public Array<Fence> getFences() {
        return fences;
    }

    public Array<CombatEntity> getEntities() {
        return entities;
    }

    public Player getPlayer() {
        return player;
    }

    public Collision getCollision() {
        return collision;
    }

    public String getTimeString() {
        String minutes = Integer.toString((int)(gameTime / 60.0f));
        if (minutes.length() < 2) minutes = "0" + minutes;
        String seconds = Integer.toString((int) gameTime % 60);
        if (seconds.length() < 2) seconds = "0" + seconds;
        String milis = Integer.toString((int)(100.0f * (gameTime - (int)gameTime)));
        if (milis.length() < 2) milis = "0" + milis;

        return minutes + ":" + seconds + ":" + milis;
    }
}
