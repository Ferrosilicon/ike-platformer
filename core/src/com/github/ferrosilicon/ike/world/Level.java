package com.github.ferrosilicon.ike.world;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

public final class Level implements Disposable {

    public final TiledMap tiledMap;
    public final int tileSize;
    public final int mapWidth;
    // TODO: Just discovered OrthoCachedTiledMapRenderer, need to check it out
    private final OrthogonalTiledMapRenderer tiledMapRenderer;

    public Level(final String path) {
        tiledMap = new TmxMapLoader(new InternalFileHandleResolver()).load(path);
        tileSize = tiledMap.getProperties().get("tilewidth", Integer.class);
        mapWidth = tiledMap.getProperties().get("width", Integer.class);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1f / tileSize);
    }

    public void render(final OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }
}
