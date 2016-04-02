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

    protected AuraEffect effect = null;
    protected AuraPeriodicEffect periodicEffect = null;

    protected final TextureRegion texture;
    protected final String textureName;

    protected boolean effectDone = false;

    protected final float duration;
    protected int ticks;
    protected int ticksDone = 0;

    public Aura(Entity owner, String textureName, float duration, int ticks, AuraPeriodicEffect periodicEffect) {
        this(owner, textureName, duration, null);
        this.ticks = ticks;
        this.periodicEffect = periodicEffect;
        tickTimer = new IntervalTimer(duration / ticks);
    }

    public Aura(Entity owner, String textureName, float duration, AuraEffect effect) {
        this.owner = owner;
        this.textureName = textureName;
        this.duration = duration;
        this.effect = effect;
        this.ticks = 0;

        mainTimer = new CountdownTimer(duration);

        texture = new TextureRegion(new Texture(Gdx.files.internal(Config.Path.TEXTURES_DIR + "auras/" + textureName + ".png")));
    }

    public void update(float delta) {
        if (!effectDone) {
            if (effect != null) {
                effect.effect(owner);
            }
            effectDone = true;
        }

        mainTimer.update(delta);
        if (!isActive()) {
            return;
        }

        if (periodicEffect != null) {
            tickTimer.update(delta);
            if (tickTimer.ready()) {
                periodicEffect.effect(owner, ticks);
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
