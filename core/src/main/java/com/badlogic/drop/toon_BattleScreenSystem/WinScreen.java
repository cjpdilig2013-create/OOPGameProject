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

public class WinScreen extends ScreenAdapter {

    private final Game game;
    private SpriteBatch batch;
    private Texture background;
    private Music victoryMusic;
    private Sound buttonSound;

    private boolean leaving = false;
    private boolean restarting = false;
    private float leaveTimer = 0f;

    public WinScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("ui/win_screen.png"));
        background.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("music/victory.mp3"));
        victoryMusic.setLooping(true);
        victoryMusic.setVolume(1f);
        victoryMusic.play();

        buttonSound = Gdx.audio.newSound(Gdx.files.internal("effect/ButtonSoundEffect.mp3"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.getProjectionMatrix().setToOrtho2D(0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.draw(background, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (!leaving) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                restarting = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
                leaving = true;
                leaveTimer = 0.5f;
                victoryMusic.stop();
                buttonSound.play(0.6f);
            }
        } else {
            leaveTimer -= delta;
            if (leaveTimer <= 0f) {
                if (restarting) {
                    game.setScreen(new BattleScreen(game));
                    dispose();
                } else {
                    Gdx.app.exit();
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (background != null) background.dispose();
        if (victoryMusic != null) victoryMusic.dispose();
        if (buttonSound != null) buttonSound.dispose();
    }
}
