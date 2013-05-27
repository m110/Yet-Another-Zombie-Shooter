package org.m110.shooter.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.actors.Bullet;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Weapon {

    private final TextureRegion texture;
    private final WeaponSlot slot;

    private final Sound readySound;
    private final Sound reloadSound;
    private final Sound fireSound;
    private final Sound emptySound;

    private float bulletVelocity = Bullet.BASE_VELOCITY;
    private float offsetFactor = 2.5f;
    private int bulletsCount = 1;
    private float cooldown = 0.5f;
    private float cooldownLeft = 0.0f;

    public Weapon(int textureNumber, WeaponSlot slot, String name) {
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/weapons.png")),
                                    textureNumber*48, 0, 48, 48);
        this.slot = slot;

        readySound = Gdx.audio.newSound(Gdx.files.internal("audio/"+name+"_ready.ogg"));
        reloadSound = Gdx.audio.newSound(Gdx.files.internal("audio/"+name+"_ready.ogg"));
        fireSound = Gdx.audio.newSound(Gdx.files.internal("audio/"+name+"_fire.ogg"));
        emptySound = Gdx.audio.newSound(Gdx.files.internal("audio/empty.ogg"));
    }

    public Array<Bullet> fire(float x, float y, float rotation) {
        if (cooldownLeft > 0.0f) {
            return null;
        }

        Array<Bullet> bullets = new Array<>();

        fireSound.play();

        for (int i = 0; i < bulletsCount; i++) {
            // Angle = player's rotation + random factor
            float angle = rotation + MathUtils.random(-offsetFactor, offsetFactor);

            // Create and store new bullet
            Bullet bullet = new Bullet(x, y, angle, bulletVelocity);
            bullets.add(bullet);
        }

        cooldownLeft = cooldown;

        return bullets;
    }

    public void updateCooldown(float delta) {
        cooldownLeft -= delta;
    }

    public void setActive() {
        readySound.play();
        cooldownLeft = 0.0f;
    }

    public void setBulletVelocity(float bulletVelocity) {
        this.bulletVelocity = bulletVelocity;
    }

    public void setOffsetFactor(float offsetFactor) {
        this.offsetFactor = offsetFactor;
    }

    public void setBulletsCount(int bulletsCount) {
        this.bulletsCount = bulletsCount;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public WeaponSlot getSlot() {
        return slot;
    }
}
