package org.m110.shooter.pickups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.actors.Player;
import org.m110.shooter.weapons.Weapon;
import org.m110.shooter.weapons.WeaponSlot;
import org.m110.shooter.weapons.magazines.Magazine;
import org.m110.shooter.weapons.magazines.ShotgunMagazine;
import org.m110.shooter.weapons.magazines.StandardMagazine;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class AmmoPickup extends Pickup {

    private int bullets;

    public AmmoPickup(String name, float x, float y, int bullets) {
        super(name, x, y, "ammo");
        this.bullets = bullets;
    }

    @Override
    public boolean pickUp(Player player) {
        Magazine magazine = null;
        Weapon weapon = null;
        switch (name) {
            case "pistol":
                weapon = player.getWeapon(WeaponSlot.PISTOL);
                if (weapon != null) {
                    magazine = new StandardMagazine(weapon.getDefaultMagazineCapacity(), bullets);
                }
                break;
            case "shotgun":
                weapon = player.getWeapon(WeaponSlot.SHOTGUN);
                if (weapon != null) {
                    ShotgunMagazine shotgunMagazine = (ShotgunMagazine) weapon.getActiveMagazine();
                    shotgunMagazine.addAllBullets(bullets);
                    weapon.playReloadSound();
                    return true;
                }
                break;
            case "rifle":
                weapon = player.getWeapon(WeaponSlot.RILE);
                if (weapon != null) {
                    magazine = new StandardMagazine(weapon.getDefaultMagazineCapacity(), bullets);
                }
                break;
        }

        if (magazine != null) {
            return weapon.addMagazine(magazine);
        } else {
            return false;
        }
    }
}
