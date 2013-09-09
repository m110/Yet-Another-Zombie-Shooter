package org.m110.shooter;

import com.badlogic.gdx.backends.lwjgl.LwjglApplet;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class AppletStarter extends LwjglApplet {

    private static final long serialVersionUID = 1L;

    public AppletStarter() {
        super(new Shooter(), true);
    }
}
