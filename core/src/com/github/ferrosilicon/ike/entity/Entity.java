package com.github.ferrosilicon.ike.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    public Vector2 dimension;

    public Entity(Vector2 dimensions) {
        dimension = dimensions;
    }

    /*
    Returns the appropriate sprite to render
    */
    public abstract TextureRegion getCurrentSprite(float deltaTime);

    /*
    Updates the motion state of the entity according to various properties ... we shall see wether to implement
    */
    //public abstract void updateMotionState(Vector2 pos, Vector2 vel, Vector2 accel);

    public enum DirectionState {
        LEFT, RIGHT
    }
}
