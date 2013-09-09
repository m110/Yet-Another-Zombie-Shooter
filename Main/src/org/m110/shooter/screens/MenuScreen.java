package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Font;
import org.m110.shooter.core.Map;
import org.m110.shooter.screens.menu.Menu;
import org.m110.shooter.screens.menu.MenuAction;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class MenuScreen extends ShooterScreen {

    private final Menu mainMenu;
    private final Menu campaignMenu;
    private final Menu survivalMenu;
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;

    private Menu activeMenu;

    public MenuScreen(final Shooter shooter) {
        super(shooter);
        mainMenu = new Menu(0.0f, Gdx.graphics.getHeight() * 0.6f);
        campaignMenu = new Menu(0.0f, Gdx.graphics.getHeight() * 0.6f);
        survivalMenu = new Menu(0.0f, Gdx.graphics.getHeight() * 0.6f);

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        mainMenu.addMenuItem("Campaign", new MenuAction() {
            @Override
            public void action() {
                setActiveMenu(campaignMenu);
            }
        });
        mainMenu.addMenuItem("Survival", new MenuAction() {
            @Override
            public void action() {
                setActiveMenu(survivalMenu);
            }
        });
        mainMenu.addMenuItem("How to play", new MenuAction() {
            @Override
            public void action() {
                shooter.showHowToPlay();
            }
        });
        mainMenu.addMenuItem("Options", new MenuAction() {
            @Override
            public void action() {
                shooter.showOptions();
            }
        });
        mainMenu.addMenuItem("Quit", new MenuAction() {
            @Override
            public void action() {
                shooter.exitWithDelay(0.5f);
            }
        });

        for (final Map map : shooter.getCampaignMaps()) {
            campaignMenu.addMenuItem(map.getName(), new MenuAction() {
                @Override
                public void action() {
                shooter.loadLevel(map, 1);
                }
            });
        }
        campaignMenu.addMenuItem("Back", new MenuAction() {
            @Override
            public void action() {
                setActiveMenu(mainMenu);
            }
        });

        for (final Map map : shooter.getSurvivalMaps()) {
            survivalMenu.addMenuItem(map.getName(), new MenuAction() {
                @Override
                public void action() {
                shooter.loadLevel(map, 1);
                }
            });
        }
        survivalMenu.addMenuItem("Back", new MenuAction() {
            @Override
            public void action() {
                setActiveMenu(mainMenu);

            }
        });

        activeMenu = mainMenu;
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        activeMenu.draw(batch, delta);
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        Font.large.setColor(0.25f, 0.25f, 0.25f, 0.7f);
        Font.large.draw(batch, "yet another zombie", 10.0f, Gdx.graphics.getHeight() * 0.95f);
        Font.big.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        Font.big.draw(batch, "Sh00ter", 30.0f, Gdx.graphics.getHeight() * 0.91f);
        Font.medium.draw(batch, "version " + Shooter.VERSION, 5.0f, 20.0f);
        batch.end();

        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setColor(0.7f, 0.7f, 0.7f, 0.7f);
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(0.0f, Gdx.graphics.getHeight() * 0.765f, Gdx.graphics.getWidth(), 3.0f);
        renderer.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);
    }

    protected void setActiveMenu(Menu activeMenu) {
        shooter.removeInput(this.activeMenu);
        this.activeMenu = activeMenu;
        shooter.addInput(this.activeMenu);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        shooter.addInput(activeMenu);
    }

    @Override
    public void hide() {
        shooter.removeInput(activeMenu);
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
