package com.github.ferrosilicon.ike.entity;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class Ike extends Character{
    // Stores the Character Dimensions
    public final static Vector2 DIMENSION = new Vector2(32,32);
    // Stores the last position the Character Had
    public Vector2 lastPos = new Vector2(0,0);
    // Stores the Amount of Times A Wall is Hit. X is a Left Wall and Y is a Right Wall
    public Vector2 wallHitCount = new Vector2(0,0);
    // Stores wether to Skip Adding a Velocity Update ( for Wall Collision Fix )
    public boolean skipImpulse = false;

    // Constructor for Ike :D
    public Ike(CharacterTextureSet textureSet){
        super(DIMENSION,textureSet,DirectionState.RIGHT,CharacterState.STANDING);
        currentSpriteSet = textureSet.standingTexture;
    }

    public TextureRegion getCurrentSprite(float deltaTime){
        // Test Code
        //System.out.print(characterState);
        return super.getCurrentSprite(deltaTime);
    }


}
