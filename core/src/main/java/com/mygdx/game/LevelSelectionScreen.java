package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class LevelSelectionScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final Main main;

    public LevelSelectionScreen(Main main) {
        this.stage = new Stage();
        this.main = main;

        skin = new Skin(Gdx.files.internal("uiskin.json"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.defaults().pad(10);

        TextButton createLevelButton = new TextButton("Create Level", skin);
        createLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.inLevelSelectionScreen = false;
                main.inlevelCreationScreen = true;
                main.setScreen(new LevelCreationScreen(main, "", false));
            }
        });

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exitButton).width(200);
        table.row();
        table.add(createLevelButton).width(200);
        table.row();

        ArrayList<String> levelNames = getLevels();
        for (String level : levelNames) {
            TextButton levelButton = new TextButton(level, skin);
            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    main.inLevelSelectionScreen = false;
                    main.setScreen(new GameScreen(main, level));
                }
            });

            TextButton editButton = new TextButton("Edit", skin);
            editButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    main.inLevelSelectionScreen = false;
                    main.inlevelCreationScreen = true;
                    main.setScreen(new LevelCreationScreen(main, level, true));
                }
            });

            table.add(levelButton).width(200).height(50);
            table.add(editButton).width(100).height(50);
            table.row();
        }

        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);
    }

    private ArrayList<String> getLevels() {
        FileHandle levelNameFile = Gdx.files.internal("levels.txt");
        String[] lines = levelNameFile.readString().split("\n");

        ArrayList<String> levels = new ArrayList<>();
        for (String line : lines) {
            levels.add(line.trim().replace(".json", ""));
        }
        FileHandle dir = Gdx.files.local("maps");
        FileHandle[] files = dir.list();
        for (FileHandle file : files) {
            levels.add(file.nameWithoutExtension());
        }
        return levels;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
    }
}
