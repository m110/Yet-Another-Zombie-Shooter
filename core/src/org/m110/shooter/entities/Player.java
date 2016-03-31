package org.m110.shooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.core.Config;
import org.m110.shooter.core.Movement;
import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.Weapon;
import org.m110.shooter.weapons.WeaponSlot;
import org.m110.shooter.weapons.magazines.Magazine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Player extends Entity {

    private static final TextureRegion texture;
    private static final Array<TextureRegion> fleshTextures;

    /**
     * Time in seconds between steps sound.
     */
    private static final float STEP_TIME = 0.35f;

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

    private int maxStamina = 100;
    private int stamina = 100;
    private final float staminaRegenTime = 0.1f;
    private float staminaTime = 0.0f;

    private final float staminaBaseUseTime = 0.01f;
    private float staminaUseTime = 0.0f;

    private final float changeWeaponBaseTime = 0.4f;
    private float changeWeaponTime = 0.0f;

    private boolean sprintActive = false;
    private float bonusVelocity = 4.0f;

    private boolean isReloading = false;

    // Weapons related stuff
    private final HashMap<WeaponSlot, Weapon> weapons;
    private Weapon activeWeapon;
    private boolean attacking = false;

    private float adrenalineInterval = 5.0f;
    private float adrenalineTimer = 0.0f;
    private boolean adrenalineActive = false;

    static {
        texture = Entity.loadTexture("player");
        fleshTextures = Entity.loadFleshTextures(texture);
    }

    public Player(GameScreen game) {
        super(game, texture, fleshTextures, "player", 0.0f, 0.0f);

        // Load step sounds
        stepSound = new Sound[3];
        for (int i = 0; i < 3; i++) {
            stepSound[i] = Gdx.audio.newSound(Gdx.files.internal(Config.AUDIO_DIR + "step"+(i+1)+".ogg"));
        }
        stepSoundTimer = STEP_TIME;
        stepNumber = 0;

        // None of movement at this moment
        movement = EnumSet.noneOf(Movement.class);
        bullets = new Array<>();

        // Weapon system
        weapons = new HashMap<>();
        activeWeapon = null;

        // Stats
        setBaseHealth(100);
        setVelocity(8.0f);
    }

    public void updateGame(GameScreen game) {
        this.game = game;
    }

    /**
     * Update player.
     * @param delta time since last update.
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        if (activeWeapon != null) {
            activeWeapon.updateCooldowns(delta);

            if (isReloading && activeWeapon.isReady()) {
                activeWeapon.reload();
            }
        }

        // Stamina regeneration
        if ((!sprintActive || movement.isEmpty()) && stamina < 100) {
            if (staminaTime < 0) {
                regenerateStamina(1);

                if (adrenalineActive) {
                    if (adrenalineTimer > 0.0f) {
                        regenerateStamina(2);
                    } else {
                        adrenalineActive = false;
                        adrenalineTimer = 0.0f;
                    }
                }

                staminaTime = staminaRegenTime;
            } else staminaTime -= delta;
        }

        // Adrenaline bonus
        if (adrenalineActive) {
            adrenalineTimer -= delta;
        }

        // Update change weapon time
        changeWeaponTime -= delta;

        if (attacking && activeWeapon != null && activeWeapon.isReady()) {
            fire();
        }

        if (!movement.isEmpty()) {
            // Stamina usage
            if (sprintActive) {
                if (stamina == 0) {
                    sprintActive = false;
                } else {
                    if (staminaUseTime < 0) {
                        stamina--;
                        staminaUseTime = staminaBaseUseTime;
                    } else staminaUseTime -= delta;
                }
            }

            float newX = getWorldX();
            float newY = getWorldY();

            float totalVelocity = getVelocity();
            if (sprintActive) {
                totalVelocity += bonusVelocity;
            }

            if (movement.contains(Movement.UP)) {
                newY += totalVelocity;
                totalVelocity /= 2;
            } else if (movement.contains(Movement.DOWN)) {
                newY -= totalVelocity;
                totalVelocity /= 2;
            }
            if (movement.contains(Movement.LEFT)) {
                newX -= totalVelocity;
            }
            if (movement.contains(Movement.RIGHT)) {
                newX += totalVelocity;
            }

/*
            double finalAngle = getRotation();

            if (movement.contains(Movement.UP)) {
                if (movement.contains(Movement.LEFT)) {
                    finalAngle += 45.0;
                } else if (movement.contains(Movement.RIGHT)) {
                    finalAngle -= 45.0;
                }
            } else if (movement.contains(Movement.DOWN)) {
                finalAngle += 180.0;
                if (movement.contains(Movement.LEFT)) {
                    finalAngle -= 45.0;
                } else if (movement.contains(Movement.RIGHT)) {
                    finalAngle += 45.0;
                }
            } else if (movement.contains(Movement.LEFT)) {
                finalAngle += 90.0;
            } else if (movement.contains(Movement.RIGHT)) {
                finalAngle += 270.0;
            }

            newX += (float) Math.cos(Math.toRadians(finalAngle)) * totalVelocity;
            newY += (float) Math.sin(Math.toRadians(finalAngle)) * totalVelocity;
*/

            // Play step sound
            if (stepSoundTimer < delta) {
                stepSound[stepNumber].play();
                stepNumber++;
                stepNumber %= 3;
                stepSoundTimer = STEP_TIME;
            } else stepSoundTimer -= delta;

            // Move player
            move(newX, newY);

            // Update the "look at"
            lookAt(getStage().getCamera().position.x + Gdx.input.getX() - Gdx.graphics.getWidth() / 2.0f,
                   getStage().getCamera().position.y - Gdx.input.getY() + Gdx.graphics.getHeight() / 2.0f);
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

    /**
     * Draws the player.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());

        // Stamina bar
        renderer.setColor(Color.YELLOW);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(getX(), getY() - 8.0f, getStaminaPercent() * getWidth(), 3);

        // Reloading bar
        if (activeWeapon != null && !activeWeapon.getReloadTimer().ready()) {
            renderer.setColor(Color.WHITE);
            renderer.rect(getX(), getY() - 11.0f,
                    activeWeapon.getReloadTimer().getPercentDone() * getWidth(), 3);
        }

        renderer.end();
        batch.begin();
    }

    /**
     * Causes the player to attack..
     */
    public void attack() {
        if (activeWeapon == null) {
            return;
        }
        attacking = true;
    }

    public void stopAttack() {
        attacking = false;
        if (activeWeapon != null) {
            activeWeapon.stopFire();
        }
    }

    public void fire() {
        Array<Bullet> firedBullets = activeWeapon.fire(
               getWorldX() + (float)Math.cos(Math.toRadians(getRotation()))*(getWidth()/2),
               getWorldY() + (float)Math.sin(Math.toRadians(getRotation()))*(getHeight()/2), getRotation());

        bullets.addAll(firedBullets);
        for (Bullet bullet : firedBullets) {
            getStage().addActor(bullet);
        }
    }

    @Override
    public void takenDamage(int damage, Entity attacker) {
        super.takenDamage(damage, attacker);
        game.afterPlayerDamage();
    }

    public boolean addWeapon(Weapon weapon) {
        if (weapons.containsKey(weapon.getProto().slot)) {
            if (weapon.getProto() == weapons.get(weapon.getProto().slot).getProto()) {
                return weapons.get(weapon.getProto().slot).addMagazine(weapon.getActiveMagazine().getBullets());
            } else {
                // Don't add the weapon, because different one occupies the slot.
                return false;
            }
        } else {
            weapons.put(weapon.getProto().slot, weapon);
            if (activeWeapon == null) {
                activeWeapon = weapon;
            }
            weapon.setPickedUp();
            return true;
        }
    }

    public boolean changeWeapon(WeaponSlot slot) {
        if (changeWeaponTime > 0) {
            return false;
        }

        // Chech if player has any weapon of this slot
        if (weapons.containsKey(slot)) {
            // Change only if current weapon is in another slot
            if (activeWeapon.getProto().slot != slot) {
                activeWeapon = weapons.get(slot);
                activeWeapon.setActive();
                changeWeaponTime = changeWeaponBaseTime;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean nextWeapon() {
        if (weapons.size() > 1) {
            WeaponSlot slot = activeWeapon.getProto().slot.getNext();
            while (!weapons.containsKey(slot)) {
                slot = slot.getNext();
            }
            return changeWeapon(slot);
        } else {
            return false;
        }
    }

    public boolean previousWeapon() {
        if (weapons.size() > 1) {
            WeaponSlot slot = activeWeapon.getProto().slot.getPrevious();
            while (!weapons.containsKey(slot)) {
                slot = slot.getPrevious();
            }
            return changeWeapon(slot);
        } else {
            return false;
        }
    }

    public void dropMagazine() {
        if (activeWeapon != null) {
            Magazine magazine = activeWeapon.dropMagazine();
            if (magazine != null) {
                // TODO drop magazine on the ground
            }
        }
    }

    public void dropWeapon() {
        if (activeWeapon != null) {
            Weapon tmp = activeWeapon;

            if (weapons.size() > 1) {
                nextWeapon();
            } else {
                activeWeapon = null;
            }

            weapons.remove(tmp.getProto().slot);
            // todo wyrzuc bron na ziemie
        }
    }

    /**
     * Reload active weapon.
     */
    public void startReloading() {
        isReloading = true;
    }

    public void stopReloading() {
        isReloading = false;
    }

    public void nextWeaponMode() {
        if (activeWeapon != null) {
            activeWeapon.nextWeaponMode();
        }
    }

    public Weapon getActiveWeapon() {
        return activeWeapon;
    }

    public Weapon getWeapon(WeaponSlot slot) {
        return weapons.get(slot);
    }

    /**
     * Returns player's movement set.
     * @return player's movement set.
     */
    public EnumSet<Movement> movement() {
        return movement;
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public int getStamina() {
        return stamina;
    }

    public void regenerateStamina(int regen) {
        if (stamina + regen > 100) {
            stamina = 100;
        } else {
            stamina += regen;
        }
    }

    public float getStaminaPercent() {
        return (float) stamina / maxStamina;
    }

    public void setSprintActive(boolean sprintActive) {
        this.sprintActive = sprintActive;
    }

    public void useAdrenaline() {
        adrenalineActive = true;
        adrenalineTimer += adrenalineInterval;
    }
}

