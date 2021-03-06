package com.github.ferrosilicon.ike.world;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.github.ferrosilicon.ike.entity.CharacterTextureSet;
import com.github.ferrosilicon.ike.entity.Entity;
import com.github.ferrosilicon.ike.entity.ExtendedTexture;
import com.github.ferrosilicon.ike.entity.Ike;
import com.github.ferrosilicon.ike.world.util.WorldBuilder;

public final class WorldManager implements Disposable {

    // The amount of updates per second
    private static final float TIME_STEP = 1 / 300f;

    // Two performance variables that you don't need to worry about
    private static final int VELOCITY_ITERATIONS = 16;
    private static final int POSITION_ITERATIONS = 6;

    public final TiledMap map;
    public final int mapTileSize;
    public final int mapWidth;

    private final World world;
    private final Box2DDebugRenderer debugRenderer;

    private final OrthogonalTiledMapRenderer tiledMapRenderer;

    public Body player;
    private float stepAccumulator;

    public WorldManager(final String level) {
        // Create a new world instance with the specified gravity and with body sleeping enabled
        // Body sleeping saves CPU on bodies that have no forces acting upon them
        world = new World(new Vector2(0, -9.80665f), true);
        // Sets the world contact listener which currently just fixes sticking to walls
        world.setContactListener(new WorldContactListener());
        // Create a debug renderer to view the boundaries of the Box2d bodies
        debugRenderer = new Box2DDebugRenderer();

        // Load the tiled map
        map = new TmxMapLoader(new InternalFileHandleResolver()).load(level);
        // Get the size of an individual tile
        mapTileSize = map.getProperties().get("tilewidth", Integer.class);
        // Gets the width of the map in tiles
        mapWidth = map.getProperties().get("width", Integer.class);

        // Create a tiled map renderer with the map and map's tile size
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1f / mapTileSize);

        // Create Box2d bodies to represent the Tiled map
        WorldBuilder.buildShapes(map, mapTileSize, world);
    }

    // A list of all of the boddies in the Box2d world
    private Array<Body> bodies = new Array<Body>();

    // Renders the world to the sprite batch with the camera's projection and specified delta time
    public void render(final SpriteBatch batch, final OrthographicCamera camera,
                       final float deltaTime) {
        // Projects the renderer to the camera
        tiledMapRenderer.setView(camera);
        // Renders the map
        tiledMapRenderer.render();

        // Populates the bodies array with the world's bodies
        world.getBodies(bodies);
        // Begin the batch
        batch.begin();
        for (final Body body : bodies)
            // If the body belongs to an entity, render it
            if (body.getUserData() instanceof Entity)
                renderEntity(batch, camera, body, deltaTime);
        // End the batch
        batch.end();

        // Renders the body outlines
        debugRenderer.render(world, camera.combined);
    }

    // Renders the entity
    private void renderEntity(final SpriteBatch batch, final OrthographicCamera camera,
                              final Body body, final float deltaTime) {
        // Project the batch with the camera
        batch.setProjectionMatrix(camera.combined);
        // Gets the current sprite of the entity and draws it. FIXME: hardcoded size
        batch.draw(((Entity) body.getUserData()).getCurrentSprite(deltaTime),
                body.getPosition().x - 0.5f, body.getPosition().y - 0.5f, 1, 1);
    }

    // All you need to know about this is that it updates the Box2d world. Don't worry about how
    public void step(final float deltaTime) {
        stepAccumulator += Math.min(deltaTime, 0.25f);
        while (stepAccumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            stepAccumulator -= TIME_STEP;
        }
    }

    // Creates a player at the specified x and y coordinates
    public void createPlayer(final float x, final float y) {
        // Create a new body definition
        final BodyDef bodyDef = new BodyDef();
        // Sets the body type to dynamic. Dynamic bodies are affected by forces and gravity
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Sets the position of the body
        bodyDef.position.set(x, y);
        // Makes the body unable to rotate, so if you clip the side of a wall the body won't flip
        bodyDef.fixedRotation = true;

        // Create a new body using the body definition
        final Body body = world.createBody(bodyDef);
        // Creates a new polygon shape to give the body. Other shapes are circle, edge, and chain.
        final PolygonShape groundBox = new PolygonShape();
        // Create a 1x1 unit rectangle, as the parameters are half of the actual values. (1/2 = 1)
        // One unit = WorldManager.mapTileSize;
        groundBox.setAsBox(0.5f, 0.5f);

        // Creates a new definition for the fixture
        final FixtureDef fixtureDef = new FixtureDef();
        // Gives the definition the shape we created
        fixtureDef.shape = groundBox;
        // Gives the definition a friction value of 0.3. The value should be between 0 and 1
        fixtureDef.friction = 20f;
        fixtureDef.density = 0.5f;
        // Creates a new fixture with the fixture definition
        body.createFixture(fixtureDef);

        // A fixture was already created with the shape, so the shape can now be removed from mem
        groundBox.dispose();
        // Creates a texture set to contain all the texture
        CharacterTextureSet ikeTextures = new CharacterTextureSet();
        // Creates the standing texture
        ikeTextures.standingTexture = new ExtendedTexture("IkeStatic.png", 1, new Vector2(64, 64), 0.1f);
        // Creates the wlaking texture
        ikeTextures.walkingTexture = new ExtendedTexture("minion_death.png", 3, new Vector2(32, 32), 0.9f);
        // Attaches an object to the body which we can use later. Currently has no use, but later
        // it will be used to hold things such as the health of the entity
        body.setUserData(new Ike(ikeTextures));
        // Sets the player field to the body we just created for easier access
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
