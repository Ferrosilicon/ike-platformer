package com.github.ferrosilicon.ike.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
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
        int playerContactPoints = 0;

        // Checks for Contact Points of The Player
        for(Contact con : player.getWorld().getContactList()){
            if(con.getFixtureB().getBody().getUserData() != null && con.getFixtureB().getBody().getUserData().equals(player.getUserData())){
                playerContactPoints++;
                // Test Code
                //System.out.println(con.getFixtureA().toString());
            }
        }

        // Sets the Default Character State to Standing
        playerData.setCharacterState(Character.CharacterState.STANDING);

        // Handles Keyboard Input
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            // Resets Right Wall Hit Count
            playerData.wallHitCount.y = 0;

            // Updates Character State / Direction
            playerData.setCharacterState(Character.CharacterState.RUNNING);
            playerData.directionState = Entity.DirectionState.LEFT;


            // Gives the Player a Nudge to the Left
            if( vel.x > -MAX_VELOCITY.x && !playerData.skipImpulse )
                player.applyLinearImpulse(-0.80f, 0, pos.x, pos.y, true);

            /*
            Checks for (Downwards Velocity or Repeated Wall Hits + Downward Velocity)
            and For (A Specific Linear Velocity or Repeated Touches)
            and For A Change in Position
            and For Player Contact
            To determine wether Contact with Wall has been Made
             */
            if ((vel.y < 0.0)|(playerData.wallHitCount.x++>3 && vel.y<0.0)
                    && (player.getLinearVelocity().x == -1.6f || playerData.skipImpulse)
                    && playerData.lastPos.x-player.getPosition().x == 0
                    && playerContactPoints > 0)
            {
                // Resets X Axis Velocity to 0 to Prevent Repeated Wall Collisions and Updates SkipImpulse to Skip the Next Impulse
                player.setLinearVelocity(0, vel.y);
                playerData.skipImpulse = true;
            }
            else
            {
                // Otherwise it Resets the Wall Count Hit and Updates skipImpulse to false
                playerData.skipImpulse = false;
                playerData.wallHitCount.set(0,0);
            }


        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            // Resets Left Wall Hit Count
            playerData.wallHitCount.x = 0;

            // Updates Character State / Direction
            playerData.setCharacterState(Character.CharacterState.RUNNING);
            playerData.directionState = Entity.DirectionState.RIGHT;

            // Gives the Player a Nudge to the Right
            if (vel.x < MAX_VELOCITY.x && !playerData.skipImpulse)
                player.applyLinearImpulse(0.80f, 0, pos.x, pos.y, true);

            /*
            Checks for (Downwards Velocity or Repeated Wall Hits + Downward Velocity)
            and For (A Specific Linear Velocity or Repeated Touches)
            and For A Change in Position
            and For Player Contact
            To determine wether Contact with Wall has been Made
             */

            if (((vel.y < 0.0)|(playerData.wallHitCount.y++>3 && vel.y<0.1))
                    && (player.getLinearVelocity().x == 1.6f || playerData.skipImpulse)
                    && playerData.lastPos.x-player.getPosition().x == 0
                    && playerContactPoints > 0 )
            {
                // Resets X Axis Velocity to 0 to Prevent Repeated Wall Collisions and Updates SkipImpulse to Skip the Next Impulse
                player.setLinearVelocity(0, vel.y);
                playerData.skipImpulse = true;

            }
            else
            {
                // Otherwise it Resets the Wall Count Hit and Updates skipImpulse to false
                playerData.skipImpulse = false;
                playerData.wallHitCount.set(0,0);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            // Sets Character State to Jumping
            playerData.setCharacterState(Character.CharacterState.JUMPING);

            // Gives the Character an Upwards Nudge
            if( vel.y < MAX_VELOCITY.y && Math.abs(vel.y) < 0.005)
                player.applyLinearImpulse(0, 4f, pos.x, pos.y, true);

        }

        // Checks to see if the Player is Simply Standing and Resets Wall Hit Count
        if(player.getLinearVelocity().x == 0 && player.getLinearVelocity().y == 0)
            playerData.wallHitCount.set(0,0);

        // Stores the Players Current ( last after iteration ) Position in the Player Data
        playerData.lastPos = player.getPosition();

        //Testing Code
        //System.out.println(player.getLinearVelocity()+ ":" +player.getWorld().getContactCount());




    }
    /*
    Renders Entities . Can be placed in a Loop to Render multiple Entities .
    Uses the Entity Body and Change in Time

     */
    private void renderEntity(Body item,float deltaTime){
        // Gets the Actual Entity Class from the Body
        Entity entity = (Entity) item.getUserData();
        // Box 2D position and Pixel Position are very Different :(
        Vector2 entityPos = entity.getPixelPosition( item.getPosition() , camera, worldManager.mapTileSize);

        // Draws the Sprite / Texture at the Right Position and Scales to Entity's Dimesions
        game.batch.begin();
        game.batch.draw(entity.getCurrentSprite(deltaTime),entityPos.x,entityPos.y,(int)entity.dimension.x,(int)entity.dimension.y);
        game.batch.end();
    }



    @Override
    public void dispose() {
        worldManager.dispose();
    }
}