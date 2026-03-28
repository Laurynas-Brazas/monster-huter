package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Player.Player;
import com.mygdx.game.world.CustomGameMap;
import com.mygdx.game.world.GameMap;
import com.mygdx.game.world.TileType;

import java.util.ArrayList;
import java.util.Objects;

public class GameScreen implements Screen {
    private String levelName;
    private SpriteBatch batch;
    private OrthographicCamera camera, hudCamera;
    private GameMap gameMap;
    private final Main main;
    private Player player;
    private Stage stage;
    private Skin skin;
    private boolean escapeMenuVisible = false;
    private Texture fullHealth, spider = new Texture("spider.png");
    private float enemiesAtBeggining;
    private BitmapFont font;
    private int spidersKilled = 0;

    public GameScreen(Main main, String levelName) {
        this.levelName = levelName;
        this.main = main;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
        batch = new SpriteBatch();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameMap = new CustomGameMap(levelName, camera);
        fullHealth = new Texture(Gdx.files.internal("fullHealth.png"));
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        font = new BitmapFont(); // Initialize the font
        font.getData().setScale(2); // Make the font larger

        TextButton exitToLevelSelectionButton = new TextButton("Exit to Level Selection", skin);
        TextButton exitGameButton = new TextButton("Exit Game", skin);

        exitToLevelSelectionButton.setPosition((float) Gdx.graphics.getWidth() / 2 - exitToLevelSelectionButton.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2 + 20);
        exitGameButton.setPosition((float) Gdx.graphics.getWidth() / 2 - exitGameButton.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2 - 40);

        exitToLevelSelectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.inLevelSelectionScreen = true;
                main.setScreen(new LevelSelectionScreen(main));
            }
        });

        exitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(exitToLevelSelectionButton);
        stage.addActor(exitGameButton);

        stage.getRoot().setVisible(false);

        Gdx.input.setInputProcessor(stage);

        ArrayList<Entity> entities = gameMap.getEntities();
        enemiesAtBeggining = entities.size() - 1; // Subtract 1 to exclude the player
        for(Entity e : entities) {
            if(Objects.equals(e.getId(), "player"))
                player = (Player) e;
        }

        int[][][] map = gameMap.getMap();
        for(int z = 0; z < map.length; z++)
            for(int y = 0; y < map[0].length; y++)
                for(int x = 0; x < map[0][0].length; x++)
                    if(map[z][y][x] != 0) {
                        gameMap.minX = Math.min(gameMap.minX, x * TileType.TILE_SIZE);
                        gameMap.minY = Math.min(gameMap.minY, (99 - y) * TileType.TILE_SIZE);
                        gameMap.maxX = Math.max(gameMap.maxX, (x + 1) * TileType.TILE_SIZE);
                        gameMap.maxY = Math.max(gameMap.maxY, (99 - y) * TileType.TILE_SIZE);
                    }
    }

    @Override
    public void render(float delta) {
        float red = 27f / 255f;
        float green = 25f / 255f;
        float blue = 25f / 255f;
        ScreenUtils.clear(red, green, blue, 1);

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            escapeMenuVisible = !escapeMenuVisible;
            stage.getRoot().setVisible(escapeMenuVisible);
            Gdx.input.setInputProcessor(escapeMenuVisible ? stage : null);
        }
        if(escapeMenuVisible) {
            stage.act(delta);
            stage.draw();
            return;
        }

        camera.position.set(player.getX() + (float) player.getWidth() / 2, player.getY() + (float) player.getHeight() / 2, 0);
        camera.position.x = Math.min(gameMap.maxX - camera.viewportWidth / 2, camera.position.x);
        camera.position.x = Math.max(gameMap.minX + camera.viewportWidth / 2, camera.position.x);
        camera.position.y = Math.min(gameMap.maxY - camera.viewportHeight / 2, camera.position.y);
        camera.position.y = Math.max(gameMap.minY + camera.viewportHeight / 2, camera.position.y);
        camera.update();

        gameMap.update(delta);
        gameMap.render(camera, batch);

        // Update the number of spiders killed
        spidersKilled = (int) (enemiesAtBeggining - (gameMap.getEntities().size() - 1)); // Subtract 1 to exclude the player

        // Render the HUD
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        // Draw the health bar
        batch.setColor(Color.GRAY);
        batch.draw(fullHealth, 10, Gdx.graphics.getHeight() - fullHealth.getHeight() * 2 - 10, fullHealth.getWidth() * 2, fullHealth.getHeight() * 2);
        TextureRegion health = new TextureRegion(fullHealth, 0, 0, fullHealth.getWidth() * player.getHealth() / 5, fullHealth.getHeight());
        batch.setColor(Color.WHITE);
        batch.draw(health, 10, Gdx.graphics.getHeight() - fullHealth.getHeight() * 2 - 10, fullHealth.getWidth() * 2 * player.getHealth() / 5, fullHealth.getHeight() * 2);

        batch.draw(spider, 10, Gdx.graphics.getHeight() - fullHealth.getHeight() * 2 - 20 - 90, 100, 100);
        font.setColor(Color.WHITE);
        font.draw(batch, spidersKilled + " / " + (int) enemiesAtBeggining, 115, Gdx.graphics.getHeight() - fullHealth.getHeight() * 2 - 20 - 40);

        batch.end();

        // Check for game over or win conditions
        if(player.getHealth() <= 0)
            main.setScreen(new GameScreen(main, levelName));
        if(gameMap.getEntities().size() == 1 && gameMap.getEntities().getFirst() instanceof Player) {
            main.inWinScren = true;
            main.setScreen(new WinScreen(main));
        }
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
        batch.dispose();
        gameMap.dispose();
        stage.dispose();
        skin.dispose();
        font.dispose();
    }
}
