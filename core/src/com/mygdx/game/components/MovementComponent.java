package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component {

    public Vector2 velocity;
    public Vector2 acceleration;
    public boolean jumping = false;
    public boolean alwaysJumping = false;
    public float deltaT;

    public MovementComponent() {
        this(new Vector2(0, 0), new Vector2(0, -16));
    }

    public MovementComponent(Vector2 velocity, Vector2 acceleration) {
        this(velocity, acceleration, false);
    }

    public MovementComponent(Vector2 velocity, Vector2 acceleration, boolean alwaysJumping) {
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.alwaysJumping = alwaysJumping;
    }
}
