package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.timers.CountdownTimer;
import org.m110.shooter.screens.menu.Menu;
import org.m110.shooter.screens.menu.MenuItem;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class MenuScreen implements Screen {

    private final Menu menu;
    private final SpriteBatch batch;

    public MenuScreen() {
        menu = new Menu(320.0f, 430.0f);
        batch = new SpriteBatch();

        menu.addMenuItem(new MenuItem("Campaign") {
            @Override
            public void action() {
                Shooter.getInstance().loadLevel("1");
            }
        });
        menu.addMenuItem(new MenuItem("Survival") {
            @Override
            public void action() {
                Shooter.getInstance().loadLevel("survival");
            }
        });
        menu.addMenuItem(new MenuItem("How to play") {
            @Override
            public void action() {
                Shooter.getInstance().showHowToPlay();
            }
        });
        menu.addMenuItem(new MenuItem("Options") {
            @Override
            public void action() {
                Shooter.getInstance().showOptions();
            }
        });
        menu.addMenuItem(new MenuItem("Quit") {
            @Override
            public void action() {
                Shooter.getInstance().exitWithDelay(0.5f);
            }
        });
        menu.alignToCenter();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        menu.draw(batch, delta);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Shooter.getInstance().addInput(menu);
    }

    @Override
    public void hide() {
        Shooter.getInstance().removeInput(menu);
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
