package org.m110.shooter.pickups;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class PickupFactory {
    private PickupFactory() {}

    public static Pickup createPickup(GameScreen game, MapObject object, float x, float y) {
        String type = object.getProperties().get("type", String.class);
        String name = object.getName();

        switch (type) {
            case "ammo":
                return new Ammo(name, x, y, getIntegerProperty(object, "ammo"));
            case "crate":
                return new Crate(game, name, x, y, getIntegerProperty(object, "ammo"));
            case "bonus":
                switch (name) {
                    case "medpack":
                        return new Medpack(x, y);
                    case "adrenaline":
                        return new Adrenaline(x, y);
                    default:
                        throw new IllegalArgumentException("No such bonus: " + name);
                }
            default:
                throw new IllegalArgumentException("No such Pickup type: " + type);
        }
    }

    public static Pickup createRandomPickup(GameScreen game, float x, float y) {
        PickupProto type = PickupProto.getRandom();
        switch (type) {
            case AMMO: return createRandomAmmo(x, y);
            case CRATE: return createRandomCrate(game, x, y);
            case MEDPACK:
                return new Medpack(x, y);
            case ADRENALINE:
                return new Adrenaline(x, y);
            default:
                throw new IllegalArgumentException("No such PickupProto: " + type);
        }
    }

    public static Pickup createAmmoOrCrate(GameScreen game, float x, float y) {
        PickupProto type = MathUtils.random(1) == 0 ? PickupProto.AMMO : PickupProto.CRATE;
        switch (type) {
            case AMMO: return createRandomAmmo(x, y);
            case CRATE: return createRandomCrate(game, x, y);
        }
        return null;
    }

    public static Pickup createRandomAmmo(float x, float y) {
        WeaponProto weaponProto = WeaponProto.getRandom();
        int ammo =  MathUtils.random(1, weaponProto.magazineCapacity);
        return new Ammo(weaponProto.name, x, y, ammo);
    }

    public static Pickup createRandomCrate(GameScreen game, float x, float y) {
        WeaponProto weaponProto = WeaponProto.getRandom();
        int ammo =  MathUtils.random(1, weaponProto.magazineCapacity);
        return new Crate(game, weaponProto.name, x, y, ammo);
    }

    private static Integer getIntegerProperty(MapObject object, String property) {
        Integer result;
        try {
            result = Integer.parseInt(object.getProperties().get(property, String.class));
        } catch (NumberFormatException e) {
            result = 0;
        }
        return result;
    }
}
