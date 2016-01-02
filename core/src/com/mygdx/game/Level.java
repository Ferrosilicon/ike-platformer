package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.screens.GameScreen;

public class Level {

    GameScreen gameScreen;
    OrthogonalTiledMapRenderer renderer;
    public TiledMap map;

    public Level(GameScreen gameScreen, String name) {
        this.gameScreen = gameScreen;

        initiateMap(name);
    }

    private void initiateMap(String name) {
        map = new TmxMapLoader(new InternalFileHandleResolver()).load(name);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
    }

    public void update() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(gameScreen.camera);
        renderer.render();
        gameScreen.game.batch.setProjectionMatrix(gameScreen.camera.combined);
    }
}
