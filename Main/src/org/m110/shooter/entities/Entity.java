package org.m110.shooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.m110.shooter.Shooter;
import org.m110.shooter.ai.AI;
import org.m110.shooter.ai.NoneAI;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Entity extends Actor {

    private enum State {
        ALIVE, DEAD
    }

    protected final GameScreen game;
    protected final ShapeRenderer renderer;

    // Textures
    protected TextureRegion texture;
    protected static final TextureRegion deadTexture;

    // Sounds
    protected Sound attackSound = null;
    protected Sound damageSound = null;
    protected Sound deathSound = null;

    // Sound misc.
    private IntervalTimer damageSoundTimer = new IntervalTimer(0.5f);

    // Stats
    private int maxHealth = 100;
    private int health = maxHealth;
    private float velocity = 1.0f;

    private final String name;

    private State state;

    // Combat system
    private float attackInterval = 0.8f;
    private float attackTime = 0.0f;
    private int attackDamage = 10;

    private Entity victim = null;

    protected AI ai = NoneAI.getInstance();

    static {
        deadTexture = new TextureRegion(new Texture(Gdx.files.internal("images/dead.png")));
    }

    public Entity(String name, float startX, float startY) {
        this.game = Shooter.getInstance().getGame();
        this.name = name;
        renderer = new ShapeRenderer();

        // Load sounds
        FileHandle attackFile = Gdx.files.internal("audio/" + name + "_attack.ogg");
        FileHandle damageFile = Gdx.files.internal("audio/" + name + "_damage.ogg");
        FileHandle deathFile = Gdx.files.internal("audio/" + name + "_death.ogg");

        if (attackFile.exists()) {
            attackSound = Gdx.audio.newSound(attackFile);
        }

        if (damageFile.exists()) {
            damageSound = Gdx.audio.newSound(damageFile);
        }

        if (deathFile.exists()) {
            deathSound = Gdx.audio.newSound(deathFile);
        }

        // Load texture and set metrics
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/" + name + ".png")));
        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(startX);
        setY(startY);

        state = State.ALIVE;
    }

    public int getHealth() {
        return health;
    }

    public float getHealthPercent() {
        return (float) health / maxHealth;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ai.act(delta);

        if (attackTime > 0) {
            attackTime -= delta;
        }


        damageSoundTimer.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        TextureRegion toDraw = null;
        if (state == State.ALIVE) {
            toDraw = texture;
        } else {
            toDraw = deadTexture;
        }
        batch.draw(toDraw, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.end();
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.setColor(Color.RED);
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(getX(), getY() - 5.0f, getHealthPercent() * getWidth(), 3);
        renderer.end();
        batch.begin();
    }

    public void takenDamage(int damage) {
        health -= damage;
        playDamageSound();

        ai.afterHit();

        if (health <= 0) {
            health = 0;
            state = State.DEAD;
            playDeathSound();
            setZIndex(0);
        }
    }

    public void dealDamage(Entity victim) {
        if (attackTime < 0) {
            playAttackSound();
            victim.takenDamage(attackDamage);
            attackTime = attackInterval;
        }
    }

    public void move(float newX, float newY) {
        if (!checkCollisions(newX - getOriginX(), newY - getOriginY())) {
            MoveToAction moveAction = new MoveToAction();
            moveAction.setDuration(0.05f);
            moveAction.setPosition(newX - getOriginX(), newY - getOriginY());
            addAction(moveAction);
        } else {
            ai.hitWall();

            if (!checkCollisions(newX - getOriginX(), getY())) {
                MoveToAction moveAction = new MoveToAction();
                moveAction.setDuration(0.05f);
                moveAction.setPosition(newX - getOriginX(), getY());
                addAction(moveAction);
            } else if (!checkCollisions(getX(), newY - getOriginY())) {
                MoveToAction moveAction = new MoveToAction();
                moveAction.setDuration(0.05f);
                moveAction.setPosition(getX(), newY - getOriginY());
                addAction(moveAction);
            }
        }
    }

    public boolean isInMeleeRange(Entity who) {
        if (!inCombat()) {
            return false;
        }

        return distanceTo(who) < getWidth()*0.65f + who.getWidth()*0.65f;
    }

    public void attackChase() {
        if (isInMeleeRange(victim)) {
            dealDamage(victim);
        } else {
            lookAt(victim.getWorldX(), victim.getWorldY());

            float newX = getWorldX() + (float) Math.cos(Math.toRadians(getRotation())) * velocity;
            float newY = getWorldY() + (float) Math.sin(Math.toRadians(getRotation())) * velocity;

            move(newX, newY);
        }
    }

    /**
     * Sets actor's rotation towards the point..
     * @param x coord x of the point.
     * @param y coord y of the point.
     */
    public void lookAt(float x, float y) {
        // Player's coords
        float px = getX() + getOriginX();
        float py = getY() + getOriginY();

        // Calculate two angles
        double a1 = Math.atan2(py - y, px - x);
        double a2 = Math.atan2(py - py, px - x);

        // Calculate degrees
        float degrees = 360 + (float) Math.toDegrees(a1 - a2);
        if (x <= px ) {
            degrees -= 180.0f;
        }

        setRotation(degrees);
    }
    public float distanceTo(Entity actor) {
        return (float) Math.sqrt(Math.pow(getWorldX() - actor.getWorldX(), 2) +
                                 Math.pow(getWorldY() - actor.getWorldY(), 2));
    }

    private boolean checkCollisions(float newX, float newY) {
        return game.collidesWithWall(newX, newY, this)
            || game.collidesWithEnemy(newX, newY, this);
    }

    public float getWorldX() {
        return getX() + getOriginX();
    }

    public float getWorldY() {
        return getY() + getOriginY();
    }

    public boolean isAlive() {
        return state == State.ALIVE;
    }

    public boolean isDead() {
        return state == State.DEAD;
    }

    public GameScreen getGame() {
        return game;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public float getVelocity() {
        return velocity;
    }

    public Entity getVictim() {
        return victim;
    }

    public void startCombat(Entity victim) {
        this.victim = victim;
    }

    public boolean inCombat() {
        return victim != null;
    }

    public void playAttackSound() {
        if (attackSound != null) {
            attackSound.play();
        }
    }

    public void playDamageSound() {
        if (damageSound != null) {
            if (damageSoundTimer.ready()) {
                damageSound.play();
                damageSoundTimer.reset();
            }
        }
    }

    public void playDeathSound() {
        if (deathSound != null) {
            deathSound.play();
        }
    }

    public void addHealth(int health) {
        if (this.health + health > maxHealth) {
            this.health = maxHealth;
        } else {
            this.health += health;
        }
    }

    protected void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /* Stats settings */

    protected void setBaseHealth(int baseHealth) {
        maxHealth = baseHealth;
        health = maxHealth;
    }

    protected void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    protected void setAttackInterval(float attackInterval) {
        this.attackInterval = attackInterval;
    }
}