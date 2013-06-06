package org.m110.shooter.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.timers.CountdownTimer;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class StreakSystem {

    private int kills = 0;
    private int totalKills = 0;
    private int bestStreak = 0;
    private final CountdownTimer timer;

    private final Array<Notification> notifications;

    private final Sound doubleKill;
    private final Sound tripleKill;
    private final Sound multiKill;

    private final float notificationX = 20.0f;
    private final float notificationY = Gdx.graphics.getHeight() - 100.0f;

    public StreakSystem() {
        timer = new CountdownTimer(1.0f);
        timer.disable();
        notifications = new Array<>();

        doubleKill = Gdx.audio.newSound(Gdx.files.internal("audio/doublekill.ogg"));
        tripleKill = Gdx.audio.newSound(Gdx.files.internal("audio/triplekill.ogg"));
        multiKill = Gdx.audio.newSound(Gdx.files.internal("audio/multikill.ogg"));
    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        for (Notification notification : notifications) {
            notification.draw(batch);
        }

        if (kills > 1) {
            Shooter.getInstance().getMediumFont().draw(batch, "Kill streak: " + kills,
                Gdx.graphics.getWidth() - 160.0f, Gdx.graphics.getHeight() - 100.0f);
        }
        batch.end();
    }

    public void update(float delta) {
        for (Notification notification : notifications) {
            notification.update(delta);
        }

        if (timer.ready()) {
            kills = 0;
            return;
        }

        timer.update(delta);
    }

    public void addKill() {
        kills++;
        timer.reset();

        totalKills++;

        if (kills > bestStreak) {
            bestStreak = kills;
        }

        switch (kills) {
            case 2:
                doubleKill.play();
                addNotification("Double kill!");
                break;
            case 3:
                tripleKill.play();
                addNotification("Triple kill!");
                break;
            case 5:
                multiKill.play();
                addNotification("Multi kill!");
                break;
        }
    }

    private void addNotification(String message) {
        for (Notification notification : notifications) {
            notification.moveDown();
        }

        notifications.add(new Notification(message, notificationX, notificationY));
    }

    public int getKills() {
        return kills;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getBestStreak() {
        return bestStreak;
    }
}
