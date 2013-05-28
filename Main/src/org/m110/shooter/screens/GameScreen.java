package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.m110.shooter.Shooter;
import org.m110.shooter.actors.Player;
import org.m110.shooter.core.PlayerInputListener;
import org.m110.shooter.weapons.Weapon;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameScreen implements Screen {

    private final int levelID;

    /**
     * Scene2D Stage, handles actors.
     */
    private final Stage stage;

    /**
     * Box2d World, handles basic physics.
     */
    private final World world;

    private Player player;

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

    private Texture crosshair;
    private final Texture leftHUD;
    private final Texture rightHUD;

    private final float mapWidth;
    private final float mapHeight;

    public GameScreen(int levelID) {
        this.levelID = levelID;

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Shooter.GAME_WIDTH, Shooter.GAME_HEIGHT);

        // Initialize world
        world = new World(new Vector2(0.0f, 0.0f), true);

        // Rendering objects
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

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

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCursorCatched(true);

        for (TiledObject object : map.objectGroups.get(0).objects) {
            switch(object.name) {
                case "player":
                    final float startX = object.x;
                    final float startY = mapHeight - object.y - map.tileHeight;
                    player = new Player(this, startX, startY);
                    stage.addActor(player);
                    stage.addListener(new PlayerInputListener(player));
                    break;
            }
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        centerCamera();
        tileMapRenderer.render(camera);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // Draw HUD
        batch.begin();
        float leftX = 20;
        float leftY = 10;
        batch.draw(leftHUD, leftX, leftY);

        // Health and stamina
        Shooter.getInstance().getMediumFont().draw(batch, "" + player.getHealth(), leftX + 210, leftY + 62);
        Shooter.getInstance().getMediumFont().draw(batch, "" + player.getStamina(), leftX + 210, leftY + 32);

        // Health and stamina bars
        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.setColor(Color.RED);
        renderer.filledRect(leftX+41, leftY+46, (float)player.getHealth()/100 * 158, 9);
        renderer.setColor(Color.YELLOW);
        renderer.filledRect(leftX+41, leftY+16, (float)player.getStamina()/100 * 158, 9);
        renderer.end();
        batch.begin();

        float rightX = Shooter.GAME_WIDTH - 260;
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

        // Check crossahair's position
        float crossX = Gdx.input.getX();
        float crossY = Gdx.input.getY();
        int newCrossX = (int) crossX;
        int newCrossY = Shooter.GAME_HEIGHT - (int) crossY;

        if (crossX > Shooter.GAME_WIDTH) {
            newCrossX = Shooter.GAME_WIDTH;
        }
        if (crossX < 0) {
            newCrossX = 0;
        }
        if (crossY > Shooter.GAME_HEIGHT) {
            newCrossY = 0;
        }
        if (crossY < 0) {
            newCrossY = Shooter.GAME_HEIGHT;
        }

        if (crossX != newCrossX || Shooter.GAME_HEIGHT - (int)crossY != newCrossY) {
            Gdx.input.setCursorPosition(newCrossX, newCrossY);
        }

        // Draw crosshair
        batch.begin();
        batch.draw(crosshair, Gdx.input.getX() - crosshair.getWidth() / 2,
                   Shooter.GAME_HEIGHT - Gdx.input.getY() - crosshair.getHeight() / 2);
        batch.end();
    }

    public void centerCamera() {
        float x = player.getX();
        float y = player.getY();

        float halfW = Shooter.GAME_WIDTH / 2;
        float halfH = Shooter.GAME_HEIGHT / 2;
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

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        tileMapRenderer.dispose();
        atlas.dispose();
        batch.dispose();
        renderer.dispose();
        crosshair.dispose();
        leftHUD.dispose();
        rightHUD.dispose();
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }
}
