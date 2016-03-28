package org.m110.shooter.auras;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.core.Config;
import org.m110.shooter.core.timers.CountdownTimer;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Aura {

    protected final Entity owner;

    protected CountdownTimer mainTimer;
    protected IntervalTimer tickTimer;

    protected AuraAction action = null;
    protected AuraAction tick = null;

    protected final TextureRegion texture;
    protected final String textureName;
    protected final float duration;
    protected int ticks;
    protected int ticksDone = 0;

    public Aura(Entity owner, String textureName, float duration, int ticks, AuraAction action, AuraAction tick) {
        this(owner, textureName, duration, action);
        this.ticks = ticks;
        this.tick = tick;
        tickTimer = new IntervalTimer(duration / ticks);
    }

    public Aura(Entity owner, String textureName, float duration, AuraAction action) {
        this.owner = owner;
        this.textureName = textureName;
        this.duration = duration;
        this.action = action;
        this.ticks = 0;

        mainTimer = new CountdownTimer(duration);

        texture = new TextureRegion(new Texture(Gdx.files.internal(Config.TEXTURES_DIR + "auras/" + textureName + ".png")));
    }

    public void update(float delta) {
        mainTimer.update(delta);
        if (!isActive()) {
            return;
        }

        action.action();

        if (tickTimer != null) {
            tickTimer.update(delta);
            if (tickTimer.ready()) {
                tick.action();
                ticksDone++;
                tickTimer.reset();
            }
        }
    }

    public float getDonePercent() {
        return mainTimer.getTimeLeft() / duration;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public float getDuration() {
        return duration;
    }

    public boolean isActive() {
        return !mainTimer.ready() || ticksDone < ticks;
    }
}
