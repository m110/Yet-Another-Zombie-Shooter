package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.ai.game.GameAI;
import org.m110.shooter.ai.game.NoneAI;
import org.m110.shooter.ai.game.SurvivalAI;
import org.m110.shooter.core.StreakSystem;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.EntityProto;
import org.m110.shooter.entities.bullets.GooBullet;
import org.m110.shooter.entities.enemies.CombatEntity;
import org.m110.shooter.entities.terrain.Dummy;
import org.m110.shooter.input.GameInput;
import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.EntityFactory;
import org.m110.shooter.entities.Player;
import org.m110.shooter.entities.terrain.Fence;
import org.m110.shooter.input.GameOverInput;
import org.m110.shooter.pickups.Pickup;
import org.m110.shooter.pickups.PickupFactory;
import org.m110.shooter.screens.menu.Menu;
import org.m110.shooter.screens.menu.MenuAction;
import org.m110.shooter.screens.menu.MenuItem;
import org.m110.shooter.weapons.Weapon;
import org.m110.shooter.weapons.WeaponSlot;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameScreen implements Screen {

    private final String levelID;

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
    private final TileMapRenderer tileMapRenderer;
    private final TiledMap map;
    private final TileAtlas atlas;

    private final TiledLayer collisions;

    private final ShapeRenderer renderer;
    private final SpriteBatch batch;

    // Pause
    private boolean paused = false;
    private final Menu pauseMenu;
    private Sound pauseSound;

    private Texture crosshair;
    private final Texture leftHUD;
    private final Texture rightHUD;
    private final Texture topHUD;

    private final float mapWidth;
    private final float mapHeight;

    private final Group entitiesGroup;
    private final Group pickupsGroup;
    private final Group backgroundGroup;

    private final Array<CombatEntity> entities;
    private final Array<Pickup> pickups;
    private final Array<Fence> fences;
    private final Array<Dummy> dummies;

    private GameAI ai = NoneAI.getInstance();

    private float gameTime = 0.0f;
    private int score = 0;
    private boolean running = true;
    private final IntervalTimer bloodScreenTimer;

    private final StreakSystem streakSystem;

    private float aggroRange = 350.0f;

    private static final BitmapFont smallFont;
    private static final BitmapFont mediumFont;
    private static final BitmapFont largeFont;

    static {
        smallFont = Shooter.getInstance().getSmallFont();
        mediumFont = Shooter.getInstance().getMediumFont();
        largeFont = Shooter.getInstance().getLargeFont();
    }

    public GameScreen(String levelID, Player player) {
        this.levelID = levelID;
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
        pauseMenu.addMenuItem("Resume", new MenuAction() {
            @Override
            public void action() {
                setPaused(false);
                Gdx.input.setCursorCatched(true);
            }
        });
        pauseMenu.addMenuItem("How to play", new MenuAction() {
            @Override
            public void action() {
                Shooter.getInstance().showHowToPlay();
            }
        });
        pauseMenu.addMenuItem("Quit", new MenuAction() {
            @Override
            public void action() {
                Shooter.getInstance().showMainMenu();
            }
        });
        pauseMenu.alignToCenter();
        pauseSound = Gdx.audio.newSound(Gdx.files.internal("audio/pause.ogg"));

        // Textures
        crosshair = new Texture(Gdx.files.internal("images/crosshair.png"));
        leftHUD = new Texture(Gdx.files.internal("images/left_hud.png"));
        rightHUD = new Texture(Gdx.files.internal("images/right_hud.png"));
        topHUD = new Texture(Gdx.files.internal("images/top_hud.png"));

        // Tile map
        map = TiledLoader.createMap(Gdx.files.internal("data/level"+levelID+".tmx"));
        atlas = new TileAtlas(map, Gdx.files.internal("data/"));
        tileMapRenderer = new TileMapRenderer(map, atlas, map.tileWidth, map.tileHeight);
        collisions = map.layers.get(1);

        mapWidth = tileMapRenderer.getMapWidthUnits();
        mapHeight = tileMapRenderer.getMapHeightUnits();

        stage = new Stage();
        stage.setCamera(camera);

        entitiesGroup = new Group();
        pickupsGroup = new Group();
        backgroundGroup = new Group();
        stage.addActor(entitiesGroup);
        stage.addActor(pickupsGroup);
        stage.addActor(backgroundGroup);
        backgroundGroup.setZIndex(0);
        pickupsGroup.setZIndex(1);

        entities = new Array<>();
        pickups = new Array<>();
        fences = new Array<>();
        dummies = new Array<>();

        bloodScreenTimer = new IntervalTimer(0.7f);
        bloodScreenTimer.disable();

        streakSystem = new StreakSystem();
    }

    public void loadLevel() {
        // Update Player's GameScreen
        player.updateGame(this);

        // Check AI
        if (map.properties.containsKey("ai")) {
            switch (map.properties.get("ai")) {
                case "survival":
                    ai = new SurvivalAI();
                    break;
            }
        }

        // Load map objects
        for (TiledObject object : map.objectGroups.get(0).objects) {
            final float objX = object.x;
            final float objY = mapHeight - object.y;
            switch (object.type) {
                case "node":
                    switch (object.name) {
                        case "start":
                            player.setPosition(objX, objY);
                            // Update rotation
                            if (object.properties.containsKey("rotation")) {
                                player.setRotation(Integer.parseInt(object.properties.get("rotation")));
                            }
                            inputListener = new GameInput();
                            stage.addListener(inputListener);
                            break;
                        case "end":

                            break;
                    }
                    break;
                case "entity":
                    Entity enemy = spawnEntity(object, objX, objY);
                    // Update rotation
                    if (object.properties.containsKey("rotation")) {
                        enemy.setRotation(Integer.parseInt(object.properties.get("rotation")));
                    }
                    break;
                case "crate":
                case "ammo":
                case "bonus":
                    spawnPickup(object, objX, objY);
                    break;
                case "terrain":
                    switch (object.name) {
                        case "fence":
                            fences.add(new Fence(objX, objY));
                            break;
                        case "dummy":
                            dummies.add(new Dummy(objX, objY));
                            break;
                    }
                    break;
            }
        }

        // To ensure player will appear on top...
        entitiesGroup.addActor(player);
        // Call AI's start
        ai.start();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Update game logic
        update(delta);

        centerCamera();

        // Render background
        tileMapRenderer.render(camera, new int[]{0,1});
        // Render stage
        stage.draw();
        // Render walls
        //centerCamera();
        tileMapRenderer.render(camera, new int[]{1});
        // Draw HUD
        batch.begin();

        float topX = Gdx.graphics.getWidth() - 175;
        float topY = Gdx.graphics.getHeight() - 90;
        batch.draw(topHUD, topX, topY);
        mediumFont.setColor(Color.WHITE);
        mediumFont.draw(batch, "Level: " + levelID, topX + 15.0f, topY + 65.0f);
        mediumFont.draw(batch, "Time: " + getTimeString(), topX + 15.0f, topY + 45.0f);
        mediumFont.draw(batch, "Score: " + score, topX + 15.0f, topY + 25.0f);

        float leftX = 20;
        float leftY = 10;
        batch.draw(leftHUD, leftX, leftY);

        // Health and stamina
        mediumFont.draw(batch, "" + player.getHealth(), leftX + 210, leftY + 62);
        mediumFont.draw(batch, "" + player.getStamina(), leftX + 210, leftY + 32);

        // Health and stamina bars
        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.setColor(Color.RED);
        renderer.filledRect(leftX+41, leftY+46, player.getHealthPercent() * 158, 9);
        renderer.setColor(Color.YELLOW);
        renderer.filledRect(leftX+41, leftY+16, player.getStaminaPercent() * 158, 9);
        renderer.end();
        batch.begin();

        float rightX = Gdx.graphics.getWidth() - 260;
        float rightY = 10;
        batch.draw(rightHUD, rightX, rightY);

        Weapon weapon = player.getActiveWeapon();
        if (weapon != null) {
            batch.draw(weapon.getTexture(), rightX + 15.0f, rightY + 15.0f);
            mediumFont.draw(batch, weapon.getMode().name, rightX + 18.0f, rightY + 75.0f);
            batch.end();

            // Draw magazines
            weapon.drawMagazines(rightX + 80.0f, rightY + 13.0f, renderer, batch);

            batch.begin();
        }
        batch.end();

        // Draw the "laser" sight
        if (player.isAlive() && player.getActiveWeapon() != null &&
            player.getActiveWeapon().getProto().slot == WeaponSlot.SNIPER_RIFLE) {
            Gdx.gl.glEnable(GL10.GL_BLEND);
            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            renderer.setColor(1.0f, 0.0f, 0.0f, 0.3f);
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            float x = player.getWorldX() + (float)Math.cos(Math.toRadians(player.getRotation()))*(player.getWidth()/2.0f);
            float y = player.getWorldY() + (float)Math.sin(Math.toRadians(player.getRotation()))*(player.getHeight()/2.0f);
            renderer.line(x, y, camera.position.x + Gdx.input.getX() - Gdx.graphics.getWidth() / 2.0f,
                                camera.position.y - Gdx.input.getY() + Gdx.graphics.getHeight() / 2.0f);
            renderer.end();
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            Gdx.gl.glDisable(GL10.GL_BLEND);
        }

        // Draw notifications from bonus system
        streakSystem.draw(batch);

        if (!bloodScreenTimer.ready()) {
            Gdx.gl.glEnable(GL10.GL_BLEND);
            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
            renderer.setColor(new Color(1.0f, 0.0f, 0.0f, 0.2f * bloodScreenTimer.getTimeLeft()));
            renderer.filledRect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL10.GL_BLEND);
        }

        // Game Over?
        if (!running) {
            Gdx.gl.glEnable(GL10.GL_BLEND);
            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
            renderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
            renderer.filledRect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL10.GL_BLEND);

            batch.begin();
            largeFont.setColor(1.0f, 1.0f, 1.0f, 0.7f);
            if (player.isDead()) {
                largeFont.draw(batch, "G A M E   O V E R", 260, 450);
            } else {
                largeFont.draw(batch, "L E V E L   C O M P L E T E", 200, 450);
            }

            mediumFont.setColor(Color.WHITE);
            mediumFont.draw(batch, "Time: " + getTimeString(), 330, 390);
            mediumFont.draw(batch, "Score: " + score, 330, 370);
            mediumFont.draw(batch, "Total kills: " + streakSystem.getTotalKills(), 330, 340);
            mediumFont.draw(batch, "Best kill streak: " + streakSystem.getBestStreak(), 330, 320);
            mediumFont.draw(batch, "Press ENTER to continue...", 280, 290);
            batch.end();
        }

        if (paused) {
            Gdx.gl.glEnable(GL10.GL_BLEND);
            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
            renderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.6f));
            renderer.filledRect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL10.GL_BLEND);

            pauseMenu.draw(batch, delta);
        }

        // Check crossahair's position
        // Has to be done here, not in the update()!
        float crossX = Gdx.input.getX();
        float crossY = Gdx.input.getY();
        int newCrossX = (int) crossX;
        int newCrossY = Gdx.graphics.getHeight() - (int) crossY;

        if (crossX > Gdx.graphics.getWidth()) {
            newCrossX = Gdx.graphics.getWidth();
        }
        if (crossX < 0) {
            newCrossX = 0;
        }
        if (crossY > Gdx.graphics.getHeight()) {
            newCrossY = 0;
        }
        if (crossY < 0) {
            newCrossY = Gdx.graphics.getHeight();
        }

        if (crossX != newCrossX || Gdx.graphics.getHeight() - (int)crossY != newCrossY) {
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
            running = false;
            stage.removeListener(inputListener);
            stage.addListener(new GameOverInput());
            return;
        }

        // Check collision with pickups
        Iterator<Pickup> pit = pickups.iterator();
        while (pit.hasNext()) {
            Pickup pickup = pit.next();
            if (collidesWith(player, pickup)) {
                if (pickup.pickUp(player)) {
                    pickup.remove();
                    pit.remove();
                } else {
                    // todo print alert or somethin
                }
            }
        }

        // Check dead entities
        Iterator<CombatEntity> it = entities.iterator();
        while (it.hasNext()) {
            CombatEntity enemy = it.next();
            if (enemy.isDead()) {
                score += enemy.getPoints();
                streakSystem.addKill();
                it.remove();
            }
        }

        stage.act(delta);
        ai.act(delta);

        bloodScreenTimer.update(delta);
        streakSystem.update(delta);
    }

    protected void centerCamera() {
        float x = player.getWorldX();
        float y = player.getWorldY();

        float halfW = Gdx.graphics.getWidth() / 2.0f;
        float halfH = Gdx.graphics.getHeight() / 2.0f;
        float mapW = tileMapRenderer.getMapWidthUnits();
        float mapH = tileMapRenderer.getMapHeightUnits();

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
    }

    /**
     * Spawns new entity.
     * @return new created entity.
     */
    public Entity spawnEntity(TiledObject object, float x, float y) {
        CombatEntity entity = EntityFactory.createEntity(object, x, y);
        addEntity(entity);
        return entity;
    }

    public Entity spawnEntity(EntityProto proto, float x, float y) {
        CombatEntity entity = EntityFactory.createEntity(proto, x, y);
        addEntity(entity);
        return entity;
    }

    public Entity spawnRandomEntity(float x, float y) {
        CombatEntity entity = EntityFactory.createEntity(EntityProto.getRandom(), x, y);
        addEntity(entity);
        return entity;
    }

    public void addEntity(CombatEntity entity) {
        entities.add(entity);
        entitiesGroup.addActor(entity);
    }

    public Pickup spawnPickup(TiledObject object, float x, float y) {
        Pickup pickup = PickupFactory.createPickup(object, x, y);
        addPickup(pickup);
        return pickup;
    }

    public Pickup spawnRandomPickup(float x, float y) {
        Pickup pickup = PickupFactory.createRandomPickup(x, y);
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

    /**
     * Checks whether the object collides with terrain.
     * @return true if object collides with terrain, false otherwise.
     */
    public boolean collidesWithWall(float x, float y, Actor actor) {
        int[][] tiles = collisions.tiles;
        int h = tiles.length - 1;
        int x1 = (int)(x / 32);
        int y1 = h - (int)(y / 32);

        int x2 = (int)((x + actor.getWidth()) / 32);
        int y2 = h - (int)((y + actor.getHeight()) / 32);
        if (tiles[y1][x1] == 0 && tiles[y2][x2] == 0 &&
            tiles[y1][x2] == 0 && tiles[y2][x1] == 0) {
            if (actor instanceof Player || actor instanceof Bullet) {
                return false;
            } else {
                for (Fence fence : fences) {
                    if (collidesWith(actor, fence)) {
                        return true;
                    }
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean collidesWithEnemy(Bullet bullet) {
        if (bullet instanceof GooBullet) {
            return false;
        }

        float bx = bullet.getX();
        float by = bullet.getY();
        float bw = bullet.getWidth();
        float bh = bullet.getHeight();
        for (Entity enemy : entities) {
            float ex = enemy.getX();
            float ey = enemy.getY();
            float ew = enemy.getWidth();
            float eh = enemy.getHeight();
            if (bx < ex+ew && bx+bw > ex &&
                by < ey+eh && by+bh > ey) {
                bullet.dealDamage(player, enemy);
                return true;
            }

        }
        return false;
    }

    public boolean collidesWithEnemy(float newX, float newY, Entity actor) {
        for (Entity enemy : entities) {
            if (enemy == actor) {
                continue;
            }

            float ex = enemy.getX();
            float ey = enemy.getY();
            float ew = enemy.getWidth();
            float eh = enemy.getHeight();
            if (newX < ex+ew && newX+actor.getWidth() > ex &&
                newY < ey+eh && newY+actor.getHeight() > ey) {
                return true;
            }
        }

        if (actor != player) {
            return collidesWith(actor, player);
        }

        return false;
    }

    public boolean collidesWith(Actor a, Actor b) {
        return (a.getX() < b.getX()+b.getWidth() && a.getX()+a.getWidth() > b.getX() &&
                a.getY() < b.getY()+b.getHeight() && a.getY()+a.getHeight() > b.getY());
    }

    public boolean collidesWith(Actor a, Dummy dummy) {
        return (a.getX() < dummy.getX()+dummy.getWidth() && a.getX()+a.getWidth() > dummy.getX() &&
                a.getY() < dummy.getY()+dummy.getHeight() && a.getY()+a.getHeight() > dummy.getY());
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        if (paused) {
            Shooter.getInstance().addInput(pauseMenu);
        } else {
            Shooter.getInstance().addInput(stage);
        }
    }

    @Override
    public void hide() {
        if (paused) {
            Shooter.getInstance().removeInput(pauseMenu);
        } else {
            Shooter.getInstance().removeInput(stage);
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
        tileMapRenderer.dispose();
        atlas.dispose();
        batch.dispose();
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
            Shooter.getInstance().removeInput(stage);
            Shooter.getInstance().addInput(pauseMenu);
        } else {
            Shooter.getInstance().removeInput(pauseMenu);
            Shooter.getInstance().addInput(stage);
        }
    }

    public void afterPlayerDamage() {
        bloodScreenTimer.reset();
    }

    public Array<Dummy> getDummies() {
        return dummies;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public float getAggroRange() {
        return aggroRange;
    }

    public void setAggroRange(float aggroRange) {
        this.aggroRange = aggroRange;
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
