package org.m110.shooter.screens;

import com.badlogic.gdx.Screen;
import org.m110.shooter.Shooter;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class ShooterScreen implements Screen {
    protected final Shooter shooter;

    public ShooterScreen(Shooter shooter) {
        this.shooter = shooter;
    }
}
