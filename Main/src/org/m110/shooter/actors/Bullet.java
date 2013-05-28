package org.m110.shooter.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.m110.shooter.Shooter;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Bullet extends ShooterActor {

    public static final float BASE_VELOCITY = 10.0f;

    /**
     * The bullet's texture.
     */
    private final TextureRegion texture;

    /**
     * The bullet's angle.
     */
    private final float angle;

    /**
     * The bullet's velocity.
     */
    private float velocity;

    private int damage;

    /**
     * Is the bullet still in use?
     */
    private boolean moving;

    public Bullet(GameScreen game, float x, float y, float angle, float velocity, int damage) {
        super(game);
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/bullet.png")));
        this.angle = angle;
        this.velocity = velocity;
        this.damage = damage;
        moving = true;

        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setX(x);
        setY(y);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setRotation(angle);
    }

    @Override
    public void act(float delta) {
        if (!moving) {
            return;
        }

        super.act(delta);
        MoveToAction moveAction = new MoveToAction();
        moveAction.setDuration(delta);
        moveAction.setPosition(getX() + (float) Math.cos(Math.toRadians((double) angle)) * velocity,
                               getY() + (float) Math.sin(Math.toRadians((double) angle)) * velocity);
        addAction(moveAction);

        // Is bullet out of bounds?
        if (getX() < 0 || getY() < 0 || getX() > game.getMapWidth() || getY() > game.getMapHeight()) {
            moving = false;
        }

        // Did bullet hit the wall?
        if (game.collidesWithWall(getX(), getY(), getWidth(), getHeight())) {
            moving = false;
        }

        // Did bullet hit an enemy?
        if (game.collidesWithEnemy(this)) {
            moving = false;
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public boolean isMoving() {
        return moving;
    }

    public int getDamage() {
        return damage;
    }
}
