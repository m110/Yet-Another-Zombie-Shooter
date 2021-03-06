package org.m110.shooter.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.core.Font;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.magazines.Magazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Shotgun extends Weapon {

    private final Magazine mainMagazine;

    public Shotgun(GameScreen game, WeaponProto proto) {
        super(game, proto);
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
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.rect(x, y, 120, 18);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(x, y, activeMagazine.getBulletsPercent() * 120, 18);
        renderer.end();

        BitmapFont font = Font.medium;
        batch.begin();
        font.draw(batch, activeMagazine.getBullets() + " / " + mainMagazine.getBullets(), x, y + 40);
        batch.end();
    }

    @Override
    public Magazine dropMagazine() {
        // No action for shotgun
        return null;
    }

    @Override
    public boolean isMagazineFull() {
        return mainMagazine.isFull();
    }
}
