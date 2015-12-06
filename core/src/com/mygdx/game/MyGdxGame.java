package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

public class MyGdxGame extends ApplicationAdapter {

    final float UNITS_SECOND = 7;

    SpriteBatch batch;
    OrthographicCamera camera;
    TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    int mapWidth;

    // TODO: Create an entity system?
    Texture playerTexture;
    float playerX, playerY = 4;

    @Override
    public void create() {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set the clear color

        // Load resources into memory
        playerTexture = new Texture(Gdx.files.internal("player.png"));
        map = new TmxMapLoader(new InternalFileHandleResolver()).load("demo_map.tmx");

        // Initialize various objects
        batch = new SpriteBatch();
        mapWidth = map.getProperties().get("width", Integer.class);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
        camera = new OrthographicCamera();

        // Setup camera's orthographic projection
        camera.setToOrtho(false, 32, 18);
        camera.update();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

        // Move the player according to input
        // TODO: Make movement use velocity
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            playerX += UNITS_SECOND * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            playerX -= UNITS_SECOND * Gdx.graphics.getDeltaTime();

        // Cap the min and max position of the player
        if (playerX < 0)
            playerX = 0;
        else if (playerX > mapWidth - 1)
            playerX = mapWidth - 1;

        camera.position.x = playerX; // Update the camera x to the player's x

        // Set up camera
        camera.position.x = MathUtils.clamp(camera.position.x, camera.viewportWidth / 2f, mapWidth - (camera.viewportWidth / 2f));
        camera.update();

        renderer.setView(camera); // Make map renderer use camera projection
        renderer.render(); // Render the map

        batch.setProjectionMatrix(camera.combined); // Make the batch use camera projection
        batch.begin();
        batch.draw(playerTexture, playerX, playerY, 1, 2); // Draw the player
        batch.end();
    }
}