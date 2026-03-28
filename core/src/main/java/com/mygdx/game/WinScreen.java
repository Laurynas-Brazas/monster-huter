package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WinScreen implements Screen {
    private final Main main;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private BitmapFont font;

    public WinScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);

        TextButton returnButton = new TextButton("Return to Level Selection", skin);
        returnButton.setPosition((float) Gdx.graphics.getWidth() / 2 - returnButton.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2 - 50);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.inWinScren = false;
                main.inLevelSelectionScreen = true;
                main.setScreen(new LevelSelectionScreen(main)); // Go back to level selection
            }
        });

        stage.addActor(returnButton);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.setColor(Color.GOLD);
        font.draw(batch, "LEVEL COMPLETED!", (float) Gdx.graphics.getWidth() / 2 - 140, (float) Gdx.graphics.getHeight() / 2 + 50);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        font.dispose();
    }
}
