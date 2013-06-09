package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.Shooter;
import org.m110.shooter.screens.menu.Menu;
import org.m110.shooter.screens.menu.MenuAction;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class MenuScreen implements Screen {

    private final Menu menu;
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;

    public MenuScreen() {
        menu = new Menu(0.0f, Gdx.graphics.getHeight() * 0.6f);
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        menu.addMenuItem("Campaign", new MenuAction() {
            @Override
            public void action() {
                Shooter.getInstance().loadLevel("1");
            }
        });
        menu.addMenuItem("Survival", new MenuAction() {
            @Override
            public void action() {
                Shooter.getInstance().loadLevel("survival");
            }
        });
        menu.addMenuItem("How to play", new MenuAction() {
            @Override
            public void action() {
                Shooter.getInstance().showHowToPlay();
            }
        });
        menu.addMenuItem("Options", new MenuAction() {
            @Override
            public void action() {
                Shooter.getInstance().showOptions();
            }
        });
        menu.addMenuItem("Quit", new MenuAction() {
            @Override
            public void action() {
                Shooter.getInstance().exitWithDelay(0.5f);
            }
        });
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        menu.draw(batch, delta);
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        Shooter.getInstance().getLargeFont().setColor(0.25f, 0.25f, 0.25f, 0.7f);
        Shooter.getInstance().getLargeFont().draw(batch, "yet another zombie", 10.0f, Gdx.graphics.getHeight() * 0.95f);
        Shooter.getInstance().getBigFont().setColor(1.0f, 1.0f, 1.0f, 0.7f);
        Shooter.getInstance().getBigFont().draw(batch, "Sh00ter", 30.0f, Gdx.graphics.getHeight() * 0.91f);
        Shooter.getInstance().getMediumFont().draw(batch, "version " + Shooter.VERSION, 5.0f, 20.0f);
        batch.end();

        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setColor(0.7f, 0.7f, 0.7f, 0.7f);
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(0.0f, Gdx.graphics.getHeight() * 0.765f, Gdx.graphics.getWidth(), 3.0f);
        renderer.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);
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
