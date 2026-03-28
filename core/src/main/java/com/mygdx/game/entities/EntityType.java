package com.mygdx.game.entities;

public enum EntityType {
    PLAYER("player", 128, 128, 40),
    ROCK("rock", 16, 16, 2),
    SPIDER("spider", 128, 128, 40);
    private String id;
    private int width, height, health = 10;
    private float weight;
    private float velocityX, velocityY;
    private float acceleration;

    EntityType(String id, int width, int height, float weight) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.weight = weight;

    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public float getAcceleration() {
        return acceleration;
    }
    public void setHealth(int health) {
        this.health = health;
    }
}
