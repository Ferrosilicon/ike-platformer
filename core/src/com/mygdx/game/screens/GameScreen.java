package com.mygdx.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Level;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.components.MovementComponent;
import com.mygdx.game.components.PositionComponent;
import com.mygdx.game.systems.MovementSystem;
import com.mygdx.game.systems.RenderSystem;

public class GameScreen extends ScreenAdapter {

    public MyGdxGame game;
    public OrthographicCamera camera;
    public Entity player;
    public Level level;
    public Texture entitiesTexture;
    public PooledEngine engine;

    public GameScreen(MyGdxGame game) {
        this.game = game;

        initiateTextures();
        initiateEngine();
        initiateCamera();
        initiateRender();

        loadLevel("demo_map");
    }

    private void initiateEngine() {
        engine = new PooledEngine(1, 100, 0, 5);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderSystem(game.batch));
    }

    private void initiateCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 32, 18);
        camera.update();
    }

    private void initiateRender() {
        Gdx.gl.glClearColor(255, 255, 255, 1);
    }

    private void initiateTextures() {
        entitiesTexture = new Texture(Gdx.files.internal("entities.png"));
    }

    public void loadLevel(String name) {
        level = new Level(this, name + ".tmx");
    }

    @Override
    public void render(float delta) {
        updateInput();
        updateCamera();
        level.update();
        game.batch.begin();
        engine.update(delta);
        game.batch.end();
    }

    private void updateInput() {
        MovementComponent movement = player.getComponent(MovementComponent.class);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            movement.velocity.x = 7;
        else if (Gdx.input.isKeyPressed(Input.Keys.A))
            movement.velocity.x = -7;
        else
            movement.velocity.x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.velocity.y = 16;
            movement.jumping = true;
        }
    }

    private void updateCamera() {
        camera.position.x = MathUtils.clamp(player.getComponent(PositionComponent.class).position.x,
                camera.viewportWidth / 2f, level.mapWidth - (camera.viewportWidth / 2f));
        camera.update();
    }
}