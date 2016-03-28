package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Config;
import org.m110.shooter.core.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class HighscoresScreen extends ShooterScreen {

    private class Entry {

        private final String playerID;
        private final int score;

        public Entry(String playerID, int score) {
            this.playerID = playerID;
            this.score = score;
        }
    }

    private final InputAdapter inputAdapter;
    private final SpriteBatch batch;

    private final Array<Entry> entries;

    public HighscoresScreen(final Shooter shooter) {
        super(shooter);
        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    shooter.showMainMenu();
                    return true;
                }
                return false;
            }
        };
        batch = new SpriteBatch();
        entries = new Array<>();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        Font.big.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        Font.big.draw(batch, "High scores", 30.0f, Gdx.graphics.getHeight() * 0.91f);

        BitmapFont font = Font.large;
        font.setColor(Color.WHITE);

        float posX = 100.0f;
        float posY = Gdx.graphics.getHeight() * 0.7f;

        for (int i = 0; i < entries.size; i++) {
            font.draw(batch, (i+1) + ".", posX, posY);
            font.draw(batch, entries.get(i).playerID, posX + 50.0f, posY);
            font.draw(batch, entries.get(i).score+"", posX + 400.0f, posY);
            posY -= 40.0f;
        }

        batch.end();
    }

    public void updateScores(String mapID) {
        entries.clear();
        try {
            URL url = new URL(Config.SCORES_URL + "?map_id=" + mapID);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                String[] buffer = line.split(":");
                entries.add(new Entry(buffer[0], Integer.parseInt(buffer[1])));
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
