package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component {

    public Vector2 velocity = new Vector2(0, 0);
    public Vector2 acceleration = new Vector2(0, 0);
    public boolean jumping = false;
    public boolean alwaysJumping = false;
    public float deltaT;

    public MovementComponent(Vector2 velocity, Vector2 acceleration) {
        this.velocity = velocity;
        this.acceleration = acceleration;
    }
}
