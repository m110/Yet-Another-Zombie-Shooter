package org.m110.shooter.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.m110.shooter.Shooter;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class MenuItem {

    public static final float ITEM_WIDTH = Gdx.graphics.getWidth() * 0.6f;
    public static final float ITEM_HEIGHT = 40.0f;

    private final String caption;
    private final float x;
    private final float y;
    private final MenuAction action;

    public MenuItem(String caption, MenuAction action, float x, float y) {
        this.caption = caption;
        this.x = x;
        this.y = y;
        this.action = action;
    }

    public String getCaption() {
        return caption;
    }

    public void draw(SpriteBatch batch, ShapeRenderer renderer, boolean active) {
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        BitmapFont font = Shooter.getInstance().getLargeFont();
        if (active) {
            renderer.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        } else {
            renderer.setColor(0.25f, 0.25f, 0.25f, 0.7f);
        }

        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);
        renderer.end();

        if (active) {
            font.setColor(0.25f, 0.25f, 0.25f, 0.7f);
        } else {
            font.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        }

        batch.begin();
        font.draw(batch, caption, x + ITEM_WIDTH - font.getBounds(caption).width - 5.0f, y + ITEM_HEIGHT + 5.0f);
        batch.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);
    }

    public void action() {
        action.action();
    }
}
