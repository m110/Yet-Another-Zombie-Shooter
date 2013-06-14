package org.m110.shooter.core;

import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.Player;
import org.m110.shooter.entities.bullets.Bullet;
import org.m110.shooter.entities.bullets.GooBullet;
import org.m110.shooter.entities.terrain.Dummy;
import org.m110.shooter.entities.terrain.Fence;
import org.m110.shooter.screens.GameScreen;

/**
 * @author m1_10sz <m110@m110.pl>
 */

public class Collision {

    private final GameScreen game;
    private final TiledLayer collisions;
    private Actor actor = null;
    private Entity entity = null;

    public Collision(GameScreen game, TiledLayer collisions) {
        this.game = game;
        this.collisions = collisions;
    }

    public void prepare() {
        actor = null;
        entity = null;
    }

    /**
     * Checks whether the object collides with terrain.
     * @return true if object collides with terrain, false otherwise.
     */
    public boolean collidesWithWall(float x, float y, Actor actor) {
        prepare();

        int[][] tiles = collisions.tiles;
        int h = tiles.length - 1;
        int x1 = (int)(x / game.getTileWidth());
        int y1 = h - (int)(y / game.getTileHeight());

        int x2 = (int)((x + actor.getWidth()) / game.getTileWidth());
        int y2 = h - (int)((y + actor.getHeight()) / game.getTileHeight());

        if (tiles[y1][x1] == 0 && tiles[y2][x2] == 0 &&
            tiles[y1][x2] == 0 && tiles[y2][x1] == 0) {

            if (actor instanceof Player || actor instanceof Bullet) {
                return false;
            } else {
                for (Fence fence : game.getFences()) {
                    if (check(actor, fence)) {
                        return true;
                    }
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean collidesWithEnemy(Bullet bullet) {
        prepare();

        if (bullet instanceof GooBullet) {
            return false;
        }

        float bx = bullet.getX();
        float by = bullet.getY();
        float bw = bullet.getWidth();
        float bh = bullet.getHeight();
        for (Entity enemy : game.getEntities()) {
            float ex = enemy.getX();
            float ey = enemy.getY();
            float ew = enemy.getWidth();
            float eh = enemy.getHeight();
            if (bx < ex+ew && bx+bw > ex &&
                by < ey+eh && by+bh > ey) {
                bullet.dealDamage(game.getPlayer(), enemy);
                entity = enemy;
                return true;
            }

        }
        return false;
    }

    public boolean collidesWithEnemy(float newX, float newY, Entity actor) {
        prepare();

        for (Entity enemy : game.getEntities()) {
            if (enemy == actor) {
                continue;
            }

            float ex = enemy.getX();
            float ey = enemy.getY();
            float ew = enemy.getWidth();
            float eh = enemy.getHeight();
            if (newX < ex+ew && newX+actor.getWidth() > ex &&
                newY < ey+eh && newY+actor.getHeight() > ey) {
                entity = enemy;
                return true;
            }
        }

        if (actor != game.getPlayer()) {
            return check(actor, game.getPlayer());
        }

        return false;
    }

    public boolean check(Actor a, Actor b) {
        prepare();

        boolean result = (a.getX() < b.getX()+b.getWidth() && a.getX()+a.getWidth() > b.getX() &&
                          a.getY() < b.getY()+b.getHeight() && a.getY()+a.getHeight() > b.getY());

        if (result) {
            actor = b;
        }

        return result;
    }

    public boolean check(Actor a, Dummy dummy) {
        prepare();

        return (a.getX() < dummy.getX()+dummy.getWidth() && a.getX()+a.getWidth() > dummy.getX() &&
                a.getY() < dummy.getY()+dummy.getHeight() && a.getY()+a.getHeight() > dummy.getY());
    }

    public Entity getEntity() {
        return entity;
    }

    public Actor getActor() {
        return actor;
    }

}
