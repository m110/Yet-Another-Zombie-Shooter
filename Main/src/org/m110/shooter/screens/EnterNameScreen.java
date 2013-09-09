package org.m110.shooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Font;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class EnterNameScreen extends ShooterScreen {

    private final InputAdapter inputAdapter;
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;
    private final Stage stage;

    private final TextField.TextFieldStyle tfs;
    private final TextField textField;

    public EnterNameScreen(final Shooter shooter) {
        super(shooter);
        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    shooter.setPlayerName(textField.getText());
                    shooter.showMainMenu();
                    return true;
                }
                return false;
            }
        };
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        stage = new Stage();

        tfs = new TextField.TextFieldStyle();
        tfs.font = Font.large;
        tfs.fontColor = Color.WHITE;

        textField = new TextField("", tfs);
        textField.setWidth(500.0f);
        textField.setHeight(30.0f);
        textField.pack();
        textField.setX(Gdx.graphics.getWidth() / 2.0f - textField.getWidth() / 2.0f);
        textField.setY(Gdx.graphics.getHeight() / 2.0f);

        stage.addActor(textField);
        textField.pack();
        stage.setKeyboardFocus(textField);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        BitmapFont font = Font.large;
        font.setColor(Color.WHITE);

        batch.begin();
        font.draw(batch, "Enter your name", Gdx.graphics.getWidth() / 2.0f - font.getBounds("Enter your name").width / 2.0f,
                Gdx.graphics.getHeight() / 2.0f + font.getLineHeight() + 80.0f);
        batch.end();

        renderer.setColor(Color.WHITE);
        renderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        renderer.filledRect(textField.getX() - 2.0f, textField.getY() - 2.0f,
                textField.getWidth() + 4.0f, 2.0f);
        renderer.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        shooter.addInput(inputAdapter);
        shooter.addInput(stage);
    }

    @Override
    public void hide() {
        shooter.removeInput(stage);
        shooter.removeInput(inputAdapter);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
