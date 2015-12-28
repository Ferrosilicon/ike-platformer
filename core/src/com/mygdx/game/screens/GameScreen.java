package com.mygdx.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.components.MovementComponent;
import com.mygdx.game.components.PositionComponent;
import com.mygdx.game.components.TextureComponent;
import com.mygdx.game.systems.MovementSystem;
import com.mygdx.game.systems.RenderSystem;

public class GameScreen extends ScreenAdapter {

    MyGdxGame game;
    PooledEngine engine;
    MovementComponent movementComponent;
    PositionComponent positionComponent;
    TextureComponent textureComponent;
    Entity mainCharacter;
    TiledMap map;
    int mapWidth;
    OrthogonalTiledMapRenderer renderer;
    OrthographicCamera camera;

    public GameScreen(MyGdxGame game) {
        this.game = game;

        initiateEngine();
        initiatePlayer();
        initiateTiledMap();
        initiateCamera();
        initiateRender();
    }

    private void initiateEngine() {
        engine = new PooledEngine(1, 100, 0, 5);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderSystem(game.batch));
    }

    private void initiatePlayer() {
        mainCharacter = engine.createEntity();

        mainCharacter.add(new TextureComponent());
        textureComponent = mainCharacter.getComponent(TextureComponent.class);
        textureComponent.textureRegion = new TextureRegion(new Texture(Gdx.files.internal("player.png")));

        mainCharacter.add(new PositionComponent());
        positionComponent = mainCharacter.getComponent(PositionComponent.class);
        positionComponent.position.add(0, 4);

        mainCharacter.add(new MovementComponent());
        movementComponent = mainCharacter.getComponent(MovementComponent.class);
        movementComponent.acceleration.add(0, -16);

        engine.addEntity(mainCharacter);
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
        updateInput();
        updateCamera();
        updateRender();
        game.batch.begin();
        engine.update(delta);
        game.batch.end();

    }

    private void updateInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            movementComponent.velocity.x = 7;
        else if (Gdx.input.isKeyPressed(Input.Keys.A))
            movementComponent.velocity.x = -7;
        else
            movementComponent.velocity.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movementComponent.velocity.y = 16;
            movementComponent.jumping = true;
        }
    }

    private void updateCamera() {
        camera.position.x = MathUtils.clamp(positionComponent.position.x, camera.viewportWidth / 2f, mapWidth - (camera.viewportWidth / 2f));
        camera.update();
    }

    private void updateRender() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(camera);
        renderer.render();
        game.batch.setProjectionMatrix(camera.combined);
    }
}