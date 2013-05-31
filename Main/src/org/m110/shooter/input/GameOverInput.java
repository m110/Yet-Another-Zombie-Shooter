package org.m110.shooter.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.m110.shooter.Shooter;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameOverInput extends InputListener {
    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (keycode == Input.Keys.ENTER) {
            Shooter.getInstance().showMainMenu();
        }
        return true;
    }
}
