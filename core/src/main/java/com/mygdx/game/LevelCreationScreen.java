package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.world.GameMapData;
import com.mygdx.game.world.GameMapLoader;
import com.mygdx.game.world.TileType;

import java.awt.Rectangle;
import java.util.ArrayList;

import static com.mygdx.game.world.TileType.TILE_SIZE;

public class LevelCreationScreen implements Screen {
    private final Stage stage;
    private final OrthographicCamera camera, hudCamera;
    private final SpriteBatch batch;
    private TextureRegion[][] tiles;
    private TextureRegion[] tileList;
    private final ShapeRenderer shapeRenderer;
    private int selectedTileIndex = -1;
    private int[][][] map;
    private final Rectangle tileSelectionArea, tilePlacementArea, playerSelectionArea, spiderSeletionArea, emptySelectionArea;
    private final int rowsInSelection = 13, colsInSelection = 13, tilesInRow = 26;
    private final int mapHeight = 100, mapWidth = 100;
    private static final Json json = new Json();
    private final TextField mapNameTextField;
    private final Texture playerTexture, spiderTexture;
    private boolean playerSelected = false, spiderSelected = false;
    private Vector2 playerPosition = new Vector2(-1, -1);
    private ArrayList<Vector2> spiderPositions = new ArrayList<>();

    public LevelCreationScreen(Main main, String levelName, boolean editMode) {
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        this.playerTexture = new Texture(Gdx.files.internal("player.png"));
        this.spiderTexture = new Texture(Gdx.files.internal("spider.png"));

        int selectionWidth = colsInSelection * TILE_SIZE / 2;
        int selectionHeight = rowsInSelection * TILE_SIZE / 2;
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));


        mapNameTextField = new TextField(levelName, skin);
        mapNameTextField.setPosition(Gdx.graphics.getWidth() - 220, Gdx.graphics.getHeight() - 130);
        mapNameTextField.setSize(200, 30);

        TextButton exitButton = new TextButton("Exit to level selection", skin);
        exitButton.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 50);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.inLevelSelectionScreen = true;
                main.inlevelCreationScreen = false;
                main.setScreen(new LevelSelectionScreen(main));
            }
        });

        TextButton saveButton = new TextButton("Save Map", skin);
        saveButton.setPosition(Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 90);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(playerPosition.x != -1) {
                    saveMap();
                    main.inlevelCreationScreen = false;
                    main.inLevelSelectionScreen = true;
                    main.setScreen(new LevelSelectionScreen(main));
                }
            }
        });

        stage.addActor(mapNameTextField);
        stage.addActor(saveButton);
        stage.addActor(exitButton);
        Gdx.input.setInputProcessor(stage);

        map = new int[1][mapHeight][mapWidth];
        if(editMode) {
            GameMapData data = GameMapLoader.loadMap(levelName, levelName);
            if(data != null) {
                for(int i = 0 ; i < mapHeight; i++)
                    map[0][i] = data.map[0][mapHeight - i - 1];
                this.playerPosition = data.playerPosition;
                this.spiderPositions = data.spiderPositions;
            }
        }
        tileSelectionArea = new Rectangle(
            Gdx.graphics.getWidth() - selectionWidth - 10,
            10,
            selectionWidth,
            selectionHeight
        );
        playerSelectionArea = new Rectangle(
            tileSelectionArea.x - 220,
            tileSelectionArea.y + selectionHeight / 2 - 100,
            200,
            200
        );
        emptySelectionArea = new Rectangle(
            tileSelectionArea.x + tileSelectionArea.width - TILE_SIZE / 2,
            tileSelectionArea.y + tileSelectionArea.height,
            TILE_SIZE / 2,
            TILE_SIZE / 2
        );

        spiderSeletionArea = new Rectangle(
            playerSelectionArea.x - 250,
            tileSelectionArea.y + selectionHeight / 2 - 100,
            200,
            200
        );

        tilePlacementArea = new Rectangle(
            0,
            selectionHeight + 20,
            Gdx.graphics.getWidth() - selectionWidth - 20,
            Gdx.graphics.getHeight() - selectionHeight - 20
        );

        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    int screenX = (int) event.getStageX();
                    int screenY = (int) event.getStageY();
                    if(playerSelectionArea.contains(screenX, screenY)) {
                        selectedTileIndex = -1;
                        playerSelected = true;
                        spiderSelected = false;
                    }
                    if(spiderSeletionArea.contains(screenX, screenY)) {
                        selectedTileIndex = -1;
                        spiderSelected = true;
                        playerSelected = false;
                    }
                    if(tileSelectionArea.contains(screenX, screenY)) {
                        int row = rowsInSelection - (screenY - tileSelectionArea.y) / (TILE_SIZE / 2) - 1;
                        int col = (screenX - tileSelectionArea.x) / (TILE_SIZE / 2);
                        playerSelected = false;
                        spiderSelected = false;
                        selectedTileIndex = row * tilesInRow + col;
                    }
                    if(emptySelectionArea.contains(screenX, screenY)) {
                        playerSelected = false;
                        spiderSelected = false;
                        selectedTileIndex = -1;
                    }

                }
                return true;
            }
        });
    }

    @Override
    public void show() {
        tiles = TextureRegion.split(new Texture("Tiles-and-Enemies.png"), TileType.TILE_SIZE / 8, TileType.TILE_SIZE / 8);
        tileList = new TextureRegion[tiles.length * tiles[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tileList[i * tiles[0].length + j] = tiles[i][j];
            }
        }
    }

    @Override
    public void render(float delta) {
        float red = 27f / 255f;
        float green = 25f / 255f;
        float blue = 25f / 255f;
        ScreenUtils.clear(red, green, blue, 1);

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            camera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
            camera.position.x = Math.max(camera.viewportWidth / 2, camera.position.x);
            camera.position.y = Math.max(camera.viewportHeight / 2, camera.position.y);
            camera.update();
        }
        shapeRenderer.setProjectionMatrix(camera.combined);
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        int invertedY = Gdx.graphics.getHeight() - mouseY;
        int hoverMapX = -1;
        int hoverMapY = -1;
        if (tilePlacementArea.contains(mouseX, invertedY)) {
            Vector3 worldCoordinates = new Vector3(mouseX, mouseY, 0);
            camera.unproject(worldCoordinates);
            hoverMapX = (int) (worldCoordinates.x / TILE_SIZE);
            hoverMapY = (int) ((worldCoordinates.y - tilePlacementArea.y) / TILE_SIZE);
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)  && hoverMapX >= 0 && hoverMapY >= 0 && hoverMapX < mapWidth && hoverMapY < mapHeight) {

            if(selectedTileIndex == -1 && !playerSelected && !spiderSelected) {
                for(int i = 0; i < spiderPositions.size(); i++) {
                    if(hoverMapX * TILE_SIZE == spiderPositions.get(i).x && hoverMapY * TILE_SIZE == spiderPositions.get(i).y) {
                        spiderPositions.remove(i);
                        i--;
                    }
                }
            }

            if(playerSelected) {
                playerPosition.x = hoverMapX * TILE_SIZE;
                playerPosition.y = hoverMapY * TILE_SIZE;
            }
            else if(spiderSelected) {
                Vector2 spiderPosition = new Vector2(hoverMapX * TILE_SIZE, hoverMapY * TILE_SIZE);
                if(!spiderPositions.contains(spiderPosition))
                    spiderPositions.add(spiderPosition);
            }
            else
                map[0][hoverMapY][hoverMapX] = selectedTileIndex + 1;

        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int i = 0; i < mapHeight; i++)
            for (int j = 0; j < mapWidth; j++)
                if (map[0][i][j] != 0 && (j != hoverMapX || i != hoverMapY))
                    batch.draw(tileList[map[0][i][j] - 1], j * TILE_SIZE, i * TILE_SIZE + tilePlacementArea.y, TILE_SIZE, TILE_SIZE);
        if(playerPosition.x != -1)
            batch.draw(playerTexture, playerPosition.x, playerPosition.y + tilePlacementArea.y, 128, 128);

        for(Vector2 position : spiderPositions) {
            batch.draw(spiderTexture, position.x, position.y + tilePlacementArea.y, 128, 128);

        }

        batch.end();
        if (hoverMapX >= 0 && hoverMapY >= 0 && hoverMapX < mapWidth && hoverMapY < mapHeight) {
            batch.begin();
            batch.setColor(1, 1, 1, 0.5f);
            if(playerSelected)
                batch.draw(playerTexture, hoverMapX * TILE_SIZE, hoverMapY * TILE_SIZE + tilePlacementArea.y, 128, 128);
            if(spiderSelected)
                batch.draw(spiderTexture, hoverMapX * TILE_SIZE, hoverMapY * TILE_SIZE + tilePlacementArea.y, 128, 128);

            if(selectedTileIndex != -1)
                batch.draw(tileList[selectedTileIndex], hoverMapX * TILE_SIZE, hoverMapY * TILE_SIZE + tilePlacementArea.y, TILE_SIZE, TILE_SIZE);
            batch.setColor(1, 1, 1, 1);
            batch.end();

        }


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
        for (int x = 0; x <= mapWidth; x++) {
            shapeRenderer.line(x * TILE_SIZE, tilePlacementArea.y, x * TILE_SIZE, mapHeight * TILE_SIZE + tilePlacementArea.y);
        }
        for (int y = 0; y <= mapHeight; y++) {
            shapeRenderer.line(0, y * TILE_SIZE + tilePlacementArea.y, mapWidth * TILE_SIZE, y * TILE_SIZE + tilePlacementArea.y);
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), (float) (colsInSelection * TILE_SIZE) / 2 + 20);
        shapeRenderer.rect(Gdx.graphics.getWidth() - (float) (rowsInSelection * TILE_SIZE) / 2 - 20, 0,
            (float) (rowsInSelection * TILE_SIZE) / 2 + 20, Gdx.graphics.getHeight());
        shapeRenderer.setColor(1, 0, 0, 1);

        float centerX = emptySelectionArea.x + (float) emptySelectionArea.width / 2;
        float centerY = emptySelectionArea.y + (float) emptySelectionArea.height / 2;

        float armLengthX = (float) emptySelectionArea.width / 2;
        float armLengthY = (float) emptySelectionArea.height / 2;

        float lineThickness = 5;

        shapeRenderer.rectLine(
            centerX - armLengthX, centerY - armLengthY,
            centerX + armLengthX, centerY + armLengthY,
            lineThickness
        );

        shapeRenderer.rectLine(
            centerX - armLengthX, centerY + armLengthY,
            centerX + armLengthX, centerY - armLengthY,
            lineThickness
        );
        shapeRenderer.end();

        batch.begin();
        if(playerSelected)
            batch.setColor(0.6f, 0.8f, 0.9f, 0.3f);
        batch.draw(playerTexture, playerSelectionArea.x, playerSelectionArea.y, 200, 200);
        if(playerSelected)
            batch.setColor(1, 1, 1, 1);

        if(spiderSelected)
            batch.setColor(0.6f, 0.8f, 0.9f, 0.3f);
        batch.draw(spiderTexture, spiderSeletionArea.x, spiderSeletionArea.y, 200, 200);
        if(spiderSelected)
            batch.setColor(1, 1, 1, 1);


        for (int i = 0; i < rowsInSelection; i++) {
            for (int j = 0; j < colsInSelection; j++) {
                if (i * tilesInRow + j == selectedTileIndex) {
                    batch.setColor(0.6f, 0.8f, 0.9f, 0.3f);
                }
                batch.draw(tileList[i * tilesInRow + j], tileSelectionArea.x + (float) (j * TILE_SIZE) / 2, tileSelectionArea.y + (float) ((rowsInSelection - i - 1) * TILE_SIZE) / 2, (float) TILE_SIZE / 2, (float) TILE_SIZE / 2);
                if (i * tilesInRow + j == selectedTileIndex) {
                    batch.setColor(1, 1, 1, 1);
                }
            }
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        for (int i = 0; i <= rowsInSelection; i++) {
            float lineX = tileSelectionArea.x + (float) (i * TILE_SIZE) / 2;
            shapeRenderer.line(lineX, tileSelectionArea.y, lineX, tileSelectionArea.y + (float) (colsInSelection * TILE_SIZE) / 2);
        }
        for (int i = 0; i <= colsInSelection; i++) {
            float lineY = tileSelectionArea.y + (float) (i * TILE_SIZE) / 2;
            shapeRenderer.line(tileSelectionArea.x, lineY, tileSelectionArea.x + (float) (rowsInSelection * TILE_SIZE) / 2, lineY);
        }
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    private void saveMap() {
        GameMapData data = new GameMapData();
        int [][][] saveMap = new int[1][mapHeight][mapWidth];
        for(int i = 0; i < mapHeight; i++)
            saveMap[0][i] = map[0][mapHeight - 1 - i];
        data.map = saveMap;
        data.playerPosition = playerPosition;
        data.spiderPositions = spiderPositions;
        String name = mapNameTextField.getText();
        Gdx.files.local("maps/").file().mkdirs();
        FileHandle file = Gdx.files.local("maps/" + name + ".json");
        file.writeString(json.prettyPrint(data), false);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    public void dispose() {}

}
