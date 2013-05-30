package org.m110.shooter.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Movement;
import org.m110.shooter.entities.Player;
import org.m110.shooter.weapons.WeaponSlot;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameInput extends InputListener {

    private final Player player;

    public GameInput() {
        this.player = Shooter.getInstance().getPlayer();
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        if (player.isAlive()) {
            player.lookAt(x, y);
        }
        return true;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (player.isDead()) {
            return false;
        }

        switch (keycode) {
            case Input.Keys.W:
                player.movement().add(Movement.UP);
                break;
            case Input.Keys.S:
                player.movement().add(Movement.DOWN);
                break;
            case Input.Keys.A:
                player.movement().add(Movement.LEFT);
                break;
            case Input.Keys.D:
                player.movement().add(Movement.RIGHT);
                break;
            case Input.Keys.R:
                player.reload();
                break;
            case Input.Keys.SHIFT_LEFT:
                player.setSprintActive(true);
                break;
            case Input.Keys.NUM_1:
                player.changeWeapon(WeaponSlot.MELEE);
                break;
            case Input.Keys.NUM_2:
                player.changeWeapon(WeaponSlot.PISTOL);
                break;
            case Input.Keys.NUM_3:
                player.changeWeapon(WeaponSlot.SHOTGUN);
                break;
            case Input.Keys.NUM_4:
                player.changeWeapon(WeaponSlot.RIFLE);
                break;
            case Input.Keys.ESCAPE:
                player.getGame().setPaused(true);
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        if (player.isDead()) {
            return false;
        }

        switch (keycode) {
            case Input.Keys.W:
                player.movement().remove(Movement.UP);
                break;
            case Input.Keys.S:
                player.movement().remove(Movement.DOWN);
                break;
            case Input.Keys.A:
                player.movement().remove(Movement.LEFT);
                break;
            case Input.Keys.D:
                player.movement().remove(Movement.RIGHT);
                break;
            case Input.Keys.SHIFT_LEFT:
                player.setSprintActive(false);
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (player.isDead()) {
            return false;
        }

        switch (button) {
            case Input.Buttons.LEFT:
                player.attack();
                break;
        }
        return true;
    }

    @Override
    public boolean scrolled (InputEvent event, float x, float y, int amount) {
        if (player.isDead()) {
            return false;
        }

        if (amount < 0) {
            player.nextWeapon();
        } else {
            player.previousWeapon();
        }
        return true;
    }
}
