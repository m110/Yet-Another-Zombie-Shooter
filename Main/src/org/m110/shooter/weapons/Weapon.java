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
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.entities.bullets.BulletFactory;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.magazines.Magazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Weapon {

    protected final GameScreen game;

    private static final Texture mainTexture;
    private final TextureRegion texture;

    protected final WeaponProto proto;

    protected final Sound readySound;
    protected final Sound reloadSound;
    protected final Sound fireSound;
    protected static final Sound emptySound;

    // Cooldowns
    protected final IntervalTimer cooldownTimer;
    protected final IntervalTimer reloadTimer;

    // Magazines
    private final Array<Magazine> magazines;
    protected Magazine activeMagazine;

    static {
        mainTexture = new Texture("images/weapons.png");
        emptySound = Gdx.audio.newSound(Gdx.files.internal("audio/empty.ogg"));
    }

    /**
     * Creates new instance of Weapon by given prototype.
     * @param proto prototype of the created weapon.
     * @return new instance of Weapon.
     */
    public static Weapon createInstance(WeaponProto proto) {
        switch (proto.slot) {
            case SHOTGUN:
                return new Shotgun();
            default:
                return new Weapon(proto);
        }
    }

    /**
     * Creates new weapon by given prototype.
     * @param proto weapon prototype.
     */
    protected Weapon(WeaponProto proto) {
        this.proto = proto;
        this.game = Shooter.getInstance().getGame();

        texture = new TextureRegion(mainTexture, proto.textureID * 48, 0, 48, 48);

        readySound = Gdx.audio.newSound(Gdx.files.internal("audio/" + proto.name + "_ready.ogg"));
        reloadSound = Gdx.audio.newSound(Gdx.files.internal("audio/" + proto.name + "_ready.ogg"));
        fireSound = Gdx.audio.newSound(Gdx.files.internal("audio/" + proto.name + "_fire.ogg"));

        cooldownTimer = new IntervalTimer(proto.cooldown);
        reloadTimer = new IntervalTimer(proto.reloadCooldown);

        magazines = new Array<>();
        activeMagazine = null;

        // Add first magazine.
        addMagazine(proto.magazineCapacity);
    }

    /**
     * Set this weapon as active. Play ready sound and reset cooldowns.
     */
    public void setActive() {
        readySound.play();
        cooldownTimer.reset();
        reloadTimer.reset();
    }

    /**
     * Fires the weapon, spawns bullet at given location.
     * @param x
     * @param y
     * @param angle
     * @return
     */
    public Array<Bullet> fire(float x, float y, float angle) {
        Array<Bullet> bullets = new Array<>();

        // No magazine or weapon cooldown or still reloading
        if (activeMagazine == null || !cooldownTimer.ready() || !reloadTimer.ready()) {
            return bullets; // return empty array
        }

        // Active magazine is empty
        if (activeMagazine.isEmpty()) {
            emptySound.play();
            return bullets; // return empty array
        }

        // Play the fire shoot
        fireSound.play();

        // Fire just a single bullet or multiple bullets?
        if (proto.bulletsCount == 1) {
            // Angle = player's angle + random recoil factor
            float totalAngle = angle + MathUtils.random(-proto.recoilFactor, proto.recoilFactor);
            bullets.add(BulletFactory.createBullet(proto, x, y, angle));
        } else {
            // Angle = player's angle - factor, and then increase it by %
            float startAngle = angle - proto.recoilFactor;
            float increase = 2.0f * proto.recoilFactor / proto.bulletsCount;

            for (int i = 0; i < proto.bulletsCount; i++) {
                // Add some additional random offset for each bullet
                float totalAngle = startAngle +
                        MathUtils.random(-proto.recoilFactor, proto.recoilFactor) / 10.0f;

                bullets.add(BulletFactory.createBullet(proto, x, y, totalAngle));

                // Increase angle for the next bullet
                startAngle += increase;
            }
        }

        cooldownTimer.reset();
        activeMagazine.takeBullet();

        return bullets;
    }

    /**
     * Reload the weapon (change magazine).
     */
    public void reload() {
        if (magazines.size == 1 || !reloadTimer.ready()) {
            return;
        }

        // Play reload sound
        reloadSound.play();

        // Find the index of the next magazine
        int index = magazines.indexOf(activeMagazine, true);
        if (index == magazines.size-1) {
            index = 0;
        } else {
            index++;
        }

        activeMagazine = magazines.get(index);
        reloadTimer.reset();
    }

    /**
     * Add new magazine.
     * @param bullets bullets of new magazine.
     * @return true if adding the magazine was successful, false otherwise.
     */
    public boolean addMagazine(int bullets) {
        return addMagazine(new Magazine(proto.magazineCapacity, bullets));
    }

    /**
     * Add new magazine.
     * @param magazine new magazine to add.
     * @return true if adding the magazine was successful, false otherwise.
     */
    protected boolean addMagazine(Magazine magazine) {
        if (magazines.size < proto.maxMagazines) {
            magazines.add(magazine);
            if (activeMagazine == null) {
                activeMagazine = magazine;
            } else {
                if (proto != WeaponProto.SHOTGUN) {
                    reloadSound.play();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Draw weapon's magazines.
     * @param x
     * @param y
     * @param renderer
     * @param batch
     */
    public void drawMagazines(float x, float y, ShapeRenderer renderer, SpriteBatch batch) {
        BitmapFont font = Shooter.getInstance().getSmallFont();
        for (Magazine magazine : magazines) {
            if (magazine == activeMagazine) {
                renderer.setColor(Color.WHITE);
            } else if (magazine.isEmpty()) {
                renderer.setColor(Color.RED);
            } else {
                renderer.setColor(Color.DARK_GRAY);
            }

            // Draw outer rectangle
            renderer.begin(ShapeRenderer.ShapeType.Rectangle);
            renderer.rect(x, y, 14, 30);
            renderer.end();

            // Draw inner filled rectangle
            renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
            renderer.filledRect(x, y, 14, magazine.getBulletsPercent() * 30);
            renderer.end();

            // Draw magazine's bullet count
            batch.begin();
            font.draw(batch, Integer.toString(magazine.getBullets()), x - 1, y + 42);
            batch.end();

            x += 20;
        }
    }

    /**
     * Update weapon cooldowns.
     * @param delta time since last update.
     */
    public void updateCooldowns(float delta) {
        cooldownTimer.update(delta);
        reloadTimer.update(delta);
    }

    /**
     * Set active magazine ammunition.
     * @param ammo bullets amount to be set.
     */
    public void setActiveMagazineAmmo(int ammo) {
        activeMagazine.setBullets(ammo);
    }

    public Magazine getActiveMagazine() {
        return activeMagazine;
    }

    public WeaponProto getProto() {
        return proto;
    }

    public void playReloadSound() {
        reloadSound.play();
    }

    public TextureRegion getTexture() {
        return texture;
    }
}
