package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

// ENEMY ASSETS: loads and owns images and sounds for all three rounds.
// Load files once, then change which references are used when the round changes.
public class EnemyAssets {
    private final Texture garlicBackground, tungBackground, calcBackground;
    private final Texture insecureSword;
    private final Texture[] garlicSprites, tungSprites, calcSprites;
    private final Music garlicMusic, tungMusic, calcMusic;
    private final Sound garlicAttackSound, tungAttackSound, calcAttackSound, enemyDefeatedSound;
    private Music battleMusic;

    public EnemyAssets() {
        // BACKGROUND IMAGES
        // Assets are loaded relative to the assets/ folder, not the Java file.
        garlicBackground = new Texture("backgrounds/battle-background.png");
        tungBackground = new Texture("backgrounds/battle-background-tung.png");
        // ROUND 3 BACKGROUND: use the new Calculator battlefield.
        calcBackground = new Texture("backgrounds/battle-background-calc.png");
        insecureSword = new Texture("creatures/swordstand.png");

        // Sprite order: 0 = idle, 1 = attack, 2 = hurt, 3 = dead.
        // Keep the original filenames so no PNG needs renaming.
        garlicSprites = new Texture[] {
            new Texture("enemies/garlic_idle.png"),
            new Texture("enemies/garlic_attack.png"),
            new Texture("enemies/garlic_hurt.png"),
            new Texture("enemies/garlic_dead.png")
        };
        tungSprites = new Texture[] {
            new Texture("enemies/tung_tung-idle.png"),
            new Texture("enemies/tung_tung-attack.png"),
            new Texture("enemies/tung_tung-hurt.png"),
            new Texture("enemies/tung_tung-dead.png")
        };
        // ROUND 3 CALCULATOR SPRITES: exact existing filenames in assets/enemies.
        calcSprites = new Texture[] {
            new Texture("enemies/calc-idle.png"),
            new Texture("enemies/calc-attack.png"),
            new Texture("enemies/calc-hurt.png"),
            new Texture("enemies/calc-dead.png")
        };
        // Keep pixel art sharp when resized rather than blurring pixels.
        for (Texture t : garlicSprites) setNearest(t);
        for (Texture t : tungSprites) setNearest(t);
        for (Texture t : calcSprites) setNearest(t);
        setNearest(insecureSword);

        // MUSIC: use Music rather than Sound for long background loops.
        garlicMusic = Gdx.audio.newMusic(Gdx.files.internal("music/garlic-battlemusic.mp3"));
        tungMusic = Gdx.audio.newMusic(Gdx.files.internal("music/tung-battlemusic.mp3"));
        calcMusic = Gdx.audio.newMusic(Gdx.files.internal("music/calc-battlemusic.mp3"));
        garlicMusic.setLooping(true);
        tungMusic.setLooping(true);
        calcMusic.setLooping(true);
        garlicMusic.setVolume(0.35f);
        tungMusic.setVolume(0.35f);
        calcMusic.setVolume(0.35f);

        // SOUND: short effects played only when a move/defeat occurs.
        garlicAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/garlic-attack.mp3"));
        tungAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tung-attack.mp3"));
        calcAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/calc-attack.mp3"));
        enemyDefeatedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemydefeated-1.mp3"));
    }

    private void setNearest(Texture sprite) {
        sprite.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    // These getters give the renderer a current image without transferring ownership.
    // Choose the matching background for each round.
    public Texture getBackground(int round) {
        if (round == 1) return garlicBackground;
        if (round == 2) return tungBackground;
        if (round == 3) return calcBackground;
        throw new IllegalArgumentException("Unknown round: " + round);
    }
    public Texture getSword() { return insecureSword; }
    public Texture getIdle(int round) { return sprite(round, 0); }
    public Texture getAttack(int round) { return sprite(round, 1); }
    public Texture getHurt(int round) { return sprite(round, 2); }
    public Texture getDead(int round) { return sprite(round, 3); }
    private Texture sprite(int round, int pose) {
        if (round == 1) return garlicSprites[pose];
        if (round == 2) return tungSprites[pose];
        if (round == 3) return calcSprites[pose];
        throw new IllegalArgumentException("Unknown round: " + round);
    }

    // SWITCH AUDIO: stop current loop before playing the new round's music.
    public void playMusic(int round) {
        stopMusic();
        if (round == 1) battleMusic = garlicMusic;
        else if (round == 2) battleMusic = tungMusic;
        else if (round == 3) battleMusic = calcMusic;
        else throw new IllegalArgumentException("Unknown round: " + round);
        battleMusic.play();
    }
    public void stopMusic() { if (battleMusic != null) battleMusic.stop(); }
    public void playEnemyAttack(int round) {
        if (round == 1) garlicAttackSound.play(0.7f);
        else if (round == 2) tungAttackSound.play(0.7f);
        else if (round == 3) calcAttackSound.play(0.7f);
    }
    public void playEnemyDefeated() { enemyDefeatedSound.play(0.6f); }

    // Dispose EACH unique resource exactly once. Background/current sprite
    // references in other classes must NOT also dispose these textures.
    public void dispose() {
        stopMusic();
        garlicBackground.dispose();
        tungBackground.dispose();
        calcBackground.dispose();
        insecureSword.dispose();
        for (Texture t : garlicSprites) t.dispose();
        for (Texture t : tungSprites) t.dispose();
        for (Texture t : calcSprites) t.dispose();
        garlicAttackSound.dispose();
        tungAttackSound.dispose();
        calcAttackSound.dispose();
        enemyDefeatedSound.dispose();
        garlicMusic.dispose();
        tungMusic.dispose();
        calcMusic.dispose();
    }
}
