package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class GameMapLoader {
    private static final Json json = new Json();

    public static GameMapData loadMap(String id, String name) {
        Gdx.files.internal("maps/").file().mkdirs();
        FileHandle file = Gdx.files.internal("maps/" + id + ".json");
        if (file.exists()) {
            return json.fromJson(GameMapData.class, file.readString());
        }
        else {
            Gdx.files.local("maps/").file().mkdirs();
            FileHandle file2 = Gdx.files.internal("maps/" + id + ".json");
            if (file2.exists()) {
                return json.fromJson(GameMapData.class, file.readString());
            }
        }
        return null;
    }
}
