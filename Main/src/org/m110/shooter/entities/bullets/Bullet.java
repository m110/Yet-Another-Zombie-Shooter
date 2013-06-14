package org.m110.shooter.entities.bullets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Config;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Bullet extends Actor {

    public static final float BASE_VELOCITY = 10.0f;

    protected final GameScreen game;

    private final TextureRegion texture;

    /**
     * The bullet's angle.
     */
    private final float angle;

    /**
     * The bullet's velocity.
     */
    private float velocity;

    private final int minDamage;
    private final int maxDamage;

    /**
     * Is the bullet still moving.
     */
    protected boolean moving;

    protected static TextureRegion loadTexture(String name) {
        return new TextureRegion(new Texture(Gdx.files.internal(Config.TEXTURES_DIR + "bullets/" + name + ".png")));
    }

    public Bullet(TextureRegion texture, float x, float y, float angle, float velocity, int minDamage, int maxDamage) {
        this.game = Shooter.getInstance().getGame();

        this.texture = texture;
        this.angle = angle;
        this.velocity = BASE_VELOCITY + velocity;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        moving = true;

        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
        setOrigin(getWidth() / 2.0f, getHeight() / 2.0f);
        setX(x);
        setY(y);
        setRotation(angle);
    }

    public Bullet(TextureRegion texture, WeaponProto proto, float x, float y, float angle) {
        this(texture, x, y, angle, proto.bulletVelocity, proto.minDamage, proto.maxDamage);
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
        if (game.getCollision().collidesWithWall(getX(), getY(), this)) {
            moving = false;
        }

        // Did bullet hit an enemy?
        if (game.getCollision().collidesWithEnemy(this)) {
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

    public void dealDamage(Entity attacker, Entity victim) {
        float x = getX() + getOriginX();
        float y = getY() + getOriginY();

        // y = ax + b
        float a = (float) Math.tan(Math.toRadians((double)getRotation()));
        float b = -(a * x) + y;

        float x0 = victim.getWorldX();
        float y0 = victim.getWorldY();
        float A = -a;
        float C = -b;

        float distance = Math.abs(A * x0 + y0 + C) / (float) Math.sqrt(A*A + 1);
        float radius = victim.getHeight() / 2.0f;
        float hitRatio = 1.0f - distance / radius;
        if (hitRatio < 0.05f) {
            hitRatio = 0.05f;
        }

        int damage = minDamage + Math.round((maxDamage - minDamage) * hitRatio);

        // Check if the shot was a critical hit
        boolean critical = false;
        if (hitRatio > 0.9f) {
            critical = true;
            damage = maxDamage;
        }

        victim.takenDamage(damage, attacker, critical);
    }
}
