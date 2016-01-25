package com.github.ferrosilicon.ike.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

// Abstract Character Class for Moving Living Breathing Entities
public abstract class Character extends Entity {
    // Texture Set for The Character
    CharacterTextureSet textureSet;
    // Direction the Character is Facing
    public DirectionState directionState;
    // State of the Character
    public CharacterState characterState;
    ExtendedTexture currentSpriteSet;

    // Constructor for a Character
    public Character(Vector2 dimension,CharacterTextureSet spriteSet,DirectionState dirState, CharacterState charState){
        super(dimension);
        textureSet = spriteSet;
        directionState = dirState;
        characterState = charState;
    }

    // Enum of Possible Character States
    public enum CharacterState{
        STANDING,RUNNING,JUMPING,DYING //Maybe one for dead ???
    }

    /*
    Returns the Current Sprite to Render Based on the Change in Time
    and based on the Character's Current Direction and State
     */
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

    /*
    Method to update the Character State ( in Hopes to Not Override Currently Rendering State )
     */
    public void setCharacterState(CharacterState state) {
        if(!currentSpriteSet.rendering){
            characterState = state;

        }
    }
}
