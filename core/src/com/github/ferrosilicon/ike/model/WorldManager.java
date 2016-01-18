package com.github.ferrosilicon.ike.model;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public final class WorldManager implements Disposable {

    private static final float TIME_STEP = 1 / 45f;

    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private static final boolean DEBUG_RENDER = true;

    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    public Body player;
    private float stepAccumulator;

    public WorldManager() {
        world = new World(new Vector2(0, -9.80665f), true);
        debugRenderer = new Box2DDebugRenderer();
    }

    // TODO: Need to remove all previous bodies
    public void createLevel(final Level level) {
        MapBodyBuilder.buildShapes(level.tiledMap, level.tileSize, world);
    }

    public void step(final float deltaTime, final OrthographicCamera camera) {
        if (DEBUG_RENDER)
            debugRenderer.render(world, camera.combined);

        stepAccumulator += Math.min(deltaTime, 0.25f);
        while (stepAccumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            stepAccumulator -= TIME_STEP;
        }
    }

    public void createPlayer(final float x, final float y) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        final Body body = world.createBody(bodyDef);
        final PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(0.5f, 0.5f);
        body.createFixture(groundBox, 0.0f);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundBox;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.9f;
        fixtureDef.restitution = 0.2f;
        body.createFixture(fixtureDef);

        groundBox.dispose();
        player = body;
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }
}
