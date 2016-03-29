package org.m110.shooter.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.MapType;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameOverInput extends InputListener {

    private final Shooter shooter;

    public GameOverInput(Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (keycode == Input.Keys.ENTER) {
            GameScreen game = shooter.getGame();
            if (shooter.getPlayer().isDead()) {
                if (game.getMap().getMapType() == MapType.SURVIVAL) {
                    // TODO - show highscores when implemented
                    // shooter.showHighscores();
                    shooter.showMainMenu();
                } else {
                    shooter.showMainMenu();
                }
            } else {
                if (game.getLevel() < game.getMap().getMaxLevel()) {
                    shooter.loadLevel(game.getMap(), game.getLevel() + 1);
                }  else {
                    // TODO Last level complete - show congratulations
                    shooter.showMainMenu();
                }
            }
        }
        return true;
    }
}
