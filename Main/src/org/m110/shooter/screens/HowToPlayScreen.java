package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Font;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class HowToPlayScreen extends ShooterScreen {

    private final InputAdapter inputAdapter;
    private final SpriteBatch batch;

    public HowToPlayScreen(final Shooter shooter) {
        super(shooter);
        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    shooter.resumeGame();
                    return true;
                }
                return false;
            }
        };
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        BitmapFont font = Font.large;

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "WSAD - control character", 100, Gdx.graphics.getHeight() * 0.9f);
        font.draw(batch, "Shift - sprint", 100, Gdx.graphics.getHeight() * 0.85f);
        font.draw(batch, "1, 2, 3, 4, ... - pick weapon", 100, Gdx.graphics.getHeight() * 0.75f);
        font.draw(batch, "R - reload", 100, Gdx.graphics.getHeight() * 0.70f);
        font.draw(batch, "G - drop current magazine", 100, Gdx.graphics.getHeight() * 0.65f);
        font.draw(batch, "Mouse - point the gun / fire", 100, Gdx.graphics.getHeight() * 0.55f);
        font.draw(batch, "Mouse scroll - next / previous weapon", 100, Gdx.graphics.getHeight() * 0.5f);
        font.draw(batch, "Escape - unfocus, pause game", 100, Gdx.graphics.getHeight() * 0.4f);
        Font.medium.draw(batch, "Press ENTER to continue...", 300, Gdx.graphics.getHeight() * 0.1f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        shooter.addInput(inputAdapter);
    }

    @Override
    public void hide() {
        shooter.removeInput(inputAdapter);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
