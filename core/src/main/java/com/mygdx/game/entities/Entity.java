package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.world.GameMap;
import com.mygdx.game.world.TileType;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Entity {
    protected Vector2 position;
    protected EntityType type;
    protected float velocityY, velocityX;
    protected GameMap map;
    protected boolean isGrounded = false;
    protected int direction = 1;
    protected boolean collidedX = false, collidedY = false, attacked = false;
    protected float angle = 0, invincibleFor = 0, cantMoveFor = 0;

    protected float MIN_VELOCITY_Y, MAX_VELOCITY_X;

    private int health;
    private final ObjectMap<String, Animation<TextureRegion>> animations;
    private String currentAnimation;
    private float stateTime = 0, timeSinceColorChange = 0;
    private boolean isFlashing = false, red = false;

    public Entity(float x, float y, EntityType type, GameMap map, int health) {
        this.position = new Vector2(x, y);
        this.health = health;
        this.type = type;
        this.map = map;
        this.animations = new ObjectMap<>();
    }

    public void addAnimation(String name, String spriteSheetPath, int framesCount, float frameDuration) {
        Animation<TextureRegion> animation = createAnimation(spriteSheetPath, framesCount, frameDuration);
        animations.put(name, animation);
    }

    public void setCurrentAnimation(String name) {
        if (animations.containsKey(name)) {
            this.currentAnimation = name;
        }
    }

    public void setStateTime(float time) {
        this.stateTime = time;
    }

    public int getCurrentAnimationFrameIndex() {
        return animations.get(currentAnimation).getKeyFrameIndex(stateTime);
    }

    public void update(float deltaTime, float gravity, float accelerationX) {
        if(map.minY > position.y) {
            health = 0;
        }
        move(deltaTime, gravity, accelerationX);
    }

    public void render(SpriteBatch batch, float deltaTime) {
        stateTime += deltaTime;
        invincibleFor -= deltaTime;
        timeSinceColorChange += deltaTime;
        Animation<TextureRegion> currentAnimation = animations.get(this.currentAnimation);
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        if (direction == -1) {
            currentFrame.flip(true, false);
        }
        if(invincibleFor > 0) {
            isFlashing = true;
            if(timeSinceColorChange >= .35f) {
                timeSinceColorChange = 0;
                red = !red;
            }
        }
        else if(isFlashing) {
            isFlashing = false;
            red = false;
        }
        if(red)
            batch.setColor(Color.RED);
        else
            batch.setColor(Color.WHITE);
        batch.draw(currentFrame,
                position.x, position.y,
                this.getWidth() / 2f, this.getHeight() / 2f,
                this.getWidth(), this.getHeight(),
                1, 1,
                angle);
        batch.setColor(Color.WHITE);
        if (direction == -1) {
            currentFrame.flip(true, false);
        }
        batch.end();
    }

    private Animation<TextureRegion> createAnimation(String spriteSheetPath, int framesCount, float frameDuration) {
        Texture spriteSheet = new Texture(spriteSheetPath);
        TextureRegion[][] tempFrames = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / framesCount, spriteSheet.getHeight());

        Array<TextureRegion> frames = new Array<>();
        for (int j = 0; j < framesCount; j++) {
            frames.add(tempFrames[0][j]);
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }


    void move(float deltaTime, float gravity, float accelerationX) {
        collidedX = false;
        collidedY = false;
        velocityY += deltaTime * gravity;
        velocityX += accelerationX * deltaTime;
        velocityY = Math.max(velocityY, MIN_VELOCITY_Y);
        velocityX = Math.min(velocityX, MAX_VELOCITY_X);
        velocityX = Math.max(velocityX, -MAX_VELOCITY_X);
        cantMoveFor -= deltaTime;

        float newY = position.y + velocityY * deltaTime;
        float newX = position.x + velocityX * deltaTime;
        ArrayList<TileType> tiles = map.collidesWithTiles(newX, position.y, getWidth(), getHeight());
        boolean change = true;
        for(TileType tile : tiles) {
            if(tile.isCollidable())
                change = false;

        }
        if(change)
            position.x = newX;
        else
            collidedX = true;
        tiles = map.collidesWithTiles(position.x, newY, getWidth(), getHeight());
        change = true;
        for(TileType tile : tiles) {
            if(tile.isCollidable())
                change = false;
        }
        if(change) {
            position.y = newY;
            isGrounded = false;
        }
        else if(velocityY <= 0) {
            collidedY = true;
            velocityY = -40;
            position.y = (int)position.y;
            isGrounded = true;
        }
        else if(velocityY > 0) {
            velocityY = 0;
        }
        else
            collidedY = true;

    }

    public boolean attack(Entity entity, int damage){
        float eps = 20;
        if(entity.getX() + eps <= getX() + entity.getWidth() && entity.getX() + entity.getWidth() - eps >= getX()
            && entity.getY() + eps / 2 <= getY() + getHeight() && entity.getY() + entity.getHeight() - eps / 2 >= getY()
            && entity.invincibleFor <= 0){
            entity.velocityY = (float) 500;
            float knockBackX = 500;
            entity.velocityX = knockBackX * (entity.getX() > getX() ? 1 : -1);
            entity.health--;
            float disableMovementFor = .5f;
            if(!Objects.equals(entity.getId(), "player"))
                entity.cantMoveFor = disableMovementFor;
            else
                entity.invincibleFor = 1.7f;

            return true;
        }
        return false;
    }


    public Vector2 getPosition() {
        return position;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }


    public EntityType getType() {
        return type;
    }

    public int getWidth() {
        return type.getWidth();
    }

    public int getHeight() {
        return type.getHeight();
    }
    public String getId() {
        return type.getId();
    }

    public float getWeight() {
        return type.getWeight();
    }
    public float getAcceleration() {
        return velocityX;
    }
    public int getHealth() {
        return health;
    }

}

