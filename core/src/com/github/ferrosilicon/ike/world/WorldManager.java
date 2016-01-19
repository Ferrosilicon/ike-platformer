package com.github.ferrosilicon.ike.world;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.github.ferrosilicon.ike.world.util.WorldBuilder;

public final class WorldManager implements Disposable {

    private static final float TIME_STEP = 1 / 45f;

    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    public final TiledMap map;
    public final int mapTileSize;
    public final int mapWidth;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;

    public Body player;
    private float stepAccumulator;

    public WorldManager(final String level) {
        world = new World(new Vector2(0, -9.80665f), true);
        debugRenderer = new Box2DDebugRenderer();

        map = new TmxMapLoader(new InternalFileHandleResolver()).load(level);
        mapTileSize = map.getProperties().get("tilewidth", Integer.class);
        mapWidth = map.getProperties().get("width", Integer.class);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1f / mapTileSize);

        WorldBuilder.buildShapes(map, mapTileSize, world);
    }

    public void render(final OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    public void step(final float deltaTime, final OrthographicCamera camera) {
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
        body.createFixture(fixtureDef);

        groundBox.dispose();

        body.setUserData(new Character());
        player = body;
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        map.dispose();
        tiledMapRenderer.dispose();
    }
}
