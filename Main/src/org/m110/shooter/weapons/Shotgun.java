package org.m110.shooter.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.Shooter;
import org.m110.shooter.actors.Bullet;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.magazines.ShotgunMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shotgun extends Weapon {

    private ShotgunMagazine magazine;

    public Shotgun(GameScreen game) {
        super(game, 1, WeaponSlot.SHOTGUN, "shotgun");
        setBulletsCount(6);
        setDefaultMagazineCapacity(8);
        setBulletVelocity(Bullet.BASE_VELOCITY + 1.0f);
        setCooldown(0.75f);
        setReloadCooldown(0.70f);
        setOffsetFactor(14.5f);
        setMaxMagazines(1);
        setDamage(20);

        magazine = new ShotgunMagazine(defaultMagazineCapacity, defaultMagazineCapacity, 0);
        addMagazine(magazine);
    }

    @Override
    public void reload() {
        if (reloadCooldownLeft > 0) {
            return;
        }

        if (magazine.loadBullet()) {
            reloadSound.play();
            reloadCooldownLeft = reloadCooldown;
        }
    }

    @Override
    public void drawMagazines(float x, float y, ShapeRenderer renderer, SpriteBatch batch) {
        renderer.setColor(Color.WHITE);
        renderer.begin(ShapeRenderer.ShapeType.Rectangle);
        renderer.rect(x, y, 120, 18);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(x, y, magazine.getBulletsPercent() * 120, 18);
        renderer.end();

        BitmapFont font = Shooter.getInstance().getMediumFont();
        batch.begin();
        font.draw(batch, magazine.getBullets()+" / " +magazine.getAllBullets(), x, y+40);
        batch.end();
    }
}
