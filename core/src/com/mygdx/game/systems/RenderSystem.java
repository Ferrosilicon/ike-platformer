package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.components.PositionComponent;
import com.mygdx.game.components.TextureComponent;

public class RenderSystem extends EntitySystem {

    private SpriteBatch batch;
    private ImmutableArray<Entity> entities;
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent position = pm.get(entity);
            batch.draw(tm.get(entity).textureRegion, position.position.x, position.position.y, 1, 2);
        }
    }
}