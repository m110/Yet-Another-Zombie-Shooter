package org.m110.shooter.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.m110.shooter.Shooter;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameOverInput extends InputListener {
    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (keycode == Input.Keys.ENTER) {
            if (Shooter.getInstance().getPlayer().isDead()) {
                Shooter.getInstance().showHighscores();
            } else {
                GameScreen game = Shooter.getInstance().getGame();
                if (game.getLevel() < game.getMap().getMaxLevel()) {
                    Shooter.getInstance().loadLevel(game.getMap(), game.getLevel() + 1);
                }  else {
                    // to-do show some congratulations
                    Shooter.getInstance().showHighscores();
                }
            }
        }
        return true;
    }
}
