package org.m110.shooter.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class ShooterActor extends Actor {

    protected final GameScreen game;

    public ShooterActor(GameScreen game) {
        this.game = game;
    }


}
