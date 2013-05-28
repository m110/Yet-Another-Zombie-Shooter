package org.m110.shooter.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.actors.Bullet;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.magazines.Magazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Weapon {

    protected final GameScreen game;

    private final TextureRegion texture;
    private final WeaponSlot slot;

    protected final Sound readySound;
    protected final Sound reloadSound;
    protected final Sound fireSound;
    protected final Sound emptySound;

    private float bulletVelocity = Bullet.BASE_VELOCITY;
    private float offsetFactor = 2.5f;
    private int bulletsCount = 1;
    protected float cooldown = 0.5f;
    protected float cooldownLeft = 0.0f;
    protected float reloadCooldown = 0.50f;
    protected float reloadCooldownLeft = 0.0f;
    protected int defaultMagazineCapacity = 10;

    protected int damage = 10;

    private int maxMagazines = 5;
    private Array<Magazine> magazines;
    protected Magazine activeMagazine;

    public Weapon(GameScreen game, int textureNumber, WeaponSlot slot, String name) {
        this.game = game;

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
        // No magazine or weapon cooldown or still reloading
        if (activeMagazine == null || cooldownLeft > 0.0f || reloadCooldownLeft > 0.0f) {
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
            Bullet bullet = new Bullet(game, x, y, angle, bulletVelocity, damage);
            bullets.add(bullet);
        }

        cooldownLeft = cooldown;
        activeMagazine.takeBullet();

        return bullets;
    }

    public void reload() {
        if (magazines.size == 1 || reloadCooldownLeft > 0) {
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
        reloadCooldownLeft = reloadCooldown;
    }

    public boolean addMagazine(Magazine magazine) {
        if (magazines.size < maxMagazines) {
            magazines.add(magazine);
            if (activeMagazine == null) {
                activeMagazine = magazine;
            }
            reloadSound.play();
            return true;
        } else {
            return false;
        }
    }

    public void drawMagazines(float x, float y, ShapeRenderer renderer, SpriteBatch batch) {
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

            BitmapFont font = Shooter.getInstance().getSmallFont();
            batch.begin();
            font.draw(batch, ""+magazine.getBullets(), x-1, y+42);
            batch.end();

            x += 20;
        }
    }

    public void updateCooldown(float delta) {
        cooldownLeft -= delta;
        reloadCooldownLeft -= delta;
    }

    public void setActiveMagazineAmmo(int ammo) {
        activeMagazine.setBullets(ammo);
    }

    public Magazine getActiveMagazine() {
        return activeMagazine;
    }

    public void setActive() {
        readySound.play();
        cooldownLeft = 0.0f;
    }

    protected void setBulletVelocity(float bulletVelocity) {
        this.bulletVelocity = bulletVelocity;
    }

    protected void setOffsetFactor(float offsetFactor) {
        this.offsetFactor = offsetFactor;
    }

    protected void setBulletsCount(int bulletsCount) {
        this.bulletsCount = bulletsCount;
    }

    protected void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    protected void setReloadCooldown(float reloadCooldown) {
        this.reloadCooldown = reloadCooldown;
    }

    protected void setMaxMagazines(int maxMagazines) {
        this.maxMagazines = maxMagazines;
    }

    protected void setDamage(int damage) {
        this.damage = damage;
    }

    protected void setDefaultMagazineCapacity(int defaultMagazineCapacity) {
        this.defaultMagazineCapacity = defaultMagazineCapacity;
    }

    public int getDefaultMagazineCapacity() {
        return defaultMagazineCapacity;
    }

    public void playReloadSound() {
        reloadSound.play();
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public WeaponSlot getSlot() {
        return slot;
    }
}
