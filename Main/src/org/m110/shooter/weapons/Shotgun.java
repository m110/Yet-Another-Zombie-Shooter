package org.m110.shooter.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.Shooter;
import org.m110.shooter.weapons.magazines.Magazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shotgun extends Weapon {

    private final Magazine mainMagazine;

    public Shotgun() {
        super(WeaponProto.SHOTGUN);
        mainMagazine = new Magazine(proto.maxMagazines * proto.magazineCapacity, 0);
        addMagazine(mainMagazine);
    }

    @Override
    public boolean addMagazine(int bullets) {
        if (mainMagazine != null) {
            playReloadSound();
            return mainMagazine.addBullets(bullets);
        } else {
            return super.addMagazine(bullets);
        }
    }

    @Override
    public void reload() {
        if (!reloadTimer.ready()) {
            return;
        }

        if (!mainMagazine.isEmpty() && !activeMagazine.isFull()) {
            mainMagazine.takeBullet();
            activeMagazine.addBullets(1);
            playReloadSound();
            reloadTimer.reset();
        }
    }

    @Override
    public void drawMagazines(float x, float y, ShapeRenderer renderer, SpriteBatch batch) {
        renderer.setColor(Color.WHITE);
        renderer.begin(ShapeRenderer.ShapeType.Rectangle);
        renderer.rect(x, y, 120, 18);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(x, y, activeMagazine.getBulletsPercent() * 120, 18);
        renderer.end();

        BitmapFont font = Shooter.getInstance().getMediumFont();
        batch.begin();
        font.draw(batch, activeMagazine.getBullets() + " / " + mainMagazine.getBullets(), x, y + 40);
        batch.end();
    }

    @Override
    public Magazine dropMagazine() {
        // No action for shotgun
        return null;
    }
}
