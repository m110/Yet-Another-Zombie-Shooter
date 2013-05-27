package org.m110.shooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.m110.shooter.actors.Player;
import org.m110.shooter.core.PlayerInputListener;
import org.m110.shooter.weapons.Weapon;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shooter extends Game {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private static Shooter INSTANCE = null;

    private Stage stage;

    private Player player;

    /**
     * Tiled map related stuff.
     */
    private TileMapRenderer tileMapRenderer;
    private TiledMap map;
    private TileAtlas atlas;

    private TiledLayer collisions;

    private ShapeRenderer renderer;
    private SpriteBatch batch;
    private Texture crosshair;
    private Texture hud;

    private OrthographicCamera camera;

    private float mapWidth;
    private float mapHeight;

    private Shooter() {}
    public static Shooter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Shooter();
        }
        return INSTANCE;
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        crosshair = new Texture(Gdx.files.internal("images/crosshair.png"));
        hud = new Texture(Gdx.files.internal("images/hud.png"));

        map = TiledLoader.createMap(Gdx.files.internal("data/level01.tmx"));
        atlas = new TileAtlas(map, Gdx.files.internal("data/"));
        tileMapRenderer = new TileMapRenderer(map, atlas, map.tileWidth, map.tileHeight);
        collisions = map.layers.get(1);

        mapWidth = tileMapRenderer.getMapWidthUnits();
        mapHeight = tileMapRenderer.getMapHeightUnits();

        stage = new Stage();
        stage.setCamera(camera);

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCursorCatched(true);

        for (TiledObject object : map.objectGroups.get(0).objects) {
            switch(object.name) {
                case "player":
                    final float startX = object.x;
                    final float startY = mapHeight - object.y - map.tileHeight;
                    player = new Player(startX, startY);
                    stage.addActor(player);
                    stage.addListener(new PlayerInputListener(player));
                    break;
            }
        }

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        centerCamera();
        tileMapRenderer.render(camera);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // Draw HUD
        batch.begin();
        float hudX = GAME_WIDTH - 270;
        float hudY = 20;
        batch.draw(hud, hudX, hudY);
        Weapon weapon = player.getActiveWeapon();
        if (weapon != null) {
            batch.draw(weapon.getTexture(), hudX + 15, hudY + 15);
            batch.end();

            // Draw magazines
            weapon.drawMagazines(hudX + 80, hudY + 13, renderer);

            batch.begin();
        }
        batch.end();

        // Check crossahair's position
        float crossX = Gdx.input.getX();
        float crossY = Gdx.input.getY();
        int newCrossX = (int) crossX;
        int newCrossY = GAME_HEIGHT - (int) crossY;

        if (crossX > GAME_WIDTH) {
            newCrossX = GAME_WIDTH;
        }
        if (crossX < 0) {
            newCrossX = 0;
        }
        if (crossY > GAME_HEIGHT) {
            newCrossY = 0;
        }
        if (crossY < 0) {
            newCrossY = GAME_HEIGHT;
        }

        if (crossX != newCrossX || GAME_HEIGHT - (int)crossY != newCrossY) {
            Gdx.input.setCursorPosition(newCrossX, newCrossY);
        }

        // Draw crosshair
        batch.begin();
        batch.draw(crosshair, Gdx.input.getX() - crosshair.getWidth() / 2, GAME_HEIGHT - Gdx.input.getY() - crosshair.getHeight() / 2);
        batch.end();
    }

    public void centerCamera() {
        float x = player.getX();
        float y = player.getY();

        float halfW = GAME_WIDTH / 2;
        float halfH = GAME_HEIGHT / 2;
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

    @Override
    public void dispose() {
        stage.dispose();
        tileMapRenderer.dispose();
        atlas.dispose();
        batch.dispose();
        renderer.dispose();
        crosshair.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    /**
     * Checks whether the object collides with terrain.
     * @return true if object collides with terrain, false otherwise.
     */
    public boolean collidesWithWall(float x, float y, float width, float height) {
        int[][] tiles = collisions.tiles;
        int h = tiles.length - 1;
        int x1 = (int)(x / 32);
        int y1 = h - (int)(y / 32);

        int x2 = (int)((x + width) / 32);
        int y2 = h - (int)((y + height) / 32);
        if (tiles[y1][x1] == 0 && tiles[y2][x2] == 0 &&
            tiles[y1][x2] == 0 && tiles[y2][x1] == 0) {
            return false;
        } else {
            return true;
        }
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }
}
