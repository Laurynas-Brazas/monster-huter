/** @author Laurynas Bražas 3 grupė*/
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
   public boolean inLevelSelectionScreen = true, inlevelCreationScreen = false, inWinScren = false;
   private LevelSelectionScreen levelSelection;
   private LevelCreationScreen levelCreation;
   private GameScreen gameScreen;
   private WinScreen winScreen;

   public void create () {
       levelSelection = new LevelSelectionScreen(this);
       setScreen(levelSelection);
   }

    public void setScreen(Screen screen) {
        if (screen instanceof LevelSelectionScreen) {
            if(levelSelection == null)
                levelSelection = new LevelSelectionScreen(this);
            levelSelection.show();
        }
        else if (screen instanceof GameScreen) {
            if(gameScreen != null)
                gameScreen.dispose();
            gameScreen = (GameScreen) screen;
            gameScreen.show();
        }
        else if (screen instanceof LevelCreationScreen) {
            if(levelCreation != null)
                levelCreation.dispose();
            levelCreation = (LevelCreationScreen) screen;
            levelCreation.show();
        }
        else if (screen instanceof WinScreen) {
            if(winScreen != null)
                winScreen.dispose();
            winScreen = (WinScreen) screen;
            winScreen.show();
        }
    }

   @Override
    public void render() {
       ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
       if(inLevelSelectionScreen) {
           levelSelection.render(Gdx.graphics.getDeltaTime());
       }
       else if(inlevelCreationScreen) {
           levelCreation.render(Gdx.graphics.getDeltaTime());
       }
       else if(inWinScren) {
           winScreen.render(Gdx.graphics.getDeltaTime());
       }
       else {
           gameScreen.render(Gdx.graphics.getDeltaTime());
       }
   }
}
