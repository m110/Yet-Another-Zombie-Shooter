package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
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
import org.m110.shooter.screens.menu.MenuItem;
import org.m110.shooter.weapons.Weapon;

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

    /**
     * Box2d World, handles basic physics.
     */
    private final World world;

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

    private Texture crosshair;
    private final Texture leftHUD;
    private final Texture rightHUD;

    private final float mapWidth;
    private final float mapHeight;

    private final Group actorsGroup;
    private final Array<Entity> entities;
    private final Array<Pickup> pickups;
    private final Array<Fence> fences;

    private boolean running = true;

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

        // Initialize world
        world = new World(new Vector2(0.0f, 0.0f), true);

        // Rendering objects
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        // Pause menu
        pauseMenu = new Menu(Gdx.graphics.getWidth() / 2.0f - 100.0f, Gdx.graphics.getHeight() / 2.0f + 100.0f);
        pauseMenu.addMenuItem(new MenuItem("Resume") {
            @Override
            public void action() {
                setPaused(false);
                Gdx.input.setCursorCatched(true);
            }
        });
        pauseMenu.addMenuItem(new MenuItem("How to play") {
            @Override
            public void action() {
                Shooter.getInstance().showHowToPlay();
            }
        });
        pauseMenu.addMenuItem(new MenuItem("Quit") {
            @Override
            public void action() {
                Shooter.getInstance().showMainMenu();
            }
        });
        pauseMenu.alignToCenter();

        // Textures
        crosshair = new Texture(Gdx.files.internal("images/crosshair.png"));
        leftHUD = new Texture(Gdx.files.internal("images/left_hud.png"));
        rightHUD = new Texture(Gdx.files.internal("images/right_hud.png"));

        // Tile map
        map = TiledLoader.createMap(Gdx.files.internal("data/level"+levelID+".tmx"));
        atlas = new TileAtlas(map, Gdx.files.internal("data/"));
        tileMapRenderer = new TileMapRenderer(map, atlas, map.tileWidth, map.tileHeight);
        collisions = map.layers.get(1);

        mapWidth = tileMapRenderer.getMapWidthUnits();
        mapHeight = tileMapRenderer.getMapHeightUnits();

        stage = new Stage();
        stage.setCamera(camera);

        actorsGroup = new Group();
        stage.addActor(actorsGroup);
        entities = new Array<>();
        pickups = new Array<>();
        fences = new Array<>();
    }

    public void loadLevel() {
        // Update Player's GameScreen
        player.updateGame(this);

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
                    }
                    break;
            }
        }

        // To ensure player will appear on top...
        actorsGroup.addActor(player);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Update game logic
        update(delta);

        centerCamera();
        tileMapRenderer.render(camera);
        stage.draw();

        // Check dead entities
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity enemy = it.next();
            if (enemy.isDead()) {
                it.remove();
            }
        }

        // Draw HUD
        batch.begin();
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
        renderer.filledRect(leftX+41, leftY+16, (float)player.getStamina()/100 * 158, 9);
        renderer.end();
        batch.begin();

        float rightX = Gdx.graphics.getWidth() - 260;
        float rightY = 10;
        batch.draw(rightHUD, rightX, rightY);

        Weapon weapon = player.getActiveWeapon();
        if (weapon != null) {
            batch.draw(weapon.getTexture(), rightX + 15, rightY + 15);
            batch.end();

            // Draw magazines
            weapon.drawMagazines(rightX + 80, rightY + 13, renderer, batch);

            batch.begin();
        }
        batch.end();

        // Game Over?
        if (player.isDead()) {
            Gdx.gl.glEnable(GL10.GL_BLEND);
            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
            renderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.6f));
            renderer.filledRect(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL10.GL_BLEND);

            batch.begin();
            largeFont.draw(batch, "GAME OVER", 315, 400);
            mediumFont.draw(batch, "Press ENTER to continue...", 280, 370);
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

        // Is player dead? (Game Over)
        if (player.isDead()) {
            running = false;
            stage.removeListener(inputListener); // seems bugged?
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
                    // todo print alert of somethin
                }
            }
        }

        stage.act(Gdx.graphics.getDeltaTime());
    }

    protected void centerCamera() {
        float x = player.getX();
        float y = player.getY();

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
        Entity entity = EntityFactory.createEntity(object, x, y);
        entities.add(entity);
        actorsGroup.addActor(entity);
        return entity;
    }

    public Pickup spawnPickup(TiledObject object, float x, float y) {
        Pickup pickup = PickupFactory.createPickup(object, x, y);
        pickups.add(pickup);
        actorsGroup.addActor(pickup);
        return pickup;
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
                enemy.takenDamage(bullet.getDamage(), player);
                return true;
            }

        }
        return false;
    }

    public boolean collidesWithEnemy(float newX, float newY, Entity actor) {
        for (Entity enemy : entities) {
            if (actor != null && enemy == actor) {
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
        if (a.getX() < b.getX()+b.getWidth() && a.getX()+a.getWidth() > b.getX() &&
            a.getY() < b.getY()+b.getHeight() && a.getY()+a.getHeight() > b.getY()) {
            return true;
        } else {
            return false;
        }
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
        actorsGroup.clear();
        pickups.clear();
        fences.clear();
    }

    public void setPaused(boolean paused) {
        if (this.paused == paused) {
            return;
        }

        this.paused = paused;
        if (paused) {
            Shooter.getInstance().removeInput(stage);
            Shooter.getInstance().addInput(pauseMenu);
        } else {
            Shooter.getInstance().removeInput(pauseMenu);
            Shooter.getInstance().addInput(stage);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public ShapeRenderer getRenderer() {
        return renderer;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }
}
