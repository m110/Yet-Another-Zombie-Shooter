package org.m110.shooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Shooter";
        cfg.width = 800;
        cfg.height = 600;

        String disableLog = System.getenv("DISABLE_ERROR_LOG");
        if (disableLog == null || !disableLog.equals("1")) {
            setExceptionHandler();
        }

        new LwjglApplication(new Shooter(), cfg);
    }

    private static void setExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                FileWriter fw = new FileWriter("error.log", false);
                PrintWriter pw = new PrintWriter(fw);

                e.printStackTrace(pw);

                pw.close();
                fw.close();
            } catch (IOException ignored) {
            }
        });
    }
}
