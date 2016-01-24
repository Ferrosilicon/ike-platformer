package com.github.ferrosilicon.ike.entity;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class Ike extends Character{
    private final static Vector2 dimension = new Vector2(32,32);
    public Ike(CharacterTextureSet textureSet){
        super(dimension,textureSet,DirectionState.RIGHT,CharacterState.STANDING);
        currentSpriteSet = textureSet.standingTexture;
    }

    public TextureRegion getCurrentSprite(float deltaTime){
        System.out.print(characterState);
        return super.getCurrentSprite(deltaTime);
    }


}