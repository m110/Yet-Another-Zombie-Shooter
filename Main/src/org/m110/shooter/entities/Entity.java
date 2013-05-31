package org.m110.shooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.ai.entity.AI;
import org.m110.shooter.ai.entity.NoneAI;
import org.m110.shooter.core.timers.CountdownTimer;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.screens.GameScreen;

import java.util.Iterator;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Entity extends Actor {

    class DamageIndicator {
        private final String damage;
        private final CountdownTimer timer;
        private final float x;
        private final float y;

        private static final float duration = 1f;
        private static final float finalY = 30.0f;
        private final BitmapFont font = Shooter.getInstance().getSmallFont();

        public DamageIndicator(int damage) {
            this.damage = Integer.toString(damage);
            timer = new CountdownTimer(duration);

            x = getWorldX() - font.getSpaceWidth() * this.damage.length() / 2.0f;
            y = getWorldY() + getHeight() / 2.0f;
        }

        public void update(float delta) {
            timer.update(delta);
        }

        public void draw(SpriteBatch batch) {
            font.setColor(1.0f, 0.0f, 0.0f, Math.min(Math.max(timer.getTimeLeft(), 0.0f), 1.0f));
            font.draw(batch, damage, x, y + finalY * (duration - timer.getTimeLeft()));
        }

        public boolean isDone() {
            return timer.ready();
        }
    }

    private enum State {
        ALIVE, DEAD
    }

    protected GameScreen game;
    protected final ShapeRenderer renderer;

    // Textures
    private TextureRegion texture;
    private static final TextureRegion deadTexture;

    // Sounds
    private Sound attackSound = null;
    private Sound damageSound = null;
    private Sound deathSound = null;

    // Sound misc.
    private IntervalTimer damageSoundTimer = new IntervalTimer(0.5f);

    // Stats
    private int maxHealth = 100;
    private int health = maxHealth;
    private float velocity = 1.0f;
    private float bonusVelocity = 0.0f;

    private final String name;

    private State state;

    // Combat system
    private float attackInterval;
    private float attackTime;
    private int attackDamage;

    private Entity victim = null;
    protected AI ai = NoneAI.getInstance();

    private Array<DamageIndicator> indicators;

    static {
        deadTexture = new TextureRegion(new Texture(Gdx.files.internal("images/dead.png")));
    }

    protected static Sound loadSound(String name) {
        // Load sounds
        FileHandle soundFile = Gdx.files.internal("audio/" + name + ".ogg");

        if (soundFile.exists()) {
            return Gdx.audio.newSound(soundFile);
        } else {
            return null;
        }
    }

    protected static Sound loadAttackSound(String name) { return loadSound(name + "_attack"); }
    protected static Sound loadDamageSound(String name) { return loadSound(name + "_damage"); }
    protected static Sound loadDeathSound(String name) { return loadSound(name + "_death"); }

    protected static TextureRegion loadTexture(String name) {
        return new TextureRegion(new Texture(Gdx.files.internal("images/" + name + ".png")));
    }

    public Entity(TextureRegion texture, String name, float startX, float startY) {
        this.game = Shooter.getInstance().getGame();
        this.name = name;
        renderer = new ShapeRenderer();

        // Load texture and set metrics
        this.texture = texture;
        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(startX);
        setY(startY);

        state = State.ALIVE;
        indicators = new Array<>();
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

        if (inCombat()) {
            if (victim.isDead()) {
                stopCombat();
            }
        }

        ai.act(delta);

        if (attackTime > 0) {
            attackTime -= delta;
        }

        damageSoundTimer.update(delta);
        updateIndicators(delta);
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

        for (DamageIndicator indicator : indicators) {
            indicator.draw(batch);
        }
    }

    public void takenDamage(int damage, Entity attacker) {
        health -= damage;
        playDamageSound();

        indicators.add(new DamageIndicator(damage));

        ai.afterHit(attacker);

        if (health <= 0) {
            die();
        }
    }

    public void dealDamage(Entity victim) {
        dealDamage(victim, attackDamage);
    }

    public void dealDamage(Entity victim, int damage) {
        if (attackTime <= 0) {
            playAttackSound();
            victim.takenDamage(damage, this);
            attackTime = attackInterval;
        }
    }

    public void die() {
        health = 0;
        state = State.DEAD;
        playDeathSound();
        setZIndex(0);
    }

    public void move(float newX, float newY) {
        if (!checkCollisions(newX - getOriginX(), newY - getOriginY())) {
            MoveToAction moveAction = new MoveToAction();
            moveAction.setDuration(0.05f);
            moveAction.setPosition(newX - getOriginX(), newY - getOriginY());
            addAction(moveAction);
        } else {
            ai.afterCollision();

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
            moveChase();
        }
    }

    public void moveChase() {
        moveChase(victim.getWorldX(), victim.getWorldY());
    }

    public void moveChase(float x, float y) {
        lookAt(x, y);
        moveForward();
    }

    public void moveForward() {
        float newX = getWorldX() + (float) Math.cos(Math.toRadians(getRotation())) * (velocity+bonusVelocity);
        float newY = getWorldY() + (float) Math.sin(Math.toRadians(getRotation())) * (velocity+bonusVelocity);

        move(newX, newY);
    }

    /**
     * Sets actor's rotation towards the point..
     * @param x coord x of the point.
     * @param y coord y of the point.
     */
    public void lookAt(float x, float y) {
        // Player's coords
        float px = getWorldX();
        float py = getWorldY();

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

    protected void updateIndicators(float delta) {
        Iterator<DamageIndicator> it = indicators.iterator();
        while (it.hasNext()) {
            DamageIndicator indicator = it.next();
            indicator.update(delta);
            if (indicator.isDone()) {
                it.remove();
            }
        }
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

    public void setBonusVelocity(float bonusVelocity) {
        this.bonusVelocity = bonusVelocity;
    }

    public float getBonusVelocity() {
        return bonusVelocity;
    }

    public Entity getVictim() {
        return victim;
    }

    public void startCombat(Entity victim) {
        this.victim = victim;
    }

    public void stopCombat() {
        victim = null;
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

    public void setAI(AI ai) {
        this.ai = ai;
    }

    public void setAttackSound(Sound attackSound) {
        this.attackSound = attackSound;
    }

    public void setDamageSound(Sound damageSound) {
        this.damageSound = damageSound;
    }

    public void setDeathSound(Sound deathSound) {
        this.deathSound = deathSound;
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
