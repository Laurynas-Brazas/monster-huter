package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Player.Player;
import com.mygdx.game.world.GameMap;
import com.mygdx.game.world.TileType;

import java.util.ArrayList;
import java.util.Objects;

public class Spider extends Entity {
    private boolean touchedGround = false;
    private boolean changedDirection = true;
    private boolean crawlOnWeb = false;
    private static final int SPEED = 250, WEB_SPEED = 150;
    private float timeUntilMove = 0, timeUntilTurn = 0;
    private final float MAX_PATROL_SPEED = 125;
    private final float CHASE_DISTACE = 7 * TileType.TILE_SIZE;
    private float sppeOnWebX, sppeOnWebY, maxWebLength = 1000;
    private Player player;
    public Vector2 webFrom, webTo;
    private final ShapeRenderer shapeRenderer;

    public Spider(float x, float y, GameMap map, int health) {
        super(x, y, EntityType.SPIDER, map, health);
        addAnimation("run", "spritesheet.png", 6, .1f);
        setCurrentAnimation("run");
        super.MIN_VELOCITY_Y = -1200;
        super.MAX_VELOCITY_X = MAX_PATROL_SPEED;
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        super.render(batch, deltaTime);

        if(crawlOnWeb) {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 1);
            drawWeb(shapeRenderer, webFrom.x, webFrom.y, webTo.x, webTo.y);
            shapeRenderer.end();
        }
    }

    @Override
    public void update(float deltaTime, float accelerationY, float accelerationX) {
        ArrayList<Entity> entities = map.getEntities();
        for(Entity e : entities) {
            if(Objects.equals(e.getId(), "player")) {
                player = (Player) e;
            }
        }

        attack(player, 1);
        timeUntilTurn -= deltaTime;
        timeUntilMove -= deltaTime;

        if(crawlOnWeb) {
            position.x += sppeOnWebX * WEB_SPEED * deltaTime;
            position.y += sppeOnWebY * WEB_SPEED * deltaTime;

            if(position.y >= webTo.y || (direction == 1 && position.x > webTo.x + 10) || (direction == -1 && position.x + TileType.TILE_SIZE < webTo.x - 10)) {
                crawlOnWeb = false;
                position.y = webTo.y;
                angle = 0;
            }
            return;
        }

        boolean isChasing = false;
        if(!touchedGround && isGrounded) {
            touchedGround = true;
        }
        if(getHealth() == 1)
            isChasing = true;
        float distanceToPlayer = (player.getX() - position.x) * (player.getX() - position.x) +
            (player.getY() - position.y) * (player.getY() - position.y);
        if(distanceToPlayer <= CHASE_DISTACE * CHASE_DISTACE) {
            isChasing = changedDirection = true;
        }

        if(timeUntilMove <= 0 && !isChasing) {
            if(!changedDirection && timeUntilTurn <= 0) {
                changedDirection = true;
                direction *= -1;
                timeUntilTurn = .2f;
            }
            if(touchedGround && timeUntilTurn <= 0) {
                patrol();
            }
            accelerationX = SPEED * direction;
        }

        if(isChasing) {
            if(!crawlOnWeb && player.isGrounded && (int) position.y / TileType.TILE_SIZE < (int) player.getY() / TileType.TILE_SIZE) {
                makeWeb();
            }
            super.MAX_VELOCITY_X = (float) 200;
            if(player.getX() > position.x) {
                if(direction == -1 && timeUntilTurn <= 0) {
                    velocityX = 0;
                    direction = 1;
                    timeUntilTurn = .2f;
                }
                accelerationX = SPEED * direction;
            }
            if(player.getX() < position.x) {
                if(direction == 1 && timeUntilTurn <= 0) {
                    velocityX = 0;
                    direction = -1;
                    timeUntilTurn = .2f;
                }
                accelerationX = SPEED * direction;
            }
        } else {
            super.MAX_VELOCITY_X = MAX_PATROL_SPEED;
        }

        if(timeUntilMove > 0) {
            accelerationX = 0;
        }

        super.update(deltaTime, accelerationY, accelerationX);
    }

    private void patrol() {
        int x = (int) Math.ceil((position.x + (double) getWidth() / 2)) / TileType.TILE_SIZE;
        int y = (int) position.y / TileType.TILE_SIZE;
        int positionX = findFirstDifferentTileX(x, y, direction) + (direction == -1 ? TileType.TILE_SIZE : 0);
        boolean changedDir = false;

        if(Math.abs(position.x + (direction == 1 ? getWidth() : 0) - positionX) < 10 || collidedX) {
            changedDir = true;
        }

        positionX = findFirstDifferentTileX(x, y - 1, direction) + (direction == -1 ? TileType.TILE_SIZE : 0);

        if(Math.abs(position.x + (direction == 1 ? getWidth() : 0) - positionX) < 30 && !changedDir) {
            changedDir = true;
        }

        if(changedDir) {
            velocityX = 0;
            changedDirection = false;
            timeUntilMove = .15f;
        }
    }

    private int findFirstDifferentTileX(int x, int y, int direction) {
        boolean searchingForCollidable = false;
        int counter = 0;

        for(int i = 0; i < map.getLayers(); i++) {
            if(!searchingForCollidable && map.getTileTypeByCoordinate(i, x, y) != null) {
                searchingForCollidable = map.getTileTypeByCoordinate(i, x, y).isCollidable();
            }
        }

        boolean found = false;
        while(!found && counter++ < 100) {
            boolean collidable = false;
            for(int i = 0; i < map.getLayers(); i++) {
                if(!collidable && map.getTileTypeByCoordinate(i, x, y) != null) {
                    collidable = map.getTileTypeByCoordinate(i, x, y).isCollidable();
                }
            }

            if(collidable != searchingForCollidable) {
                found = true;
            } else {
                x += direction;
            }
        }

        if(!found) {
            return -1000;
        }
        return x * TileType.TILE_SIZE;
    }

    private void makeWeb() {
        int playerX = (int) (player.getX() + (float) player.getWidth() / 2) / TileType.TILE_SIZE;
        int playerY = (int) player.getY() / TileType.TILE_SIZE - 1;
        int x = findFirstDifferentTileX(playerX, playerY, player.getX() > position.x ? -1 : 1);

        if(player.getX() > position.x && x > position.x) {
            crawlOnWeb = true;
            webFrom = new Vector2(position.x, position.y);
            webTo = new Vector2(x + TileType.TILE_SIZE, player.getY());
        }
        else if(player.getX() < position.x && x < position.x) {
            crawlOnWeb = true;
            webFrom = new Vector2(position.x, position.y);
            webTo = new Vector2(x, player.getY());
        }

        if(crawlOnWeb) {
            for(int i = 0; i < map.getLayers(); i++) {
                TileType tile = map.getTileTypeByCoordinate(i, (int) (webTo.x + (direction == -1 ? TileType.TILE_SIZE : getWidth())) / TileType.TILE_SIZE, Math.round(webTo.y / TileType.TILE_SIZE));
                if(tile != null && tile.isCollidable()) {
                    crawlOnWeb = false;
                    return;
                }
            }
            float dx = webTo.x - position.x;
            if(dx < 0) {
                dx -= TileType.TILE_SIZE;
            }
            float dy = webTo.y - position.y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);


            if(Math.abs(length) > maxWebLength) {
                crawlOnWeb = false;
                return;
            }

            angle = (float) Math.atan2(webTo.y - position.y, webTo.x - position.x) * MathUtils.radiansToDegrees;
            if(dx < 0) {
                angle += 180;
            }

            sppeOnWebX = dx / length;
            sppeOnWebY = dy / length;

            if(sppeOnWebX < 0 && direction == 1) {
                direction = -1;
            }
            if(sppeOnWebY > 0 && direction == -1) {
                direction = 1;
            }
        }
    }

    private void drawWeb(ShapeRenderer shapeRenderer, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float length = (float) Math.sqrt(dx * dx + dy * dy);
        float angle = (float) Math.atan2(dy, dx) * (180 / (float) Math.PI);

        shapeRenderer.identity();
        shapeRenderer.translate(x1, y1, 0);
        shapeRenderer.rotate(0, 0, 1, angle);
        shapeRenderer.rect(0, (float) -5 / 2, length, 5);
        shapeRenderer.identity();
    }

    public boolean isCrawlingOnWeb() {
        return crawlOnWeb;
    }
}
