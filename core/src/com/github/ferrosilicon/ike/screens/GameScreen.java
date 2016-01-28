package com.github.ferrosilicon.ike.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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

    private final Body ikeBody;
    private final Ike ike;

    public Vector2 originVector, currentVector;

    private int halfWidth;

    private final ShapeRenderer shapeRenderer;

    public GameScreen(final IkeGame game) {
        this.game = game;

        worldManager = new WorldManager("test_map.tmx");
        worldManager.createPlayer(1.5f, 5.5f);
        ikeBody = worldManager.player;
        ike = (Ike) ikeBody.getUserData();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 32, 18);
        camera.update();

        shapeRenderer = new ShapeRenderer();

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

        if (originVector != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0.5f, 0.5f, 0, 0.5f);
            shapeRenderer.line(originVector.x - ControlListener.MIN_X_DISTANCE, 0,
                    originVector.x - ControlListener.MIN_X_DISTANCE, Gdx.graphics.getHeight());
            shapeRenderer.line(originVector.x + ControlListener.MIN_X_DISTANCE, 0,
                    originVector.x + ControlListener.MIN_X_DISTANCE, Gdx.graphics.getHeight());
            shapeRenderer.setColor(0, 0.5f, 0.5f, 0.5f);
            shapeRenderer.circle(originVector.x, Gdx.graphics.getHeight() - originVector.y,
                    ControlListener.ORIGIN_MAX_DISTANCE);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

        }
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

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(entity.getCurrentSprite(deltaTime),
                +item.getPosition().x - 0.5f, item.getPosition().y - 0.5f, 1, 1);
        game.batch.end();
    }

    @Override
    public void resize(final int width, final int height) {
        halfWidth = width / 2;
    }

    @Override
    public void dispose() {
        worldManager.dispose();
    }

    private class ControlListener extends InputAdapter {

        private static final int MIN_X_DISTANCE = 100;
        private static final int ORIGIN_MAX_DISTANCE = 300;
        private static final int ORIGIN_UPDATE_DISTANCE = 150;

        private int walkPointer = -1, jumpPointer = -1;

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
                originVector = new Vector2(screenX, screenY);
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
                jumpPointer = -1;
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
                final boolean distance = Math.abs(xDif) > MIN_X_DISTANCE;
                if (originVector.dst(currentVector) > ORIGIN_MAX_DISTANCE) {
                    final float angle = MathUtils.atan2(originVector.y - screenY,
                            originVector.x - screenX);
                    originVector.x -= MathUtils.cos(angle) * ORIGIN_UPDATE_DISTANCE;
                    originVector.y -= MathUtils.sin(angle) * ORIGIN_UPDATE_DISTANCE;
                }

                ike.movingLeft = distance && xDif > 0;
                ike.movingRight = distance && xDif < 0;
                return true;
            }
            return false;
        }

        private void jump() {
            if (ikeBody.getLinearVelocity().y < MAX_VELOCITY.y && ike.grounded)
                ikeBody.applyLinearImpulse(0, 4f, ikeBody.getPosition().x, ikeBody.getPosition().y,
                        true);
        }
    }
}