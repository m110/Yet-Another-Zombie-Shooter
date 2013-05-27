package org.m110.shooter.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Movement;
import org.m110.shooter.weapons.*;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Player extends Actor {

    /**
     * Time in seconds between steps sound.
     */
    private static final float STEP_TIME = 0.35f;

    /**
     * Player's texture.
     */
    private TextureRegion texture;

    /**
     * Step sound.
     */
    private final Sound[] stepSound;

    /**
     * Step sound timer for checking when the sound should play.
     */
    private float stepSoundTimer;

    /**
     * The number of next step sound to play.
     */
    private int stepNumber;

    /**
     * Set of player's movement.
     */
    private final EnumSet<Movement> movement;

    /**
     * Array of bullets shot by this player.
     */
    private final Array<Bullet> bullets;

    /**
     * The velocity of moving player.
     */
    private float velocity = 5.0f;

    // Weapons related stuff

    private final HashMap<WeaponSlot, Weapon> weapons;
    private Weapon activeWeapon;

    public Player(float startX, float startY) {
        // Load the texture
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/player.png")));

        // Load sounds
        stepSound = new Sound[3];
        for (int i = 0; i < 3; i++) {
            stepSound[i] = Gdx.audio.newSound(Gdx.files.internal("audio/step"+(i+1)+".ogg"));
        }
        stepSoundTimer = STEP_TIME;
        stepNumber = 0;

        // None of movement at this moment
        movement = EnumSet.noneOf(Movement.class);
        bullets = new Array<>();

        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(startX);
        setY(startY);

        weapons = new HashMap<>();
        activeWeapon = null;

        // temporary
        addWeapon(new Pistol());
        addWeapon(new Shotgun());
        addWeapon(new Rifle());
    }

    /**
     * Update player.
     * @param delta time since last update.
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        if (activeWeapon != null) {
            activeWeapon.updateCooldown(delta);
        }

        float newX = getX();
        float newY = getY();

        if (movement.contains(Movement.UP)) {
            newY += velocity;
        }

        if (movement.contains(Movement.DOWN)) {
            newY -= velocity;
        }

        if (movement.contains(Movement.LEFT)) {
            newX -= velocity;
        }

        if (movement.contains(Movement.RIGHT)) {
            newX += velocity;
        }

        if (newX != getX() || newY != getY()) {
            // Play step sound
            if (stepSoundTimer < delta) {
                stepSound[stepNumber].play();
                stepNumber++;
                stepNumber %= 3;
                stepSoundTimer = STEP_TIME;
            } else stepSoundTimer -= delta;

            // Move player
            if (newX != getX() && !checkCollisions(newX, getY())) {
                MoveToAction moveAction = new MoveToAction();
                moveAction.setDuration(0.05f);
                moveAction.setPosition(newX, getY());
                addAction(moveAction);
            }

            if (newY != getY() && !checkCollisions(getX(), newY)) {
                MoveToAction moveAction = new MoveToAction();
                moveAction.setDuration(0.05f);
                moveAction.setPosition(getX(), newY);
                addAction(moveAction);
            }

            // Update the "look at"
            lookAt(getStage().getCamera().position.x + Gdx.input.getX() - Shooter.GAME_WIDTH / 2,
                   getStage().getCamera().position.y - Gdx.input.getY() + Shooter.GAME_HEIGHT / 2);
        }

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            if (!bullet.isMoving()) {
                bullet.remove();
                it.remove();
            }
        }
    }

    private boolean checkCollisions(float newX, float newY) {
        return Shooter.getInstance().collidesWithWall(newX, newY, getWidth(), getHeight());
    }

    /**
     * Draws the player.
     */
    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
       batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    /**
     * Causes the player to attack.
     */
    public void attack() {
        if (activeWeapon == null) {
            return;
        }

        Array<Bullet> firedBullets = activeWeapon.fire(getX()+getOriginX(), getY()+getOriginY(), getRotation());
        if (firedBullets != null) {
            bullets.addAll(firedBullets);
            for (Bullet bullet : firedBullets) {
                getStage().addActor(bullet);
            }
        }
    }

    public void addWeapon(Weapon weapon) {
        if (weapons.containsKey(weapon.getSlot())) {
            // to-do
            // just add the ammo?
        } else {
            weapons.put(weapon.getSlot(), weapon);
            if (activeWeapon == null) {
                activeWeapon = weapon;
            }
        }
    }

    public boolean changeWeapon(WeaponSlot slot) {
        // Chech if player has any weapon of this slot
        if (weapons.containsKey(slot)) {
            // Change only if current weapon is in another slot
            if (activeWeapon.getSlot() != slot) {
                activeWeapon = weapons.get(slot);
                activeWeapon.setActive();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Weapon getActiveWeapon() {
        return activeWeapon;
    }

    /**
     * Sets player's rotation towards the mouse crosshair.
     * @param x coord x of the mouse pointer relative to the game world.
     * @param y coord y of the mouse pointer relative to the game world.
     */
    public void lookAt(float x, float y) {
        // Player's coords
        float px = getX() + getOriginX();
        float py = getY() + getOriginY();

        // Calculate two angles
        double a1 = Math.atan2(py - y, px - x);
        double a2 = Math.atan2(py - py, px - x);

        // Calculate degrees
        float degrees = (float) Math.toDegrees(a1 - a2);
        if (x <= px ) {
            degrees += 180.0f;
        }

        setRotation(degrees);
    }

    /**
     * Returns player's movement set.
     * @return player's movement set.
     */
    public EnumSet<Movement> movement() {
        return movement;
    }
}

