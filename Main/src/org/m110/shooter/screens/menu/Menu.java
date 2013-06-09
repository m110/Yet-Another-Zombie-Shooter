package org.m110.shooter.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Font;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Menu extends InputAdapter {

    private final Array<MenuItem> items;
    private MenuItem activeItem;
    private float startX;
    private float startY;
    private float currentY;
    private int maxWidth = 0;

    // Sounds
    private static final Sound choiceSound;
    private static final Sound activeSound;

    // Menu style
    private Color color = Color.WHITE;
    private float spacing = 40.0f;

    private final ShapeRenderer renderer;

    static {
        choiceSound = Gdx.audio.newSound(Gdx.files.internal("audio/menu_choice.ogg"));
        activeSound = Gdx.audio.newSound(Gdx.files.internal("audio/menu_active.ogg"));
    }

    public Menu(float x, float y) {
        this.startX = x;
        this.startY = y;
        currentY = y;
        items = new Array<>();
        activeItem = null;
        this.renderer = new ShapeRenderer();
    }

    public void draw(SpriteBatch batch, float delta) {
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());
        for (MenuItem item : items) {
            boolean active = item == activeItem;
            item.draw(batch, renderer, active);
        }
    }

    public void alignToCenter() {
        float x = Gdx.graphics.getWidth() / 2.0f;
        float y = Gdx.graphics.getHeight() / 2.0f;

        float height = (items.size * Font.large.getSpaceWidth()) + (items.size * spacing);

        startX = x - (maxWidth * Font.large.getSpaceWidth()) / 2.0f + 40.0f;
        startY = y + height / 2.0f;
    }

    public void addMenuItem(String caption, MenuAction action) {
        MenuItem menuItem = new MenuItem(caption, action, startX, currentY);
        currentY -= spacing;

        items.add(menuItem);
        if (activeItem == null) {
            activeItem = menuItem;
        }

        int length = menuItem.getCaption().length();
        if (length > maxWidth) {
            maxWidth = length;
        }
    }

    public void itemUp() {
        if (items.size > 1) {
            choiceSound.play();
            int index = items.indexOf(activeItem, true);
            if (index == 0) {
                activeItem = items.get(items.size - 1);
            } else {
                activeItem = items.get(index - 1);
            }
        }
    }

    public void itemDown() {
        if (items.size > 1) {
            choiceSound.play();
            int index = items.indexOf(activeItem, true);
            if (index == items.size - 1) {
                activeItem = items.get(0);
            } else {
                activeItem = items.get(index + 1);
            }
        }
    }

    public void action() {
        activeSound.play();
        if (activeItem != null) {
            activeItem.action();
        }
    }

    public void reset() {
        activeItem = items.get(0);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                itemUp();
                break;
            case Input.Keys.DOWN:
                itemDown();
                break;
            case Input.Keys.ENTER:
                action();
                break;
        }

        return true;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }
}
