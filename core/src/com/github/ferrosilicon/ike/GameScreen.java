package com.github.ferrosilicon.ike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.ferrosilicon.ike.model.Level;
import com.github.ferrosilicon.ike.model.WorldManager;

public final class GameScreen extends ScreenAdapter {

    private static final int MAX_VELOCITY = 3;

    private final IkeGame game;

    private final WorldManager worldManager;
    private final Level level;
    private final OrthographicCamera camera;

    public GameScreen(final IkeGame game) {
        this.game = game;

        level = new Level("test_map.tmx");

        worldManager = new WorldManager();
        worldManager.createLevel(level);
        worldManager.createPlayer(1.5f, 5.5f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / level.tileSize / 2,
                Gdx.graphics.getHeight() / level.tileSize / 2);
        camera.update();
    }

    @Override
    public void render(final float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = MathUtils.clamp(worldManager.player.getPosition().x,
                camera.viewportWidth / 2f, level.mapWidth - (camera.viewportWidth / 2f));
        level.render(camera);
        Vector2 vel = worldManager.player.getLinearVelocity();
        Vector2 pos = worldManager.player.getPosition();

// apply left impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -MAX_VELOCITY) {
            worldManager.player.applyLinearImpulse(-0.80f, 0, pos.x, pos.y, true);
        }

// apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < MAX_VELOCITY) {
            worldManager.player.applyLinearImpulse(0.80f, 0, pos.x, pos.y, true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && vel.x < MAX_VELOCITY) {
            worldManager.player.applyLinearImpulse(0, 0.80f, pos.x, pos.y, true);
        }
        worldManager.step(deltaTime, camera);
    }

    @Override
    public void dispose() {
        worldManager.dispose();
        level.dispose();
    }
}