package com.github.ferrosilicon.ike.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by KrithikR on 1/23/16.
 */
public abstract class Entity {
    public Entity(Vector2 dimensions){
        dimension = dimensions;
    }
    public Vector2 dimension;
    /*
    Returns the appropriate sprite to render
    */
    public abstract TextureRegion getCurrentSprite(float deltaTime);

    /*
    Updates the motion state of the entity according to various properties ... we shall see wether to implement
    */
    //public abstract void updateMotionState(Vector2 pos, Vector2 vel, Vector2 accel);

    /*
    Returns the true position ( pixels ) relative to the screen size.
     */
    public Vector2 getPixelPosition(Vector2 entityPos, Camera camera , int mapTileSize){
        return new Vector2(
                ((entityPos.x - camera.position.x + camera.viewportWidth/2) * mapTileSize * 2) - dimension.x/2 ,        // X Coordinate Change ...
                ((entityPos.y - camera.position.y + camera.viewportHeight/2) * mapTileSize * 2) - dimension.y/2);    // Y Coordinate Change
    }
    public enum DirectionState{
        LEFT,RIGHT
    }
}
