package org.m110.shooter.ai;

import org.m110.shooter.actors.Player;
import org.m110.shooter.actors.ShooterActor;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class BasicAI extends AI {

    private final float updateInterval = 0.04f;
    private float updateTime = 0.0f;

    @Override
    public void act(ShooterActor me, float delta) {
        if (me.isDead()) {
            return;
        }

        if (updateTime < 0) {
            Player player = me.getGame().getPlayer();
            float distance = me.distanceTo(player);
            if (distance < me.getWidth() + player.getWidth()) {
                me.attack(player);
            } else if (distance < 400.0f) {
                me.lookAt(player.getWorldX(), player.getWorldY());

                float newX = me.getWorldX() + (float) Math.cos(Math.toRadians(me.getRotation())) * me.getVelocity();
                float newY = me.getWorldY() + (float) Math.sin(Math.toRadians(me.getRotation())) * me.getVelocity();

                me.move(newX, newY);
            }
            updateTime = updateInterval;
        } else updateTime -= delta;
    }
}
