package com.mygdx.game.entities.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.EntityType;
import com.mygdx.game.world.GameMap;

import java.util.Objects;

public class Rock extends Entity {
    GameMap map;
    public boolean shouldBeDestroyed = false, canAttack = true;
    public Rock(float x, float y, GameMap map) {
        super(x, y, EntityType.ROCK, map, 0);
        this.map = map;
        addAnimation("default", "rock.png", 1, .1f);
        setCurrentAnimation("default");
        super.MIN_VELOCITY_Y = -1000000;
        super.MAX_VELOCITY_X =  1000000;
    }

    @Override
    public void update(float deltaTime, float accelerationY, float accelerationX) {

        super.update(deltaTime, accelerationY, accelerationX);
        if(collidedX || collidedY) {
            canAttack = false;
            setVelocity(0, 0);
        }
        Entity entity = collidedWithEntity();
        if(entity != null && canAttack) {
            if(attack(entity, 1))
                shouldBeDestroyed = true;
        }
    }

    public void setVelocity(float velocityX, float velocityY) {
        super.velocityX = velocityX;
        super.velocityY = velocityY;
    }

    private Entity collidedWithEntity() {
        for(Entity e : map.getEntities()) {
            float eps = 20;
            if(e.getX() + eps <= getX() + e.getWidth() && e.getX() + e.getWidth() - eps >= getX()
                && e.getY() + eps / 2 <= getY() + getHeight() && e.getY() + e.getHeight() - eps / 2 >= getY()
                && !Objects.equals(e.getId(), "player")) {
                return e;
            }
        }
        return null;
    }

}
