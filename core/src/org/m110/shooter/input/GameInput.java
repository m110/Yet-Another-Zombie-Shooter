package org.m110.shooter.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.m110.shooter.core.Movement;
import org.m110.shooter.entities.Player;
import org.m110.shooter.weapons.WeaponSlot;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class GameInput extends InputListener {

    private final Player player;

    public GameInput(Player player) {
        this.player = player;
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        player.lookAt(x, y);
        return true;
    }

    @Override
    public void touchDragged (InputEvent event, float x, float y, int pointer) {
        player.lookAt(x, y);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
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
                player.startReloading();
                break;
            case Input.Keys.G:
                player.dropMagazine();
                break;
            case Input.Keys.F:
                player.dropWeapon();
                break;
            case Input.Keys.SHIFT_LEFT:
                player.setSprintActive(true);
                break;
            case Input.Keys.NUM_1:
                player.changeWeapon(WeaponSlot.HANDGUN);
                break;
            case Input.Keys.NUM_2:
                player.changeWeapon(WeaponSlot.SMG);
                break;
            case Input.Keys.NUM_3:
                player.changeWeapon(WeaponSlot.SHOTGUN);
                break;
            case Input.Keys.NUM_4:
                player.changeWeapon(WeaponSlot.RIFLE);
                break;
            case Input.Keys.NUM_5:
                player.changeWeapon(WeaponSlot.HEAVY);
                break;
            case Input.Keys.ESCAPE:
                player.getGame().setPaused(true);
                break;
            case Input.Keys.F10:
                player.die();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
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
            case Input.Keys.R:
                player.stopReloading();
                break;
            case Input.Keys.SHIFT_LEFT:
                player.setSprintActive(false);
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                player.attack();
                break;
            case Input.Buttons.RIGHT:
                player.nextWeaponMode();
                break;
        }
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                player.stopAttack();
                break;
        }
    }

    @Override
    public boolean scrolled (InputEvent event, float x, float y, int amount) {
        if (amount < 0) {
            player.nextWeapon();
        } else {
            player.previousWeapon();
        }
        return true;
    }
}
