package com.github.ferrosilicon.ike.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by KrithikR on 1/23/16.
 */
public abstract class Character extends Entity {
    CharacterTextureSet textureSet;
    public DirectionState directionState;
    public CharacterState characterState;
    ExtendedTexture currentSpriteSet;

    public Character(Vector2 dimension,CharacterTextureSet spriteSet,DirectionState dirState, CharacterState charState){
        super(dimension);
        textureSet = spriteSet;
        directionState = dirState;
        characterState = charState;
    }

    public enum CharacterState{
        STANDING,RUNNING,JUMPING,DYING //Maybe one for dead ???
    }

    public TextureRegion getCurrentSprite(float deltaTime){

        if(!currentSpriteSet.rendering){
            switch (characterState) {

                case DYING:
                    currentSpriteSet = textureSet.dyingTexture;
                    break;
                case JUMPING:
                    currentSpriteSet = textureSet.standingTexture;
                    break;
                case RUNNING:
                    currentSpriteSet = textureSet.walkingTexture;
                    break;
                case STANDING:
                    currentSpriteSet = textureSet.standingTexture;
                    break;

            }

        }
        return currentSpriteSet.render(deltaTime,directionState);
    }

    public void setCharacterState(CharacterState state) {
        if(!currentSpriteSet.rendering){
            characterState = state;

        }
    }
}
