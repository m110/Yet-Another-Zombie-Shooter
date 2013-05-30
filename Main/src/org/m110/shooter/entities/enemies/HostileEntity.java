package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.m110.shooter.entities.Entity;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class HostileEntity extends Entity {
    public HostileEntity(TextureRegion texture, String name, float startX, float startY) {
        super(texture, name, startX, startY);
    }
}
