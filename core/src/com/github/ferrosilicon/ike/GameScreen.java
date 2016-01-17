package com.github.ferrosilicon.ike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.github.ferrosilicon.ike.model.Level;
import com.github.ferrosilicon.ike.model.WorldManager;

public final class GameScreen extends ScreenAdapter {

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
        worldManager.step(deltaTime, camera);
    }

    @Override
    public void dispose() {
        worldManager.dispose();
        level.dispose();
    }
}