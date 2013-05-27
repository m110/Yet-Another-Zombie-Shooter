package org.m110.shooter.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.actors.Bullet;
import org.m110.shooter.weapons.magazines.Magazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Weapon {

    private final TextureRegion texture;
    private final WeaponSlot slot;

    protected final Sound readySound;
    protected final Sound reloadSound;
    protected final Sound fireSound;
    protected final Sound emptySound;

    private float bulletVelocity = Bullet.BASE_VELOCITY;
    private float offsetFactor = 2.5f;
    private int bulletsCount = 1;
    private float cooldown = 0.5f;
    private float cooldownLeft = 0.0f;

    private int maxMagazines = 5;
    private Array<Magazine> magazines;
    protected Magazine activeMagazine;

    public Weapon(int textureNumber, WeaponSlot slot, String name) {
        texture = new TextureRegion(new Texture(Gdx.files.internal("images/weapons.png")),
                                    textureNumber*48, 0, 48, 48);
        this.slot = slot;

        readySound = Gdx.audio.newSound(Gdx.files.internal("audio/"+name+"_ready.ogg"));
        reloadSound = Gdx.audio.newSound(Gdx.files.internal("audio/"+name+"_ready.ogg"));
        fireSound = Gdx.audio.newSound(Gdx.files.internal("audio/"+name+"_fire.ogg"));
        emptySound = Gdx.audio.newSound(Gdx.files.internal("audio/empty.ogg"));

        magazines = new Array<>();
        activeMagazine = null;
    }

    public Array<Bullet> fire(float x, float y, float rotation) {
        if (activeMagazine == null || cooldownLeft > 0.0f) {
            return null;
        }

        if (activeMagazine.isEmpty()) {
            emptySound.play();
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
        activeMagazine.takeBullet();

        return bullets;
    }

    public void reload() {
        if (magazines.size == 1) {
            return;
        }

        // Play reload sound
        reloadSound.play();

        int index = magazines.indexOf(activeMagazine, true);
        if (index == magazines.size-1) {
            index = 0;
        } else {
            index++;
        }

        activeMagazine = magazines.get(index);
    }

    public void addMagazine(Magazine magazine) {
        if (magazines.size < maxMagazines) {
            magazines.add(magazine);
            if (activeMagazine == null) {
                activeMagazine = magazine;
            }
        }
    }

    public void drawMagazines(float x, float y, ShapeRenderer renderer) {
        for (Magazine magazine : magazines) {
            if (magazine == activeMagazine) {
                renderer.setColor(Color.WHITE);
            } else if (magazine.isEmpty()) {
                renderer.setColor(Color.RED);
            } else {
                renderer.setColor(Color.DARK_GRAY);
            }
            renderer.begin(ShapeRenderer.ShapeType.Rectangle);
            renderer.rect(x, y, 14, 30);
            renderer.end();

            renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
            renderer.filledRect(x, y, 14, magazine.getBulletsPercent() * 30);
            renderer.end();

            x += 20;
        }
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

    public void setMaxMagazines(int maxMagazines) {
        this.maxMagazines = maxMagazines;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public WeaponSlot getSlot() {
        return slot;
    }
}
