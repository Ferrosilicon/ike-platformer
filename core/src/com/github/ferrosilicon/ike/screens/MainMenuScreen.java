package com.github.ferrosilicon.ike.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.ferrosilicon.ike.IkeGame;

// The screen when you first start the game
public final class MainMenuScreen extends ScreenAdapter {

    private final IkeGame game;
    private final OrthographicCamera camera;

    public MainMenuScreen(final IkeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        // Sets the screen's background color
        Gdx.gl.glClearColor(0, 0, 0, 1);
        // Clears the screen with the background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Updates the camera
        camera.update();
        // Projects the batch with the camera
        game.batch.setProjectionMatrix(camera.combined);
        // Begins the batch to begin drawing
        game.batch.begin();
        // Draws strings using the font to the batch at the specified coordinates
        game.font.draw(game.batch, "Welcome to Iron Ike! ", 100, 150);
        game.font.draw(game.batch, "Need to create options and such here! Tap to begin!", 100, 100);
        // Ends the sprite batch, rendering it
        game.batch.end();

        // If the screen is touched change the screen to the in-game screen
        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }
}
