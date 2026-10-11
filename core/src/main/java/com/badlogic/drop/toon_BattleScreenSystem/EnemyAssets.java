package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

// ENEMY ASSETS: owns all battle backgrounds, character sprites, music, and sounds.
// Load resources once; switch between them based on the current round.
public class EnemyAssets {
    private final Texture garlicBackground, tungBackground, calcBackground;
    private final Texture insecureSword;
    private final Texture[] garlicSprites, tungSprites, calcSprites, bookSprites, nekSprites;
    private final Music garlicMusic, tungMusic, calcMusic, bookMusic, nekMusic;
    private final Sound garlicAttackSound, tungAttackSound, calcAttackSound,
        bookAttackSound, nekAttackSound, enemyDefeatedSound;
    private Music battleMusic;

    public EnemyAssets() {
        // BACKGROUNDS: paths are relative to the assets folder.
        garlicBackground = new Texture("backgrounds/battle-background.png");
        tungBackground = new Texture("backgrounds/battle-background-tung.png");
        calcBackground = new Texture("backgrounds/battle-background-calc.png");
        insecureSword = new Texture("creatures/swordstand.png");

        // SPRITES: each set is ordered idle, attack, hurt, dead.
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
        calcSprites = new Texture[] {
            new Texture("enemies/calc-idle.png"),
            new Texture("enemies/calc-attack.png"),
            new Texture("enemies/calc-hurt.png"),
            new Texture("enemies/calc-dead.png")
        };
        // ROUND 4: Angry Book.
        bookSprites = new Texture[] {
            new Texture("enemies/book-idle.png"),
            new Texture("enemies/book-attack.png"),
            new Texture("enemies/book-hurt.png"),
            new Texture("enemies/book-dead.png")
        };
        // ROUND 5: Professor Boss uses the existing nek sprite set.
        nekSprites = new Texture[] {
            new Texture("enemies/nek-idle.png"),
            new Texture("enemies/nek-attack.png"),
            new Texture("enemies/nek-hurt.png"),
            new Texture("enemies/nek-dead.png")
        };

        // NEAREST FILTER: keeps pixel art sharp when scaled up.
        for (Texture t : garlicSprites) setNearest(t);
        for (Texture t : tungSprites) setNearest(t);
        for (Texture t : calcSprites) setNearest(t);
        for (Texture t : bookSprites) setNearest(t);
        for (Texture t : nekSprites) setNearest(t);
        setNearest(insecureSword);

        // MUSIC: long background music loops throughout its round.
        garlicMusic = Gdx.audio.newMusic(Gdx.files.internal("music/garlic-battlemusic.mp3"));
        tungMusic = Gdx.audio.newMusic(Gdx.files.internal("music/tung-battlemusic.mp3"));
        calcMusic = Gdx.audio.newMusic(Gdx.files.internal("music/calc-battlemusic.mp3"));
        bookMusic = Gdx.audio.newMusic(Gdx.files.internal("music/book-battlemusic.mp3"));
        nekMusic = Gdx.audio.newMusic(Gdx.files.internal("music/nek-battlemusic.mp3"));
        Music[] tracks = {garlicMusic, tungMusic, calcMusic, bookMusic, nekMusic};
        for (Music track : tracks) {
            track.setLooping(true);
            track.setVolume(0.35f);
        }

        // SOUND EFFECTS: short sounds played only on an attack or defeat.
        garlicAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/garlic-attack.mp3"));
        tungAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tung-attack.mp3"));
        calcAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/calc-attack.mp3"));
        bookAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/book-attack.mp3"));
        nekAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/nek-attack.mp3"));
        enemyDefeatedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemydefeated-1.mp3"));
    }

    private void setNearest(Texture texture) {
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    // BACKGROUNDS: Round 4 and 5 reuse Calculator's until you add new ones.
    public Texture getBackground(int round) {
        if (round == 1) return garlicBackground;
        if (round == 2) return tungBackground;
        if (round >= 3 && round <= 5) return calcBackground;
        throw new IllegalArgumentException("Unknown round: " + round);
    }

    public Texture getSword() { return insecureSword; }
    public Texture getIdle(int round) { return sprite(round, 0); }
    public Texture getAttack(int round) { return sprite(round, 1); }
    public Texture getHurt(int round) { return sprite(round, 2); }
    public Texture getDead(int round) { return sprite(round, 3); }

    // SPRITE GETTER: return the correct enemy pose without reloading textures.
    private Texture sprite(int round, int pose) {
        switch (round) {
            case 1: return garlicSprites[pose];
            case 2: return tungSprites[pose];
            case 3: return calcSprites[pose];
            case 4: return bookSprites[pose];
            case 5: return nekSprites[pose];
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }

    // MUSIC SWITCHING: stop the last round's music before starting the next.
    public void playMusic(int round) {
        stopMusic();
        switch (round) {
            case 1: battleMusic = garlicMusic; break;
            case 2: battleMusic = tungMusic; break;
            case 3: battleMusic = calcMusic; break;
            case 4: battleMusic = bookMusic; break;
            case 5: battleMusic = nekMusic; break;
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
        battleMusic.play();
    }

    public void stopMusic() {
        if (battleMusic != null) battleMusic.stop();
    }

    // Play the signature attack sound for the active opponent.
    public void playEnemyAttack(int round) {
        switch (round) {
            case 1: garlicAttackSound.play(0.7f); break;
            case 2: tungAttackSound.play(0.7f); break;
            case 3: calcAttackSound.play(0.7f); break;
            case 4: bookAttackSound.play(0.7f); break;
            case 5: nekAttackSound.play(0.7f); break;
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }

    public void playEnemyDefeated() {
        enemyDefeatedSound.play(0.6f);
    }

    // RESOURCE CLEANUP: dispose only what this class created.
    public void dispose() {
        stopMusic();
        garlicBackground.dispose();
        tungBackground.dispose();
        calcBackground.dispose();
        insecureSword.dispose();
        for (Texture t : garlicSprites) t.dispose();
        for (Texture t : tungSprites) t.dispose();
        for (Texture t : calcSprites) t.dispose();
        for (Texture t : bookSprites) t.dispose();
        for (Texture t : nekSprites) t.dispose();
        garlicAttackSound.dispose();
        tungAttackSound.dispose();
        calcAttackSound.dispose();
        bookAttackSound.dispose();
        nekAttackSound.dispose();
        enemyDefeatedSound.dispose();
        garlicMusic.dispose();
        tungMusic.dispose();
        calcMusic.dispose();
        bookMusic.dispose();
        nekMusic.dispose();
    }
}
