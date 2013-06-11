package org.m110.shooter.pickups;

import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.weapons.WeaponProto;

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
        PickupProto type = PickupProto.getRandom();
        switch (type) {
            case AMMO: return createRandomAmmo(x, y);
            case CRATE: return createRandomCrate(x, y);
            case MEDPACK:
                return new Medpack(x, y);
            case ADRENALINE:
                return new Adrenaline(x, y);
            default:
                throw new IllegalArgumentException("No such PickupProto: " + type);
        }
    }

    public static Pickup createAmmoOrCrate(float x, float y) {
        PickupProto type = MathUtils.random(1) == 0 ? PickupProto.AMMO : PickupProto.CRATE;
        switch (type) {
            case AMMO: return createRandomAmmo(x, y);
            case CRATE: return createRandomCrate(x, y);
        }
        return null;
    }

    public static Pickup createRandomAmmo(float x, float y) {
        WeaponProto weaponProto = WeaponProto.getRandom();
        int ammo =  MathUtils.random(1, weaponProto.magazineCapacity);
        return new Ammo(weaponProto.name, x, y, ammo);
    }

    public static Pickup createRandomCrate(float x, float y) {
        WeaponProto weaponProto = WeaponProto.getRandom();
        int ammo =  MathUtils.random(1, weaponProto.magazineCapacity);
        return new Crate(weaponProto.name, x, y, ammo);
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
