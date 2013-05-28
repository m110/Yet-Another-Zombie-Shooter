package org.m110.shooter.pickups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.actors.Player;
import org.m110.shooter.weapons.Pistol;
import org.m110.shooter.weapons.Rifle;
import org.m110.shooter.weapons.Shotgun;
import org.m110.shooter.weapons.Weapon;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class CratePickup extends Pickup {

    private int bullets;

    public CratePickup(String name, float x, float y, int bullets) {
        super(name, x, y, "crate");
        this.bullets = bullets;
    }

    @Override
    public boolean pickUp(Player player) {
        Weapon weapon = null;
        switch (name) {
            case "pistol":
                weapon = new Pistol(player.getGame());
                break;
            case "shotgun":
                weapon = new Shotgun(player.getGame());
                break;
            case "rifle":
                weapon = new Rifle(player.getGame());
                break;
        }

        if (weapon != null) {
            if (bullets > 0) {
                weapon.setActiveMagazineAmmo(bullets);
            }
            return player.addWeapon(weapon);
        } else {
            return false;
        }
    }
}