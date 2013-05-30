package org.m110.shooter.pickups;

import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.weapons.WeaponSlot;
import org.m110.shooter.weapons.WeaponType;

import java.util.HashMap;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class PickupFactory {
    private PickupFactory() {}

    public static Pickup createPickup(TiledObject object, float x, float y) {
        switch (object.type) {
            case "ammo":
                return new Ammo(object.name, x, y, getIntegerProperty(object, "ammo"));
            case "crate":
                return new Crate(object.name, x, y, getIntegerProperty(object, "ammo"));
            case "bonus":
                switch (object.name) {
                    case "medpack":
                        return new Medpack(x, y);
                    case "adrenaline":
                        return new Adrenaline(x, y);
                    default:
                        throw new IllegalArgumentException("No such bonus: " + object.name);
                }
            default:
                throw new IllegalArgumentException("No such Pickup type: " + object.type);
        }
    }

    public static Pickup createRandomPickup(float x, float y) {
        PickupType type = PickupType.getRandom();
        switch (type) {
            case AMMO:
                WeaponType weaponType = WeaponType.getRandom();
                int ammo =  MathUtils.random(1, weaponType.getMagazineCapacity());
                return new Ammo(weaponType.toString().toLowerCase(), x, y, ammo);
            case CRATE:
                weaponType = WeaponType.getRandom();
                ammo =  MathUtils.random(1, weaponType.getMagazineCapacity());
                return new Ammo(weaponType.toString().toLowerCase(), x, y, ammo);
            case MEDPACK:
                return new Medpack(x, y);
            case ADRENALINE:
                return new Adrenaline(x, y);
            default:
                throw new IllegalArgumentException("No such PickupType: " + type);
        }
    }

    private static Integer getIntegerProperty(TiledObject object, String property) {
        Integer result;
        try {
            result = Integer.parseInt(object.properties.get(property));
        } catch (NumberFormatException e) {
            result = 0;
        }
        return result;
    }
}
