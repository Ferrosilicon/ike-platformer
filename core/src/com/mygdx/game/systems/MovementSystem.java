package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mygdx.game.components.MovementComponent;
import com.mygdx.game.components.PositionComponent;



/**
 * Created by Shubhang on 12/27/2015.
 */
public class MovementSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<MovementComponent> mm = ComponentMapper.getFor(MovementComponent.class);

    public MovementSystem() {}

    int deltaT = 0;

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, MovementComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent position = pm.get(entity);
            MovementComponent movement = mm.get(entity);

            position.position.x += movement.velocity.x * deltaTime;

            if (movement.jumping) {
                deltaT += deltaTime;
                position.position.y = movement.velocity.y * deltaT - movement.acceleration.y * deltaT * deltaT + 4;
               if (position.position.y < 4) {
                    movement.jumping = false;
                    deltaT = 0;
                    position.position.y = 4;
                }
            }
        }
    }
}