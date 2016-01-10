package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.components.MovementComponent;
import com.mygdx.game.components.PositionComponent;
import com.mygdx.game.components.TextureComponent;
import com.mygdx.game.screens.GameScreen;

public class Level {

    public int mapWidth;
    public TiledMap map;
    GameScreen gameScreen;
    OrthogonalTiledMapRenderer renderer;

    public Level(GameScreen gameScreen, String name) {
        this.gameScreen = gameScreen;

        initiateMap(name);
        initiateEntities();
    }

    private void initiateMap(String name) {
        map = new TmxMapLoader(new InternalFileHandleResolver()).load(name);
        mapWidth = map.getProperties().get("width", Integer.class);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
    }

    private void initiateEntities() {
        gameScreen.engine.removeAllEntities();
        for (final MapObject object : map.getLayers().get("Object Layer 1").getObjects()) {
            if (object.getName().equals("Player"))
                initiatePlayer(getObjectVector2(object));
            else if (object.getName().equals("Enemy"))
                initiateEnemy(getObjectVector2(object));
        }
    }

    private void initiatePlayer(Vector2 position) {
        Entity player = gameScreen.engine.createEntity();
        player.add(new TextureComponent(new TextureRegion(gameScreen.entitiesTexture, 0, 0, 32, 64)));
        player.add(new PositionComponent(position));
        player.add(new MovementComponent());
        gameScreen.player = player;
        gameScreen.engine.addEntity(player);
    }

    private void initiateEnemy(Vector2 position) {
        Entity enemy = gameScreen.engine.createEntity();
        enemy.add(new TextureComponent(new TextureRegion(gameScreen.entitiesTexture, 32, 0, 32, 64)));
        enemy.add(new PositionComponent(position));
        enemy.add(new MovementComponent(new Vector2(0, 16), new Vector2(0, -16), true));
        gameScreen.engine.addEntity(enemy);
    }

    private Vector2 getObjectVector2(MapObject object) {
        return new Vector2(object.getProperties().get("x", Float.class) / 32,
                (mapWidth - object.getProperties().get("y", Float.class)) / 32 + object.getProperties().get("height", Float.class) / 32);
    }

    public void update() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(gameScreen.camera);
        renderer.render();
        gameScreen.game.batch.setProjectionMatrix(gameScreen.camera.combined);
    }
}
