package org.m110.shooter.entities.bullets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Arrow extends Bullet {

    private static final TextureRegion texture;

    private Entity hitEntity = null;
    private float rotationOffset = 0.0f;

    static {
        texture = Bullet.loadTexture("arrow");
    }

    public Arrow(GameScreen game, WeaponProto proto, float x, float y, float angle) {
        super(game, texture, proto, x, y, angle);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!moving && hitEntity == null) {
            if (game.getCollision().collidesWithEnemy(this)) {
                hitEntity = game.getCollision().getEntity();
                rotationOffset = getRotation() - hitEntity.getRotation();
            }
        }

        if (hitEntity != null && hitEntity.isAlive()) {
            setRotation(hitEntity.getRotation() + rotationOffset);
            setX(hitEntity.getWorldX() - getOriginX() + (float) Math.cos(Math.toRadians((double) getRotation() + 180.0f)) * hitEntity.getOriginX());
            setY(hitEntity.getWorldY() - getOriginY() + (float) Math.sin(Math.toRadians((double) getRotation() + 180.0f)) * hitEntity.getOriginY());
        }
    }

    @Override
    public boolean isMoving() {
        return true;
    }
}
