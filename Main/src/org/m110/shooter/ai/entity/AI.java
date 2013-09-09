package org.m110.shooter.ai.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.m110.shooter.Shooter;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class AI {
    protected final Entity me;
    protected final GameScreen game;
    protected Player player;

    public AI(Entity me) {
        this.me = me;

        if (me != null) {
            game = me.getGame();
            player = game.getPlayer();
        } else {
            game = null;
        }
    }

    public void act(float delta) {}
    public void draw(SpriteBatch batch) {}

    public void afterHit(Entity attacker) {}
    public void afterCollision() {}
    public void afterDeath() {}
}
