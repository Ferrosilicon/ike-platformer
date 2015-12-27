package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
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
import com.mygdx.game.components.MovementComponent;
import com.mygdx.game.components.PositionComponent;
import com.mygdx.game.systems.MovementSystem;

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

    PooledEngine engine = new PooledEngine(1,
            100,
            0,
            5);
    Entity mainCharacter;
    MovementComponent movementComponent;
    PositionComponent positionComponent;

    public GameScreen(MyGdxGame game) {
        this.game = game;
        engine.addSystem(new MovementSystem());
        initiatePlayer();
        initiateTiledMap();
        initiateCamera();
        initiateRender();
    }

    private void initiatePlayer() {
//        playerX = 0;
//        playerY = 4;
//        playerTexture = new Texture(Gdx.files.internal("player.png"));
        mainCharacter = engine.createEntity();

        mainCharacter.add(new PositionComponent());
        positionComponent = mainCharacter.getComponent(PositionComponent.class);
        positionComponent.position.add(0, 4);

        mainCharacter.add(new MovementComponent());
        movementComponent = mainCharacter.getComponent(MovementComponent.class);
        movementComponent.acceleration.add(0, -16);
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
        updateVectors(delta);
        engine.update(delta);
        updateCamera();
        updateRender();
    }

    private void updateVectors(final float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            movementComponent.velocity.set(7,0);
        else if (Gdx.input.isKeyPressed(Input.Keys.A))
            movementComponent.velocity.set(-7,0);
        else
            movementComponent.velocity.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movementComponent.velocity.add(0, 16);
            movementComponent.jumping = true;
        }
//        if (Gdx.input.isKeyPressed(Input.Keys.W))
//            jumping = true;
//
//        if (jumping) {
//            deltaT += delta;
//            playerY = INITIAL_JUMP_VELOCITY * deltaT - GRAVITATIONAL_ACCELERATION * deltaT * deltaT + 4;
//            if (playerY < 4) {
//                jumping = false;
//                deltaT = 0;
//                playerY = 4;
//            }
//        }

//        if (playerX < 0)
//            playerX = 0;
//        else if (playerX > mapWidth - 1)
//            playerX = mapWidth - 1;
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