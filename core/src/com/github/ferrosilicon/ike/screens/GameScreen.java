package com.github.ferrosilicon.ike.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.ferrosilicon.ike.IkeGame;
import com.github.ferrosilicon.ike.entity.Entity;
import com.github.ferrosilicon.ike.entity.Ike;
import com.github.ferrosilicon.ike.world.WorldManager;

public final class GameScreen extends ScreenAdapter {

    private static final Vector2 MAX_VELOCITY = new Vector2(3, 5);

    private final IkeGame game;

    private final WorldManager worldManager;
    private final OrthographicCamera camera;

    // TODO: find out if body velocity and position references save, and if not short them
    private final Body ikeBody;
    private final Ike ike;

    public GameScreen(final IkeGame game) {
        this.game = game;

        worldManager = new WorldManager("test_map.tmx");
        worldManager.createPlayer(1.5f, 5.5f);
        ikeBody = worldManager.player;
        ike = (Ike) ikeBody.getUserData();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / worldManager.mapTileSize / 2,
                Gdx.graphics.getHeight() / worldManager.mapTileSize / 2);
        camera.update();

        Gdx.input.setInputProcessor(new ControlListener());
    }

    @Override
    public void render(final float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = MathUtils.clamp(worldManager.player.getPosition().x,
                camera.viewportWidth / 2f, worldManager.mapWidth - (camera.viewportWidth / 2f));
        camera.update();
        worldManager.render(camera);
        renderEntity(worldManager.player, deltaTime);
        updateInput();
        worldManager.step(deltaTime, camera);
    }

    private void updateInput() {
        final Vector2 vel = ikeBody.getLinearVelocity();
        final Vector2 pos = ikeBody.getPosition();

        if (ike.movingLeft && vel.x > -MAX_VELOCITY.x)
            ikeBody.applyLinearImpulse(-0.80f, 0, pos.x, pos.y, true);
        if (ike.movingRight && vel.x < MAX_VELOCITY.x)
            ikeBody.applyLinearImpulse(0.80f, 0, pos.x, pos.y, true);
    }

    private void renderEntity(final Body item, final float deltaTime) {
        final Entity entity = (Entity) item.getUserData();
        final Vector2 entityPos = entity.getPixelPosition(item.getPosition(), camera, worldManager.mapTileSize);

        game.batch.begin();
        game.batch.draw(entity.getCurrentSprite(deltaTime), entityPos.x, entityPos.y, (int) entity.dimension.x, (int) entity.dimension.y);
        game.batch.end();
    }


    @Override
    public void dispose() {
        worldManager.dispose();
    }

    private class ControlListener extends InputAdapter {

        private final float halfWidth = Gdx.graphics.getWidth() / 2;
        private int walkPointer = -1, jumpPointer = -1;
        public Vector3 originVector;
        public Vector2 currentVector;

        @Override
        public boolean keyDown(final int keyCode) {
            if (keyCode == Input.Keys.A) {
                ike.movingLeft = true;
                return true;
            }
            if (keyCode == Input.Keys.D) {
                ike.movingRight = true;
                return true;
            }
            if (keyCode == Input.Keys.W) {
                jump();
                return true;
            }
            return false;
        }

        @Override
        public boolean keyUp(final int keyCode) {
            if (keyCode == Input.Keys.A) {
                ike.movingLeft = false;
                return true;
            }
            if (keyCode == Input.Keys.D) {
                ike.movingRight = false;
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDown(final int screenX, final int screenY, final int pointer,
                                 final int button) {
            if (halfWidth > screenX) {
                walkPointer = pointer;
                originVector = new Vector3(screenX, screenY, 0);
                currentVector = new Vector2(screenX, screenY);
            } else {
                jumpPointer = pointer;
                jump();
            }
            return true;
        }

        @Override
        public boolean touchUp(final int screenX, final int screenY, final int pointer,
                               final int button) {
            if (walkPointer == pointer) {
                ike.movingLeft = false;
                ike.movingRight = false;
                originVector = null;
                currentVector = null;
                walkPointer = -1;
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
            if (walkPointer == pointer) {
                currentVector.x = screenX;
                currentVector.y = screenY;
                final float xDif = originVector.x - currentVector.x;
                final boolean distance = Math.abs(xDif) > 50;
                ike.movingLeft = distance && xDif > 0;
                ike.movingRight = distance && xDif < 0;
                return true;
            }
            return false;
        }

        private void jump() {
            if (ikeBody.getLinearVelocity().y < MAX_VELOCITY.y
                    && ike.grounded)
                ikeBody.applyLinearImpulse(0, 4f, ikeBody.getPosition().x, ikeBody.getPosition().y,
                        true);
        }
    }
}