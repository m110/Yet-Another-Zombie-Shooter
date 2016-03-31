package org.m110.shooter.ai.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.core.timers.RandomIntervalTimer;
import org.m110.shooter.entities.EntityProto;
import org.m110.shooter.entities.enemies.Spawner;
import org.m110.shooter.entities.terrain.Dummy;
import org.m110.shooter.pickups.PickupFactory;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class SurvivalAI extends GameAI {

    private Array<Dummy> dummies;
    private final IntervalTimer spawnTimer;
    private final RandomIntervalTimer weaponTimer;
    private final RandomIntervalTimer pickupTimer;

    private int challengeCounter = 1;

    private final float spawnTime = 8.0f;

    public SurvivalAI(GameScreen game) {
        super(game);

        spawnTimer = new IntervalTimer(spawnTime);
        weaponTimer = new RandomIntervalTimer(5.0f, 10.0f);
        pickupTimer = new RandomIntervalTimer(10.0f, 15.0f);
        game.setAggroRange(5000.0f);
    }

    @Override
    public void start() {
        this.dummies = game.getDummies();

        if (dummies.size == 0) {
            return;
        }

        // Spawn initial entities and pickups
        for (int i = 0; i < 3; i++) {
            Dummy randomDummy = dummies.random();
            game.spawnRandomEntity(randomDummy.getX(), randomDummy.getY());
        }

        for (int i = 0; i < 3; i++) {
            Dummy randomDummy = dummies.random();
            game.addPickup(PickupFactory.createRandomCrate(game, randomDummy.getX(), randomDummy.getY()));
        }
    }

    @Override
    public void act(float delta) {
        spawnTimer.update(delta);
        weaponTimer.update(delta);
        pickupTimer.update(delta);

        if (weaponTimer.ready()) {
            game.addPickup(PickupFactory.createAmmoOrCrate(game, game.getStartNode().getX(),
                    game.getStartNode().getY()));
            weaponTimer.reset();
        }

        if (!spawnTimer.ready() && !pickupTimer.ready() && !weaponTimer.ready()) {
            return;
        }

        if (dummies.size == 0) {
            return;
        }

        Dummy randomDummy = dummies.random();
        float x = randomDummy.getX();
        float y = randomDummy.getY();

        if (spawnTimer.ready()) {
            if (challengeCounter % 5 == 0) {
                if (MathUtils.random(1) == 0) {
                    for (int i = 0; i < 3; i++) {
                        game.spawnRandomEntity(x + 10 + MathUtils.random(-40.0f, 40.0f),
                                               y + 10 + MathUtils.random(-40.0f, 40.0f));
                    }
                } else {
                    Spawner spawner = new Spawner(game, EntityProto.getRandomWithoutSpawner(), x, y,
                                                  MathUtils.random(1.0f, 3.0f), MathUtils.random(3, 5));
                    game.addEntity(spawner);
                }
            } else {
                game.spawnRandomEntity(x, y);
            }

            spawnTimer.reset(spawnTime - 0.01f * (challengeCounter-1) * spawnTime);
            challengeCounter++;
        }

        if (pickupTimer.ready()) {
            game.spawnRandomPickup(x, y);
            pickupTimer.reset();
        }
    }
}
