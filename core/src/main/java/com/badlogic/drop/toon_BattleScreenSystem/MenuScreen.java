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

// MenuScreen = the first screen of the game (title screen).
// - Background is an animation: the images in BACKGROUND_FILES are shown one after another, in a loop.
// - Music plays in a loop.
// - Press ENTER to go to BattleScreen.
// It is built the same way as WinScreen.
public class MenuScreen extends ScreenAdapter {

    // ====== CHANGE THESE to match your files (inside the "assets" folder) ======
    private static final String[] BACKGROUND_FILES = {
        "ui/menu1-background.png",
        "ui/menu2-background.png"
    };
    private static final String MUSIC_FILE = "music/Menu-song.mp3";
    private static final String BUTTON_SOUND_FILE = "effect/ButtonSoundEffect.mp3";

    // How long each image stays on screen (seconds). Smaller = faster animation.
    private static final float FRAME_TIME = 0.5f;
    // ===========================================================================

    private final Game game;
    private SpriteBatch batch;
    private Texture[] frames;
    private Music menuMusic;
    private Sound buttonSound;

    // Animation
    private int currentFrame = 0;      // which image is showing now
    private float frameTimer = 0f;     // counts up until it reaches FRAME_TIME

    // Leaving the menu (wait a short time so the button sound can play)
    private boolean leaving = false;
    private float leaveTimer = 0f;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Load every background image
        frames = new Texture[BACKGROUND_FILES.length];
        for (int i = 0; i < BACKGROUND_FILES.length; i++) {
            frames[i] = new Texture(Gdx.files.internal(BACKGROUND_FILES[i]));
            frames[i].setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_FILE));
        menuMusic.setLooping(true);
        menuMusic.setVolume(1f);
        menuMusic.play();

        buttonSound = Gdx.audio.newSound(Gdx.files.internal(BUTTON_SOUND_FILE));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateAnimation(delta);

        // Draw the current image to fill the whole window
        batch.getProjectionMatrix().setToOrtho2D(0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.draw(frames[currentFrame], 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (!leaving) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                leaving = true;
                leaveTimer = 0.5f;
                menuMusic.stop();
                buttonSound.play(0.6f);
            }
        } else {
            leaveTimer -= delta;
            if (leaveTimer <= 0f) {
                game.setScreen(new BattleScreen(game));
                dispose();
            }
        }
    }

    // Every FRAME_TIME seconds, switch to the next image (after the last one, go back to the first)
    private void updateAnimation(float delta) {
        frameTimer += delta;
        if (frameTimer >= FRAME_TIME) {
            frameTimer -= FRAME_TIME;
            currentFrame = (currentFrame + 1) % frames.length;
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (frames != null) {
            for (Texture frame : frames) {
                if (frame != null) frame.dispose();
            }
        }
        if (menuMusic != null) menuMusic.dispose();
        if (buttonSound != null) buttonSound.dispose();
    }
}
