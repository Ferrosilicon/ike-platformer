package com.github.ferrosilicon.ike.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.ferrosilicon.ike.IkeGame;
import com.github.ferrosilicon.ike.world.WorldManager;

public final class GameScreen extends ScreenAdapter {

    private static final Vector2 MAX_VELOCITY = new Vector2(3, 5);

    private final IkeGame game;

    private final WorldManager worldManager;
    private final OrthographicCamera camera;

    public GameScreen(final IkeGame game) {
        this.game = game;

        worldManager = new WorldManager("test_map.tmx");
        worldManager.createPlayer(1.5f, 5.5f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / worldManager.mapTileSize / 2,
                Gdx.graphics.getHeight() / worldManager.mapTileSize / 2);
        camera.update();
    }

    @Override
    public void render(final float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = MathUtils.clamp(worldManager.player.getPosition().x,
                camera.viewportWidth / 2f, worldManager.mapWidth - (camera.viewportWidth / 2f));
        camera.update();
        worldManager.render(camera);

        updateInput();
        worldManager.step(deltaTime, camera);
    }

    private void updateInput() {
        final Body player = worldManager.player;
        final Vector2 vel = player.getLinearVelocity();
        final Vector2 pos = player.getPosition();

        System.out.println(vel);
        if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -MAX_VELOCITY.x)
            player.applyLinearImpulse(-0.80f, 0, pos.x, pos.y, true);
        if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < MAX_VELOCITY.x)
            player.applyLinearImpulse(0.80f, 0, pos.x, pos.y, true);
        if (Gdx.input.isKeyPressed(Input.Keys.W) && vel.y < MAX_VELOCITY.y
                && Math.abs(vel.y) < 0.005)
            player.applyLinearImpulse(0, 4f, pos.x, pos.y, true);
    }

    @Override
    public void dispose() {
        worldManager.dispose();
    }
}