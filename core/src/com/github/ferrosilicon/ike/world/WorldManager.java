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
import com.github.ferrosilicon.ike.entity.CharacterTextureSet;
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
        World.setVelocityThreshold(0.0f);
        debugRenderer = new Box2DDebugRenderer();

        map = new TmxMapLoader(new InternalFileHandleResolver()).load(level);
        mapTileSize = map.getProperties().get("tilewidth", Integer.class);
        mapWidth = map.getProperties().get("width", Integer.class);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1f / mapTileSize);

        // Create Box2d bodies to represent the Tiled map
        WorldBuilder.buildShapes(map, mapTileSize, world);
    }

    public void render(final OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    // All you need to know about this is that it updates the Box2d world. Don't worry about how
    public void step(final float deltaTime, final OrthographicCamera camera) {
        debugRenderer.render(world, camera.combined);

        stepAccumulator += Math.min(deltaTime, 0.25f);
        while (stepAccumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            stepAccumulator -= TIME_STEP;
        }
    }

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

        groundBox.setAsBox((Ike.DIMENSION.x/mapTileSize)/4, (Ike.DIMENSION.x/mapTileSize)/4);

        // Creates a new definition for the fixture
        final FixtureDef fixtureDef = new FixtureDef();
        // Gives the definition the shape we created
        fixtureDef.shape = groundBox;
        // Gives the definition a restitution ( bounciness ) of 0
        fixtureDef.restitution = 0.0f;
        // Gives the definition a friction value of 0.3. The value should be between 0 and 1
        fixtureDef.friction = 0.3f;
        fixtureDef.density = 0.5f;

        // Creates a new fixture with the fixture definition
        body.createFixture(fixtureDef);

        // A fixture was already created with the shape, so the shape can now be removed from mem
        groundBox.dispose();

        // Creates the Texture Set for Ike
        CharacterTextureSet ikeTextures = new CharacterTextureSet();
        ikeTextures.standingTexture = new ExtendedTexture("IkeStatic.png",1,new Vector2(64,64),0.1f);
        ikeTextures.walkingTexture = new ExtendedTexture("minion_death.png",3,new Vector2(32,32),0.9f);

        // Attaches an object to the body which we can use later.
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
