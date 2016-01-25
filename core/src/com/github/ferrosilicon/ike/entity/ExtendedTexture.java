package com.github.ferrosilicon.ike.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by KrithikR on 1/23/16.
 */


public class ExtendedTexture {
    private final Texture texture;

    private final int frames; // Number of Frames in The Sprite Sheet
    private final Vector2 dimension; // Dimensions of Each Sprite in the Sprite Sheet
    private final float renderTime; // Time it takes to Render the Whole Sprite Sheet
    public boolean rendering = false; // Boolean to Represnet the Rendering Status of the Sprite Sheet
    private float renderProgress = 0.0f; // Float to Store the Time Change Since the Rendering Started

    /*
    Extended Texture initializer.
    Arguments include:
    Image Path , Number of Frames in the Sheet , the Dimensions of Each Sprite , and The Time to Render the Sheet
     */
    public ExtendedTexture(String imgPath,int numberOfFrames,Vector2 spriteDimension,float rendTime) {
        texture = new Texture(Gdx.files.internal(imgPath));
        frames = numberOfFrames;
        dimension = spriteDimension;
        renderTime = rendTime;
    }

    /*
    Outputs the Rendered Sprite based on The Time and Direction you Would Need the Sprite
     */
    public TextureRegion render(float deltaTime, Entity.DirectionState direction) {

        // Checks wether the SpriteSheet has more than One Frame to See if it can be Interrupted
        if (frames > 1)
            rendering = true;

        // Updates Progress of the Render
        renderProgress += deltaTime;

        // Gets the Current Frame based off the # of Frames / ( The Overall Time / Time Rendered So Far )
        int renderFrame = (int) Math.floor(frames / (renderTime / renderProgress));


        // Sets direction modifier ( flip ) boolean to true or false based off wether character is facing left or right.
        final boolean directionModifier;
        switch (direction) {
            case RIGHT:
                directionModifier = false;
                break;
            case LEFT:
                directionModifier = true;
                break;
            default:
                directionModifier = false;
        }


        //  Checks to see Wether the Current Frame is Equal to The Total Number of Frames,
        if (renderFrame == frames){
            renderFrame--;
            rendering = false;
        }

        // Creates a Region of Texture
        TextureRegion outputTexture = new TextureRegion(texture,renderFrame*(int)dimension.x,0,(int)dimension.x,(int)dimension.y);
        outputTexture.flip(directionModifier,false);

        // Checks if Done Rendering + Resets the Render Time Counter
        if(!rendering)
            renderProgress = 0;


        return outputTexture;
    }



}
