package org.m110.shooter.pickups;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import org.m110.shooter.screens.GameScreen;
import org.m110.shooter.weapons.WeaponProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class PickupFactory {
    public static final String AMMO = "ammo";
    public static final String CRATE = "crate";
    public static final String MEDPACK = "medpack";
    public static final String ADRENALINE = "adrenaline";

    private PickupFactory() {}

    public static Pickup createPickup(GameScreen game, MapObject object, float x, float y) {
        String type = object.getProperties().get("type", String.class);
        String name = object.getName();

        // TODO Find a better way to do this
        PickupProto proto;
        if (type.equals(AMMO) || type.equals(CRATE)) {
            proto = PickupProto.getByName(type);
        } else {
            proto = PickupProto.getByName(name);
        }

        switch (type) {
            case AMMO:
                return new Ammo(proto, x, y, name, getIntegerProperty(object, "ammo"));
            case CRATE:
                return new Crate(proto, x, y, game, name, getIntegerProperty(object, "ammo"));
            case MEDPACK:
                return new Medpack(proto, x, y);
            case ADRENALINE:
                return new Adrenaline(proto, x, y);
            default:
                throw new IllegalArgumentException("No such Pickup type: " + type);
        }
    }

    public static Pickup createRandomPickup(GameScreen game, float x, float y) {
        PickupProto proto = PickupProto.getRandom();

        // TODO Support all pickup names
        switch (proto.name) {
            case AMMO: return createRandomAmmo(x, y);
            case CRATE: return createRandomCrate(game, x, y);
            case MEDPACK:
                return new Medpack(proto, x, y);
            case ADRENALINE:
                return new Adrenaline(proto, x, y);
            default:
                // TODO throw exception instead
                return new Medpack(proto, x, y);
        }
    }

    public static Pickup createAmmoOrCrate(GameScreen game, float x, float y) {
        String name = MathUtils.random(1) == 0 ? AMMO : CRATE;

        switch (name) {
            case AMMO: return createRandomAmmo(x, y);
            case CRATE: return createRandomCrate(game, x, y);
        }
        return null;
    }

    public static Pickup createRandomAmmo(float x, float y) {
        WeaponProto weaponProto = WeaponProto.getRandom();
        int ammo =  MathUtils.random(1, weaponProto.magazineCapacity);
        return new Ammo(PickupProto.getByName("ammo"), x, y, weaponProto.name, ammo);
    }

    public static Pickup createRandomCrate(GameScreen game, float x, float y) {
        WeaponProto weaponProto = WeaponProto.getRandom();
        int ammo =  MathUtils.random(1, weaponProto.magazineCapacity);
        return new Crate(PickupProto.getByName("crate"), x, y, game, weaponProto.name, ammo);
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
