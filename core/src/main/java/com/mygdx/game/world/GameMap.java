package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.entities.Entity;

import java.util.ArrayList;

public abstract class GameMap {
    private static final float epsilon = 1f;
    protected ArrayList<Entity> entities = new ArrayList<>();
    protected String mapName;
    public float minX = 100 * TileType.TILE_SIZE, minY = 100 * TileType.TILE_SIZE, maxX = 0, maxY = 0;
    public GameMap() {
        this.mapName = mapName;
    }
    public void render(OrthographicCamera cam, SpriteBatch batch) {

    }

    public void update(float delta) {

    }
    public abstract void dispose();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public TileType getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    public ArrayList<TileType> collidesWithTiles(float x, float y, int width, int height) {
        float temp = width;
        width*=0.625f;
        x = x + (temp - width) / 2;

        ArrayList<TileType> tiles = new ArrayList<>();
        for(int row = (int)y / TileType.TILE_SIZE; row < (y + height - epsilon) / TileType.TILE_SIZE; row++)
            for(int col = (int)x / TileType.TILE_SIZE; col < (x + width) / TileType.TILE_SIZE; col++)
                for(int layer = 0; layer < getLayers(); layer++) {
                    TileType type = this.getTileTypeByCoordinate(layer, col, row);
                    if (type != null)
                        tiles.add(type);
                }
        return tiles;
    }

    public abstract TileType getTileTypeByCoordinate(int layer, int col, int row);

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getLayers();
    public abstract ArrayList<Entity> getEntities();
    public abstract int[][][] getMap();
}
