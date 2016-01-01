package com.mygdx.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Level;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.components.MovementComponent;
import com.mygdx.game.components.PositionComponent;
import com.mygdx.game.components.TextureComponent;
import com.mygdx.game.systems.MovementSystem;
import com.mygdx.game.systems.RenderSystem;

public class GameScreen extends ScreenAdapter {

    public MyGdxGame game;
    public OrthographicCamera camera;
    public Entity mainCharacter;
    public Level level;

    private PooledEngine engine;

    public GameScreen(MyGdxGame game) {
        this.game = game;

        initiateEngine();
        initiatePlayer();
        initiateCamera();
        initiateRender();

        loadLevel("demo_map");
    }

    private void initiateEngine() {
        engine = new PooledEngine(1, 100, 0, 5);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderSystem(game.batch));
    }

    private void initiatePlayer() {
        mainCharacter = engine.createEntity();
        mainCharacter.add(new TextureComponent(new TextureRegion(new Texture(Gdx.files.internal("player.png")))));
        mainCharacter.add(new PositionComponent(new Vector2(0, 4)));
        mainCharacter.add(new MovementComponent(new Vector2(0, 0), new Vector2(0, -16)));
        engine.addEntity(mainCharacter);
    }

    private void initiateCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 32, 18);
        camera.update();
    }

    private void initiateRender() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        MovementComponent movement = mainCharacter.getComponent(MovementComponent.class);
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
        camera.position.x = MathUtils.clamp(mainCharacter.getComponent(PositionComponent.class).position.x,
                camera.viewportWidth / 2f, level.map.getProperties().get("width", Integer.class) - (camera.viewportWidth / 2f));
        camera.update();
    }
}