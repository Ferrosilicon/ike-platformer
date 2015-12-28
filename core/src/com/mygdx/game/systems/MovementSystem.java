package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mygdx.game.components.MovementComponent;
import com.mygdx.game.components.PositionComponent;

public class MovementSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<MovementComponent> mm = ComponentMapper.getFor(MovementComponent.class);

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, MovementComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            MovementComponent movement = mm.get(entity);
            PositionComponent position = pm.get(entity);
            position.position.x += movement.velocity.x * deltaTime;

            if (movement.jumping) {
                movement.deltaT += deltaTime;
                position.position.y = movement.velocity.y * movement.deltaT + movement.acceleration.y * movement.deltaT * movement.deltaT + 4;
                if (position.position.y < 4) {
                    movement.jumping = false;
                    movement.deltaT = 0;
                    position.position.y = 4;
                }
            }
        }
    }
}