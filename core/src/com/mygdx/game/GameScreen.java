package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

public class GameScreen extends ScreenAdapter {

    final float UNITS_SECOND = 7;
    final float INITIAL_JUMP_VELOCITY = 16;
    final float GRAVITATIONAL_ACCELERATION = 16;

    MyGdxGame game;

    OrthographicCamera camera;
    TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    int mapWidth;

    Texture playerTexture;
    float playerX, playerY;
    boolean jumping = false;
    float deltaT = 0;

    public GameScreen(MyGdxGame game) {
        this.game = game;

        initiatePlayer();
        initiateTiledMap();
        initiateCamera();
        initiateRender();
    }

    private void initiatePlayer() {
        playerX = 0;
        playerY = 4;
        playerTexture = new Texture(Gdx.files.internal("player.png"));
    }

    private void initiateTiledMap() {
        map = new TmxMapLoader(new InternalFileHandleResolver()).load("demo_map.tmx");
        mapWidth = map.getProperties().get("width", Integer.class);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
    }

    private void initiateCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 32, 18);
        camera.update();
    }

    private void initiateRender() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
    }

    @Override
    public void render(final float delta) {
        updateMovement(delta);
        updateCamera();
        updateRender();
    }

    private void updateMovement(final float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            playerX += UNITS_SECOND * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            playerX -= UNITS_SECOND * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            jumping = true;

        if (jumping) {
            deltaT += delta;
            playerY = INITIAL_JUMP_VELOCITY * deltaT - GRAVITATIONAL_ACCELERATION * deltaT * deltaT + 4;
            if (playerY < 4) {
                jumping = false;
                deltaT = 0;
                playerY = 4;
            }
        }

        if (playerX < 0)
            playerX = 0;
        else if (playerX > mapWidth - 1)
            playerX = mapWidth - 1;
    }

    private void updateCamera() {
        camera.position.x = MathUtils.clamp(playerX, camera.viewportWidth / 2f, mapWidth - (camera.viewportWidth / 2f));
        camera.update();
    }

    private void updateRender() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(camera);
        renderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(playerTexture, playerX, playerY, 1, 2);
        game.batch.end();
    }
}