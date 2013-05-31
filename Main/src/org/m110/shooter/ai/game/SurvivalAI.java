package org.m110.shooter.ai.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.timers.RandomIntervalTimer;
import org.m110.shooter.entities.enemies.EntityProto;
import org.m110.shooter.entities.enemies.Spawner;
import org.m110.shooter.entities.terrain.Dummy;
import org.m110.shooter.pickups.Crate;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class SurvivalAI extends GameAI {

    private Array<Dummy> dummies;
    private final RandomIntervalTimer spawnTimer;
    private final RandomIntervalTimer pickupTimer;

    private int challengeCounter = 1;

    public SurvivalAI() {
        spawnTimer = new RandomIntervalTimer(5.0f, 15.0f);
        pickupTimer = new RandomIntervalTimer(10.0f, 20.0f);
        Shooter.getInstance().getGame().setAggroRange(5000.0f);
    }

    @Override
    public void start() {
        this.dummies = Shooter.getInstance().getGame().getDummies();

        // Spawn initial entities and pickups
        for (int i = 0; i < 3; i++) {
            Dummy randomDummy = dummies.random();
            Shooter.getInstance().getGame().spawnRandomEntity(randomDummy.getX(), randomDummy.getY());
        }

        Dummy randomDummy = dummies.random();
        Shooter.getInstance().getGame().addPickup(new Crate("pistol", randomDummy.getX(), randomDummy.getY(), 0));
        randomDummy = dummies.random();
        Shooter.getInstance().getGame().addPickup(new Crate("shotgun", randomDummy.getX(), randomDummy.getY(), 0));
        randomDummy = dummies.random();
        Shooter.getInstance().getGame().addPickup(new Crate("rifle", randomDummy.getX(), randomDummy.getY(), 0));
    }

    @Override
    public void act(float delta) {
        spawnTimer.update(delta);
        pickupTimer.update(delta);

        Dummy randomDummy = dummies.random();
        float x = randomDummy.getX();
        float y = randomDummy.getY();

        if (spawnTimer.ready()) {
            if (challengeCounter % 5 == 0) {
                if (MathUtils.random(1) == 0) {
                    for (int i = 0; i < 3; i++) {
                        Shooter.getInstance().getGame().spawnRandomEntity(
                                x + MathUtils.random(-50.0f, 50.0f), y + MathUtils.random(-50.0f, 50.0f));
                    }
                } else {
                    Spawner spawner = new Spawner(x, y, EntityProto.getRandomWithoutSpawner().toString().toLowerCase(),
                                                  MathUtils.random(0.5f, 3.0f), MathUtils.random(5, 15));
                    Shooter.getInstance().getGame().addEntity(spawner);
                }
            } else {
                Shooter.getInstance().getGame().spawnRandomEntity(x, y);
            }

            spawnTimer.reset();
            challengeCounter++;
        }

        if (pickupTimer.ready()) {
            Shooter.getInstance().getGame().spawnRandomPickup(x, y);
            pickupTimer.reset();
        }
    }
}
