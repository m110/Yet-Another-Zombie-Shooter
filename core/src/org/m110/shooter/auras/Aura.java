package org.m110.shooter.auras;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.core.Config;
import org.m110.shooter.core.timers.CountdownTimer;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;

/**
 * Represents any effect over time applied on Entity.
 */
public class Aura {

    protected final Entity owner;

    protected CountdownTimer mainTimer;
    protected IntervalTimer tickTimer;

    protected AuraEffect effect = null;
    protected AuraPeriodicEffect periodicEffect = null;

    private final TextureRegion texture;

    private boolean effectApplied = false;

    private final float duration;
    private int ticks = 0;
    private int ticksDone = 0;

    public Aura(Entity owner, String textureName, float duration, int ticks, AuraPeriodicEffect periodicEffect) {
        this(owner, textureName, duration, null);
        this.ticks = ticks;
        this.periodicEffect = periodicEffect;
        tickTimer = new IntervalTimer(duration / ticks);
    }

    public Aura(Entity owner, String textureName, float duration, AuraEffect effect) {
        this.owner = owner;
        this.duration = duration;
        this.effect = effect;

        mainTimer = new CountdownTimer(duration);

        texture = new TextureRegion(new Texture(Gdx.files.internal(Config.Path.TEXTURES_DIR + "auras/" + textureName + ".png")));
    }

    public void update(float delta) {
        if (!isActive()) {
            return;
        }

        if (!effectApplied) {
            if (effect != null) {
                effect.apply(owner);
            }
            effectApplied = true;
        }

        mainTimer.update(delta);
        if (!isActive()) {
            if (effect != null) {
                effect.wearOff(owner);
            }
            return;
        }

        if (periodicEffect != null) {
            tickTimer.update(delta);
            if (tickTimer.ready()) {
                periodicEffect.tick(owner, ticks);
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

    public boolean isActive() {
        return !mainTimer.ready() || ticksDone < ticks;
    }
}
