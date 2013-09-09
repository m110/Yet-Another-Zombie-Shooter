package org.m110.shooter;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Shooter";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 600;
        new LwjglApplication(new Shooter(), cfg);
    }
}
