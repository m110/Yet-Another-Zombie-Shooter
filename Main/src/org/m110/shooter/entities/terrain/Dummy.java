package org.m110.shooter.entities.terrain;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Dummy {
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public Dummy(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Dummy(float x, float y) {
        this(x, y, 0.0f, 0.0f);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
