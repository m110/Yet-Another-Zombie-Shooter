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
    private final float startTime = 0.8f;
    private final CountdownTimer timer;

    private final Array<Notification> notifications;

    private final Sound firstBlood;
    private final Sound doubleKill;
    private final Sound tripleKill;
    private final Sound multiKill;
    private final Sound rampage;
    private final Sound killingSpree;

    private final float notificationX = 20.0f;
    private final float notificationY = Gdx.graphics.getHeight() - 100.0f;

    public StreakSystem() {
        timer = new CountdownTimer(startTime);
        timer.disable();
        notifications = new Array<>();

        firstBlood = Gdx.audio.newSound(Gdx.files.internal(Config.AUDIO_DIR + "firstblood.ogg"));
        doubleKill = Gdx.audio.newSound(Gdx.files.internal(Config.AUDIO_DIR + "doublekill.ogg"));
        tripleKill = Gdx.audio.newSound(Gdx.files.internal(Config.AUDIO_DIR + "triplekill.ogg"));
        multiKill = Gdx.audio.newSound(Gdx.files.internal(Config.AUDIO_DIR + "multikill.ogg"));
        rampage = Gdx.audio.newSound(Gdx.files.internal(Config.AUDIO_DIR + "rampage.ogg"));
        killingSpree = Gdx.audio.newSound(Gdx.files.internal(Config.AUDIO_DIR + "killingspree.ogg"));
    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        for (Notification notification : notifications) {
            notification.draw(batch);
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
        if (totalKills == 0) {
            firstBlood.play();
        }

        timer.reset(startTime + 0.1f * kills);
        kills++;

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
            case 6:
                rampage.play();
                addNotification("Rampage!");
                break;
            case 7:
                killingSpree.play();
                addNotification("Killing spree!");
                break;
            case 9:
                addNotification("Dominating!");
                break;
            case 11:
                addNotification("Unstoppable!");
                break;
            case 13:
                addNotification("Mega kill!");
                break;
            case 15:
                addNotification("Ultra kill!");
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
