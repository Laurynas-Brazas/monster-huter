package com.mygdx.game.world;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Player.Player;
import com.mygdx.game.entities.Spider;

import java.util.ArrayList;

public class CustomGameMap extends GameMap {
    String id;
    String name;
    int[][][] map;
    Vector2 playerPosition;
    ArrayList<Vector2> spiderPostions;
    private static final float GRAVITY = -2160;
    private TextureRegion[][] tiles;
    private TextureRegion[] tiles2;
    public CustomGameMap(String levelName, OrthographicCamera camera) {
        GameMapData data = GameMapLoader.loadMap(levelName, levelName);
        this.map = data.map;
        this.playerPosition = data.playerPosition;
        this.spiderPostions = data.spiderPositions;
        for(Vector2 postion : spiderPostions) {
            addEntity(new Spider(postion.x, postion.y, this, 2));
        }
        addEntity(new Player(playerPosition.x, playerPosition.y, this, 5, camera));
        tiles = TextureRegion.split(new Texture("Tiles-and-Enemies.png"), TileType.TILE_SIZE / 8, TileType.TILE_SIZE / 8);
        tiles2 = new TextureRegion[tiles.length * tiles[0].length];
        for(int i = 0; i < tiles.length; i++)
            for(int j = 0; j < tiles[0].length; j++)
                    tiles2[i * tiles[0].length + j] = tiles[i][j];
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for(int layer = 0; layer < getLayers(); layer++) {
            for(int row = 0; row < getHeight(); row++) {
                for(int col = 0; col < getWidth(); col++) {
                    TileType type = this.getTileTypeByCoordinate(layer, col, row);
                    if (type != null) {
                        batch.draw(tiles2[type.getId() - 1], col * TileType.TILE_SIZE, row * TileType.TILE_SIZE, TileType.TILE_SIZE, TileType.TILE_SIZE);
                    }

                }
            }
        }

        super.render(camera, batch);
        batch.end();
        for(int i = 0; i < entities.size(); i++) {
            if(entities.get(i).getHealth() <= 0) {
                entities.remove(i);
                i--;
                continue;
            }
            entities.get(i).update(Gdx.graphics.getDeltaTime(), GRAVITY, 0);
            entities.get(i).render(batch, Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {}

    @Override
    public TileType getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) x / TileType.TILE_SIZE, getHeight() - (int) y / TileType.TILE_SIZE - 1);
    }

    @Override
    public TileType getTileTypeByCoordinate(int layer, int col, int row) {
        if(col < 0 || col >= getWidth() || row < 0 || row >= getHeight())
             return null;
        return TileType.getTileTypeById(map[layer][getHeight() - row - 1][col]);
    }

    @Override
    public int getWidth() {
        return map[0][0].length;
    }

    @Override
    public int getHeight() {
        return map[0].length;
    }
    @Override
    public int getLayers() {
        return map.length;
    }

    @Override
    public ArrayList<Entity> getEntities() {
        return entities;
    }
    @Override
    public int[][][] getMap() {
        return map;
    }

}
