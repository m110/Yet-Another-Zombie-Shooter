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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.ai.entity.AI;
import org.m110.shooter.ai.entity.NoneAI;
import org.m110.shooter.core.Config;
import org.m110.shooter.core.Font;
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
        private final BitmapFont font = Font.small;

        public DamageIndicator(int damage, boolean critical) {
            this.damage = Integer.toString(damage) + (critical ? " CRITICAL! " : "");

            timer = new CountdownTimer(duration);

            x = getWorldX() - font.getSpaceWidth() * this.damage.length() / 2.0f
                            + MathUtils.random(-getWidth()/2, getWidth()/2);
            y = getWorldY() + getHeight() / 2.0f
                            + MathUtils.random(-getHeight()/4, getHeight()/4);
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

    private class Piece {

        private final TextureRegion texture;
        private final float rotation;
        private float x;
        private float y;
        private final float offsetX;
        private final float offsetY;

        private float angle = 0.0f;
        private float velocity = 0.0f;

        public Piece(TextureRegion texture) {
            this.texture = texture;
            offsetX = MathUtils.random(-getOriginX() / 2.0f, getOriginX() / 2.0f);
            offsetY = MathUtils.random(-getOriginY() / 2.0f, getOriginY() / 2.0f);
            rotation = MathUtils.random(0.0f, 360.0f);
            updatePosition();
        }

        public void draw(SpriteBatch batch) {
            batch.draw(texture, x, y, 0, 0,
                       texture.getRegionWidth(), texture.getRegionHeight(), 1, 1, rotation);
        }

        public void act(float delta) {
            if (velocity <= 0) {
                return;
            }

            x += Math.cos(Math.toRadians(angle)) * velocity;
            y += Math.sin(Math.toRadians(angle)) * velocity;

            velocity -= delta;
        }

        public void updatePosition() {
            x = getWorldX() + offsetX;
            y = getWorldY() + offsetY;
        }

        public void setRecoil(float angle, float velocity) {
            this.angle = angle;
            this.velocity = velocity;
        }
    }

    private enum State {
        ALIVE, DEAD
    }

    protected GameScreen game;
    protected final ShapeRenderer renderer;

    // Textures
    private final TextureRegion texture;
    private static final TextureRegion deadTexture;

    // Sounds
    private Sound attackSound = null;
    private Sound damageSound = null;
    private Sound deathSound = null;

    // Sound misc.
    private IntervalTimer damageSoundTimer = new IntervalTimer(0.5f);

    // Stats
    private int maxHealth;
    private int health;
    private float velocity;
    private float bonusVelocity;

    private final String name;

    private State state;

    protected AI ai = NoneAI.getInstance();

    private Array<DamageIndicator> indicators;
    private final Array<Piece> pieces;

    static {
        deadTexture = new TextureRegion(new Texture(Gdx.files.internal(Config.TEXTURES_DIR + "entities/dead.png")));
    }

    protected static Sound loadSound(String name) {
        // Load sounds
        FileHandle soundFile = Gdx.files.internal(Config.AUDIO_DIR + "" + name + ".ogg");

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
        return new TextureRegion(new Texture(Gdx.files.internal(Config.TEXTURES_DIR + "entities/" + name + ".png")));
    }

    protected static Array<TextureRegion> loadFleshTextures(TextureRegion texture) {
        Array<TextureRegion> regions = new Array<>();

        final float width = texture.getRegionWidth();
        final float height = texture.getRegionHeight();

        float stepH = height / MathUtils.random(4, 5);
        float stepW = width / MathUtils.random(4, 5);
        for (int i = 0; i < height; i += stepH) {
            for (int j = 0; j < width; j += stepW) {
                regions.add(new TextureRegion(texture, j, i, (int) stepW, (int) stepH));
            }
        }

        return regions;
    }

    /**
     * Creates new entity
     * @param texture
     * @param fleshTextures
     * @param name
     * @param startX
     * @param startY
     */
    public Entity(TextureRegion texture, Array<TextureRegion> fleshTextures, String name, float startX, float startY) {
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

        pieces = new Array<>();
        for (TextureRegion fleshTexture : fleshTextures) {
            pieces.add(new Piece(fleshTexture));
        }
    }

    /**
     * Entity's acting handled here.
     * @param delta time since last update.
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        ai.act(delta);

        // Update flesh piecs
        if (isDead()) {
            updatePieces(delta);
        }

        damageSoundTimer.update(delta);
        updateIndicators(delta);
    }

    public void beforeDraw(SpriteBatch batch) {

    }

    /**
     * Draws the entity on stage.
     * @param batch
     * @param parentAlpha
     */
    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        ai.draw(batch);

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

        // Draw flesh pieces on top
        if (isDead()) {
            for (Piece piece : pieces) {
                piece.draw(batch);
            }
        }

        // Draw damage indicators
        for (DamageIndicator indicator : indicators) {
            indicator.draw(batch);
        }
    }

    public void takenDamage(int damage, Entity attacker) {
        takenDamage(damage, attacker, false);
    }

    public void takenDamage(int damage, Entity attacker, boolean critical) {
        if (critical) {
            damage *= 2;
        }

        health -= damage;

        indicators.add(new DamageIndicator(damage, critical));

        ai.afterHit(attacker);

        if (health <= 0) {
            die();
        } else {
            playDamageSound();
        }
    }

    public void die() {
        health = 0;
        state = State.DEAD;
        playDeathSound();

        // Make sure the body lies on the bottom
        setZIndex(0);

        // Set random rotation of the dead texture
        setRotation(MathUtils.random(0.0f, 360.0f));

        for (Piece piece : pieces) {
            piece.updatePosition();
        }

        ai.afterDeath();
    }

    /**
     * Moves entity to given point, checking collisions.
     * @param newX
     * @param newY
     */
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
        return distanceTo(who) < getWidth()*0.65f + who.getWidth()*0.65f;
    }

    public void moveForward() {
        float newX = getWorldX() + (float) Math.cos(Math.toRadians(getRotation())) * (velocity+bonusVelocity);
        float newY = getWorldY() + (float) Math.sin(Math.toRadians(getRotation())) * (velocity+bonusVelocity);

        move(newX, newY);
    }

    public void setPiecesRecoil(float angleMin, float angleMax, float velocityMin, float velocityMax) {
        for (Piece piece : pieces) {
            piece.setRecoil(MathUtils.random(angleMin, angleMax), MathUtils.random(velocityMin, velocityMax));
        }
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

    public void lookAt(Entity entity) {
        lookAt(entity.getWorldX(), entity.getWorldY());
    }

    public float angleWith(Entity entity) {
        float px = getWorldX();
        float py = getWorldY();

        // Calculate two angles
        double a1 = Math.atan2(py - entity.getWorldY(), px - entity.getWorldX());
        double a2 = Math.atan2(py - py, px - entity.getWorldX());

        // Calculate degrees
        float degrees = 360 + (float) Math.toDegrees(a1 - a2);
        if (entity.getWorldX() <= px ) {
            degrees -= 180.0f;
        }
        return degrees;
    }

    public float distanceTo(Entity actor) {
        return (float) Math.sqrt(Math.pow(getWorldX() - actor.getWorldX(), 2) +
                                 Math.pow(getWorldY() - actor.getWorldY(), 2));
    }

    private boolean checkCollisions(float newX, float newY) {
        return game.collidesWithWall(newX, newY, this)
            || game.collidesWithEnemy(newX, newY, this);
    }

    public void pushAwayFrom(Entity entity, float pushbackPower) {
        float ex = entity.getWorldX();
        float ey = entity.getWorldY();

        float dx = Math.signum(getWorldX() - ex);
        float dy = Math.signum(getWorldY() - ey);
        move(ex + pushbackPower * dx, ey + pushbackPower * dy);
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

    protected void updatePieces(float delta) {
        for (Piece piece : pieces) {
            piece.act(delta);
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

    public void playAttackSound() {
        if (attackSound != null) {
            attackSound.play(0.5f);
        }
    }

    public void playDamageSound() {
        if (damageSound != null) {
            if (damageSoundTimer.ready()) {
                damageSound.play(0.5f);
                damageSoundTimer.reset();
            }
        }
    }

    public void playDeathSound() {
        if (deathSound != null) {
            deathSound.play(0.5f);
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

    public int getHealth() {
        return health;
    }

    public float getHealthPercent() {
        return (float) health / maxHealth;
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
}
