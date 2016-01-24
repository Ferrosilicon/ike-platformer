package com.github.ferrosilicon.ike.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.CharArray;

/**
 * Created by KrithikR on 1/23/16.
 */


public class ExtendedTexture {
    private final Texture texture;
    private final int frames;
    private final Vector2 dimension;
    private final float renderTime;
    public boolean rendering = false;
    private float renderProgress = 0.0f;

    public ExtendedTexture(String imgPath,int numberOfFrames,Vector2 spriteDimension,float rendTime) {
        texture = new Texture(Gdx.files.internal(imgPath));
        frames = numberOfFrames;
        dimension = spriteDimension;
        renderTime = rendTime;
    }
    public TextureRegion render(float deltaTime, Entity.DirectionState dirState) {
        if (frames > 1)
            rendering = true;



        renderProgress += deltaTime;
        int renderFrame = (int) Math.floor(frames / (renderTime / renderProgress));


        final int directionModifier;
        switch (dirState) {
            case RIGHT:
                directionModifier = 1;
                break;
            case LEFT:
                directionModifier = -1;
                break;
            default:
                directionModifier = 1;
        }

        if (renderFrame == frames){
            renderFrame--;
            rendering = false;
        }


        TextureRegion outputTexture = new TextureRegion(texture,renderFrame*(int)dimension.x,0,(int)dimension.x*directionModifier,(int)dimension.y);

        if(!rendering)
            renderProgress = 0;


        return outputTexture;/*
        Texture o = new Texture(Gdx.files.internal("IkeStatic.png"));
        TextureRegion region = new TextureRegion(o,0,0,dimension.x,dimension.y);
        return region;*/
    }



}
