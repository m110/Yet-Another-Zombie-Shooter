package org.m110.shooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.m110.shooter.Shooter;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Shooter";
        cfg.width = 800;
        cfg.height = 600;
        new LwjglApplication(new Shooter(), cfg);
    }
}
