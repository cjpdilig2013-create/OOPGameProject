package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoseScreen extends ScreenAdapter {
    private final Game game;
    private SpriteBatch batch;
    private Texture background;
    private Music gameOverMusic;
    private Sound selectionSound;

    private boolean leaving;
    private boolean restarting;
    private float leaveTimer;
    private static final float SELECTION_DELAY = 0.5f;

    public LoseScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("ui/lose_screen.png"));
        background.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Same selection sound as BattleScreen.
        try {
            selectionSound = Gdx.audio.newSound(
                Gdx.files.internal("effect/ButtonSoundEffect.mp3"));
            Gdx.app.log("LoseScreen", "Selection ding loaded: effect/ButtonSoundEffect.mp3");
        } catch (Exception e) {
            Gdx.app.error("LoseScreen", "Failed to load selection ding", e);
        }

        // Exact location shown in your IntelliJ asset folder.
        try {
            if (!Gdx.files.internal("music/gameover.mp3").exists()) {
                Gdx.app.error("LoseScreen", "Missing assets/music/gameover.mp3");
            } else {
                gameOverMusic = Gdx.audio.newMusic(
                    Gdx.files.internal("music/gameover.mp3"));
                gameOverMusic.setLooping(true);
                gameOverMusic.setVolume(1f);
                gameOverMusic.play();
                Gdx.app.log("LoseScreen", "Game-over music started: " + gameOverMusic.isPlaying());
            }
        } catch (Exception e) {
            Gdx.app.error("LoseScreen", "Failed to play music/gameover.mp3", e);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.getProjectionMatrix().setToOrtho2D(
            0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.draw(background, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (leaving) {
            leaveTimer -= delta;
            if (leaveTimer <= 0f) {
                if (restarting) {
                    if (gameOverMusic != null) gameOverMusic.stop();
                    game.setScreen(new BattleScreen(game));
                    dispose();
                } else {
                    Gdx.app.exit();
                }
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            beginLeave(true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            beginLeave(false);
        }
    }

    private void beginLeave(boolean restartBattle) {
        leaving = true;
        restarting = restartBattle;
        leaveTimer = SELECTION_DELAY;
        if (gameOverMusic != null) gameOverMusic.setVolume(0.2f);
        if (selectionSound != null) {
            selectionSound.play(1f);
            Gdx.app.log("LoseScreen", "Selection ding played");
        }
    }

    @Override
    public void dispose() {
        if (gameOverMusic != null) {
            gameOverMusic.stop();
            gameOverMusic.dispose();
            gameOverMusic = null;
        }
        if (selectionSound != null) {
            selectionSound.dispose();
            selectionSound = null;
        }
        if (background != null) {
            background.dispose();
            background = null;
        }
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
    }
}
