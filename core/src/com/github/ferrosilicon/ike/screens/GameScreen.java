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
import com.github.ferrosilicon.ike.entity.Character;
import com.github.ferrosilicon.ike.entity.Entity;
import com.github.ferrosilicon.ike.entity.Ike;
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
        renderEntity( worldManager.player , deltaTime );
        updateInput();
        worldManager.step(deltaTime, camera);
    }

    private void updateInput() {
        final Body player = worldManager.player;
        Ike playerData = (Ike) player.getUserData();
        final Vector2 vel = player.getLinearVelocity();
        final Vector2 pos = player.getPosition();


        playerData.setCharacterState(Character.CharacterState.STANDING);

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerData.wallHitCount.y = 0;
            playerData.setCharacterState(Character.CharacterState.RUNNING);
            playerData.directionState = Entity.DirectionState.LEFT;
            //System.out.print(vel.x);

            if( vel.x > -MAX_VELOCITY.x && !playerData.skipImpulse )
                player.applyLinearImpulse(-0.80f, 0, pos.x, pos.y, true);
            if ((vel.y < 0.0)|(playerData.wallHitCount.x++>3 && vel.y<0.0)
                    && (player.getLinearVelocity().x == -1.6f || playerData.skipImpulse)
                    && playerData.lastPos.x-player.getPosition().x == 0)
            {
                System.out.print("ya");
                player.setLinearVelocity(0, vel.y);
                playerData.skipImpulse = true;
            }else {
                playerData.skipImpulse = false;
                playerData.wallHitCount.set(0,0);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerData.wallHitCount.x = 0;

            playerData.setCharacterState(Character.CharacterState.RUNNING);
            playerData.directionState = Entity.DirectionState.RIGHT;


            if (vel.x < MAX_VELOCITY.x && !playerData.skipImpulse)
                player.applyLinearImpulse(0.80f, 0, pos.x, pos.y, true);
            if (((vel.y < 0.0)|(playerData.wallHitCount.y++>3 && vel.y<0.1))
                    && (player.getLinearVelocity().x == 1.6f || playerData.skipImpulse)
                    && playerData.lastPos.x-player.getPosition().x == 0)
            {
                System.out.print("ya");
                player.setLinearVelocity(0, vel.y);
                playerData.skipImpulse = true;

                if(playerData.wallHitCount.y>10){
                    playerData.wallHitCount.y = 0;
                }
            }else {
                playerData.skipImpulse = false;
                playerData.wallHitCount.set(0,0);
            }




        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerData.setCharacterState(Character.CharacterState.JUMPING);
            if( vel.y < MAX_VELOCITY.y && Math.abs(vel.y) < 0.005)
                player.applyLinearImpulse(0, 4f, pos.x, pos.y, true);

        }
        if(player.getLinearVelocity().x == 0 && player.getLinearVelocity().y == 0)
            playerData.wallHitCount.set(0,0);

        playerData.lastPos = player.getPosition();
        System.out.println(player.getLinearVelocity()+ ":" + player.getPosition().x);



    }
    private void renderEntity(Body item,float deltaTime){
        Entity entity = (Entity) item.getUserData();
        Vector2 entityPos = entity.getPixelPosition( item.getPosition() , camera, worldManager.mapTileSize);

        game.batch.begin();
        game.batch.draw(entity.getCurrentSprite(deltaTime),entityPos.x,entityPos.y,(int)entity.dimension.x,(int)entity.dimension.y);
        game.batch.end();
    }



    @Override
    public void dispose() {
        worldManager.dispose();
    }
}