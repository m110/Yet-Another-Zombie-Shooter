package org.m110.shooter.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.m110.shooter.ai.AI;
import org.m110.shooter.ai.NoneAI;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class ShooterActor extends Actor {

    private enum State {
        ALIVE, DEAD
    }

    protected final GameScreen game;

    protected TextureRegion texture;
    protected static final TextureRegion deadTexture;
    protected ShapeRenderer renderer;

    protected Sound attackSound = null;
    protected Sound damageSound = null;
    protected Sound deathSound = null;

    private int health = 100;
    private float velocity = 1.0f;

    private State state;

    private float attackInterval = 0.8f;
    private float attackTime = 0.0f;

    private int attackDamage = 10;

    protected AI ai = NoneAI.getInstance();

    static {
        deadTexture = new TextureRegion(new Texture(Gdx.files.internal("images/dead.png")));
    }

    public ShooterActor(GameScreen game) {
        this.game = game;
        renderer = new ShapeRenderer();
        state = State.ALIVE;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ai.act(this, delta);

        attackTime -= delta;
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
        renderer.filledRect(getX(), getY() - 5.0f, (float)health/100 * getWidth(), 3);
        renderer.end();
        batch.begin();
    }

    public void damage(int damage) {
        health -= damage;
        playDamageSound();
        if (health <= 0) {
            health = 0;
            state = State.DEAD;
            playDeathSound();
        }
    }

    public void attack(ShooterActor victim) {
        if (attackTime < 0) {
            playAttackSound();
            victim.damage(attackDamage);
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
    public float distanceTo(ShooterActor actor) {
        return (float) Math.sqrt(Math.pow(getWorldX() - actor.getWorldX(), 2) +
                                 Math.pow(getWorldY() - actor.getWorldY(), 2));
    }

    private boolean checkCollisions(float newX, float newY) {
        return game.collidesWithWall(newX, newY, getWidth(), getHeight())
            || game.collidesWithEnemy(newX, newY, getWidth(), getHeight(), this);
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

    public void playAttackSound() {
        if (attackSound != null) {
            attackSound.play();
        }
    }

    public void playDamageSound() {
        if (damageSound != null) {
            damageSound.play();
        }
    }

    public void playDeathSound() {
        if (deathSound != null) {
            deathSound.play();
        }
    }

}
