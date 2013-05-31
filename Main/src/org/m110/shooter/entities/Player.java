package org.m110.shooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Movement;
import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.pickups.Ammo;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.*;
import org.m110.shooter.weapons.magazines.Magazine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Player extends Entity {

    private static final TextureRegion texture;

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
    private float bonusVelocity = 6.0f;

    // Weapons related stuff
    private final HashMap<WeaponSlot, Weapon> weapons;
    private Weapon activeWeapon;

    // This should be replaced with proper buff system
    private float medpackInterval = 0.5f;
    private float medpackTimer = 0.0f;
    private int medpackMaxCount = 10;
    private int medpackCount = 5;
    private int medpackBonus = 1;
    private float adrenalineInterval = 5.0f;
    private float adrenalineTimer = 0.0f;
    private boolean medpackActive = false;
    private boolean adrenalineActive = false;

    static {
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/player.png")));
    }

    public Player() {
        super(texture, "player", 0.0f, 0.0f);

        // Load step sounds
        stepSound = new Sound[3];
        for (int i = 0; i < 3; i++) {
            stepSound[i] = Gdx.audio.newSound(Gdx.files.internal("audio/step"+(i+1)+".ogg"));
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
        setVelocity(6.0f);
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

        // Medpack health regeneration
        if (medpackActive) {
            if (medpackCount >= medpackMaxCount) {
                medpackActive = false;
            } else {
                if (medpackTimer < 0) {
                    addHealth(medpackBonus);
                    medpackCount++;
                    medpackTimer = medpackInterval;
                } else medpackTimer -= delta;
            }
        }

        // Update change weapon time
        changeWeaponTime -= delta;

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
            }
            if (movement.contains(Movement.DOWN)) {
                newY -= totalVelocity;
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
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.setColor(Color.YELLOW);
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(getX(), getY() - 8.0f, (float)stamina/100 * getWidth(), 3);
        renderer.end();
        batch.begin();
/*
        batch.end();

        ShapeRenderer r = new ShapeRenderer();
        r.setProjectionMatrix(batch.getProjectionMatrix());
        r.setTransformMatrix(batch.getTransformMatrix());
        r.setColor(Color.RED);
        r.begin(ShapeRenderer.ShapeType.FilledCircle);
        r.filledCircle(getWorldX() + (float)Math.cos(Math.toRadians(getRotation()))*(getWidth()/2),
                       getWorldY() + (float)Math.sin(Math.toRadians(getRotation()))*(getHeight()/2), 1.0f);
        //r.line(getX()+getOriginX(), getY()+getOriginY(), getStage().getCamera().position.x + Gdx.input.getX() - Shooter.GAME_WIDTH / 2,
        //        getStage().getCamera().position.y - Gdx.input.getY() + Shooter.GAME_HEIGHT / 2);
        r.end();
        batch.begin();
        */
    }

    /**
     * Causes the player to dealDamage.
     */
    public void attack() {
        if (activeWeapon == null) {
            return;
        }

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
                weapons.get(weapon.getProto().slot).setActiveMagazineAmmo(weapon.getActiveMagazine().getBullets());
                weapon.playReloadSound();
                return true;
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
                // todo wyrzuc magazynek na ziemie
            }
        }
    }

    /**
     * Reload active weapon.
     */
    public void reload() {
        if (activeWeapon != null) {
            activeWeapon.reload();
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

    public void useMedpack(int bonus) {
        medpackActive = true;
        medpackBonus = bonus / medpackMaxCount;
        medpackTimer = medpackInterval;
        medpackCount = 0;
    }

    public void useAdrenaline() {
        adrenalineActive = true;
        adrenalineTimer += adrenalineInterval;
    }
}

