package com.github.ferrosilicon.ike.entity;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class Ike extends Character {
    // Stores the Character Dimensions
    private final static Vector2 dimension = new Vector2(32, 32);

    public boolean grounded;
    public boolean movingLeft, movingRight;

    // Constructor for Ike :D
    public Ike(CharacterTextureSet textureSet) {
        super(dimension, textureSet, DirectionState.RIGHT, CharacterState.STANDING);
        currentSpriteSet = textureSet.standingTexture;
    }

    public TextureRegion getCurrentSprite(float deltaTime) {
        return super.getCurrentSprite(deltaTime);
    }

    @Override
    public CharacterState getCharacterState() {
        /*
        If you don't know what a ternary is or the binary bitwise operators this may look a bit
        confusing, but it's the same thing as:
        if (!grounded)
            return CharacterState.JUMPING;
        else if (movingLeft && !movingRight || !movingLeft && movingRight)
            return CharacterState.RUNNING;
        else
            return CharacterState.STANDING;
         */
        return !grounded ? CharacterState.JUMPING : movingLeft ^ movingRight
                ? CharacterState.RUNNING : CharacterState.STANDING;
    }
}
