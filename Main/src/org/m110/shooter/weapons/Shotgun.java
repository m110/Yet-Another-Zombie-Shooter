package org.m110.shooter.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.actors.Bullet;
import org.m110.shooter.weapons.magazines.ShotgunMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shotgun extends Weapon {

    private ShotgunMagazine magazine;

    public Shotgun() {
        super(1, WeaponSlot.SHOTGUN, "shotgun");
        setBulletsCount(6);
        setBulletVelocity(Bullet.BASE_VELOCITY + 1.0f);
        setCooldown(0.75f);
        setOffsetFactor(6.5f);
        setMaxMagazines(1);

        magazine = new ShotgunMagazine(8, 8, 16);
        addMagazine(magazine);
    }

    @Override
    public void reload() {
        if (magazine.loadBullet()) {
            reloadSound.play();
        }
    }

    @Override
    public void drawMagazines(float x, float y, ShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);
        renderer.begin(ShapeRenderer.ShapeType.Rectangle);
        renderer.rect(x, y, 100, 18);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(x, y, magazine.getBulletsPercent() * 100, 18);
        renderer.end();
    }
}
