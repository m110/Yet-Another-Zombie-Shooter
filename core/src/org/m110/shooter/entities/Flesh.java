package org.m110.shooter.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Flesh {

    private class Piece {

        private final TextureRegion texture;
        private final float x;
        private final float y;
        private final float rotation;

        public Piece(TextureRegion texture, float x, float y) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            rotation = MathUtils.random(0.0f, 360.0f);
        }

        public void draw(SpriteBatch batch) {
            batch.draw(texture, x, y, 0, 0, texture.getRegionWidth(), texture.getRegionHeight(), 1, 1, rotation);
        }
    }

    private Array<Piece> pieces;

    public Flesh(TextureRegion texture, float x, float y) {
        pieces = new Array<>();

        final float width = texture.getRegionWidth();
        final float height = texture.getRegionHeight();

        float stepH = height / 3.0f;
        float stepW = width / 3.0f;
        for (int i = 0; i < height; i += stepH) {
            for (int j = 0; j < width; j += stepW) {
                float newX = x + MathUtils.random(-width/2, width/2);
                float newY = y + MathUtils.random(-height/2, height/2);
                pieces.add(new Piece(new TextureRegion(texture, j, i, (int)stepW, (int)stepH), newX, newY));
                System.out.println("("+j+","+i+") " + stepW + ", " + stepH);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Piece piece : pieces) {
           piece.draw(batch);
        }
    }
}
