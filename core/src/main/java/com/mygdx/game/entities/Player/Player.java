package com.mygdx.game.entities.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.EntityType;
import com.mygdx.game.entities.Spider;
import com.mygdx.game.world.GameMap;
import com.mygdx.game.world.TileType;

import java.util.ArrayList;
import java.util.Objects;

public class Player extends Entity {
    private static final int JUMP_VELOCITY = 960;
    private static final int SPEED = 800;
    private boolean running = false;
    private boolean holdingSpace = false;
    private float timeSinceShot = 0;
    private final float rockGravity = -1550;
    private boolean isShooting = false;
    private final ShapeRenderer shapeRenderer;
    private final ArrayList<Rock> rocks = new ArrayList<>();
    private float angle;
    private final OrthographicCamera camera;
    private final Rock rock;

    public Player(float x, float y, GameMap map, int health, OrthographicCamera camera) {
        super(x, y, EntityType.PLAYER, map, health);
        this.camera = camera;
        rock = new Rock(0,0,map);
        shapeRenderer = new ShapeRenderer();
        addAnimation("idle", "playerIdle.png", 4, .18f);
        addAnimation("run", "playerRun.png", 4, .1f);
        addAnimation("jump", "playerJump.png", 1, 20);
        addAnimation("stayShoot", "playerStayAttack.png", 4, .2f);
        addAnimation("jumpShoot", "playerJumpAttack.png", 4, .2f);
        super.MIN_VELOCITY_Y = -1200;
        super.MAX_VELOCITY_X = 500;
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        super.render(batch, deltaTime);
        for(Rock rock : rocks) {
            rock.render(batch, deltaTime);
        }
    }

    @Override
    public void update(float deltaTime, float accelerationY, float accelerationX) {
        timeSinceShot += deltaTime;

        float rockSpeed = 1300;
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            angle = calculateAngle();
            predictAttackTraj(rockSpeed * MathUtils.cos(angle), rockSpeed * MathUtils.sin(angle));
        }

        float shotDelay = 1;
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceShot > shotDelay) {
            angle = calculateAngle();
            isShooting = true;
            setStateTime(0);
            if(isGrounded)
                setCurrentAnimation("stayShoot");
            else
                setCurrentAnimation("jumpShoot");
            timeSinceShot = 0;
        }

        if(isShooting && getCurrentAnimationFrameIndex() == 3) {
            isShooting = false;
            Rock rock = new Rock(position.x + (direction == 1 ? getWidth() : 0), position.y + (float)getHeight() / 2, map);
            rock.setVelocity(rockSpeed * MathUtils.cos(angle), rockSpeed * MathUtils.sin(angle));
            rocks.add(rock);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if(isGrounded) {
                this.velocityY = JUMP_VELOCITY;
                isGrounded = false;
                holdingSpace = true;
            }
        }
        else if(holdingSpace) {
            holdingSpace = false;
            if(velocityY > 0)
                velocityY *= 0.5f;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(isGrounded && !isShooting)
                setCurrentAnimation("run");
            if(this.velocityX > 0)
                this.velocityX = 0;
            accelerationX = -SPEED;
            if(!isShooting)
                this.direction = -1;
            running = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            if(isGrounded && !isShooting)
                setCurrentAnimation("run");
            if(this.velocityX < 0)
                this.velocityX = 0;
            accelerationX = SPEED;
            if(!isShooting)
                this.direction = 1;
            running = true;
        } else if(running || Math.abs(this.velocityX) >= 20) {
            if(this.velocityX < 0)
                accelerationX = SPEED * 3;
            else
                accelerationX = -SPEED * 3;
            if(Math.abs(this.velocityX) < 20) {
                accelerationX = 0;
                this.velocityX = 0;
                running = false;
            }
        }

        if(!isGrounded && !isShooting)
            setCurrentAnimation("jump");
        if(isGrounded && !running && !isShooting)
            setCurrentAnimation("idle");

        super.update(deltaTime, accelerationY, accelerationX);
        for(int i = 0; i < rocks.size(); i++) {
            if(rocks.get(i).shouldBeDestroyed) {
                rocks.remove(i);
                i--;
                continue;
            }
            rocks.get(i).update(deltaTime, rockGravity, 0);
        }
    }

    private float calculateAngle() {
        Vector3 mouseWorldCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorldCoords);

        float playerLaunchX = position.x + (direction == 1 ? getWidth() : 0);
        float playerLaunchY = position.y + (float)getHeight() / 2;

        float deltaX = mouseWorldCoords.x - playerLaunchX;
        float deltaY = mouseWorldCoords.y - playerLaunchY;

        float angle = MathUtils.atan2(deltaY, deltaX);

        if(direction == 1 && deltaX < 0)
            angle = MathUtils.PI - angle;
        if(direction == -1 && deltaX > 0)
            angle = -MathUtils.PI - angle;

        return angle;
    }

    private void predictAttackTraj(float V_X0, float V_Y0) {
        ArrayList<Vector2> positions = new ArrayList<>();
        Vector2 hitPoint = null;
        float time = 0;
        int counter = 0;

        float PRED_TIME = 5;
        while(time < PRED_TIME) {
            counter++;
            float x = position.x + (direction == 1 ? getWidth() : 0) + V_X0 * time;
            float y = position.y + (float)getHeight() / 2 + V_Y0 * time + 0.5f * rockGravity * time * time;

            boolean hitWall = false;
            for(int i = 0; i < map.getLayers(); i++) {
                TileType tile = map.getTileTypeByCoordinate(i, (int)(x + rock.getWidth()) / TileType.TILE_SIZE, (int)y / TileType.TILE_SIZE);
                if(tile != null && tile.isCollidable())
                    hitWall = true;
                tile = map.getTileTypeByCoordinate(i, (int)x / TileType.TILE_SIZE, (int)(y + rock.getHeight()) / TileType.TILE_SIZE);
                if(tile != null && tile.isCollidable())
                    hitWall = true;
            }

            if(hitWall || hitEnemie(x, y)) {
                hitPoint = new Vector2(x, y);
                break;
            }

            int pointDensity = 100;
            if(counter % pointDensity == 0)
                positions.add(new Vector2(x, y));
            float INTERVALS = .001f;
            time += INTERVALS;
        }

        renderTraj(positions, hitPoint);
    }

    private void renderTraj(ArrayList<Vector2> positions, Vector2 hitPoint) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 0, 0.5f);

        for(Vector2 point : positions) {
            shapeRenderer.circle(point.x, point.y, 5);
        }

        if(hitPoint != null) {
            shapeRenderer.setColor(1, 0, 0, 1);
            float size = 10;
            float thickness = 3;

            shapeRenderer.rectLine(
                hitPoint.x - size, hitPoint.y + size,
                hitPoint.x + size, hitPoint.y - size,
                thickness
            );
            shapeRenderer.rectLine(
                hitPoint.x + size, hitPoint.y + size,
                hitPoint.x - size, hitPoint.y - size,
                thickness
            );
        }

        shapeRenderer.end();
    }

    private boolean hitEnemie(float x, float y) {
        ArrayList<Entity> entities = map.getEntities();
        for(Entity e : entities) {
            if(Objects.equals(e.getId(), "spider")) {
                if(e.getX() <= x && e.getX() + e.getWidth() >= x && e.getY() <= y && e.getY() + e.getHeight() - (float) e.getHeight() / 3 >= y) {
                    return true;
                }
            }
            if(Objects.equals(e.getId(), "spider")) {
                Spider spider = (Spider) e;
                if(spider.isCrawlingOnWeb()) {
                    Vector2 p1 = spider.webFrom;
                    Vector2 p2 = spider.webTo;
                    if(x > Math.max(p1.x, p2.x) || x < Math.min(p1.x, p2.x))
                        continue;
                    float A = p2.y - p1.y;
                    float B = p1.x - p2.x;
                    float C = p2.x * p1.y - p1.x * p2.y;
                    if(Math.abs(A * x + B * y + C) / Math.sqrt(A * A + B * B) < 1)
                        return true;
                }
            }
        }
        return false;
    }
}
