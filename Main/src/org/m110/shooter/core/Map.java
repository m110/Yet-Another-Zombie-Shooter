package org.m110.shooter.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Map {
    private final MapType mapType;
    private final String name;
    private final String mapID;
    private final int maxLevel;

    private TiledMap map = null;

    public Map(MapType mapType, String name, String mapID, int maxLevel) {
        this.mapType = mapType;
        this.name = name;
        this.mapID = mapID;
        this.maxLevel = maxLevel;
    }

    public MapType getMapType() {
        return mapType;
    }

    public String getName() {
        return name;
    }

    public String getMapID() {
        return mapID;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public TiledMap getTiledMap(int level) {
        if (level > maxLevel) {
            return null;
        }

        this.map =TiledLoader.createMap(Gdx.files.internal(Config.LEVELS_DIR + mapID + "/level" + level + ".tmx"));
        return map;
    }

    public TileAtlas getAtlas() {
        return new TileAtlas(map, Gdx.files.internal(Config.LEVELS_DIR + mapID));
    }

}
