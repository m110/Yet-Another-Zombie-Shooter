package org.m110.shooter.ai.game;

import org.m110.shooter.entities.Player;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class GameAI {

    protected final GameScreen game;
    protected final Player player;

    public GameAI(GameScreen game) {
        this.game = game;

        if (game != null) {
            player = game.getPlayer();
        } else {
            player = null;
        }
    }

    public void start() {}
    public void act(float delta) {}
}
