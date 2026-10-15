package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;


//This is the Enemy Assets Tab: This is where we store all the backgrounds, sound effects (enemy and creature), and more below
//To add a new enemy, add ONE new line to the list in the constructor (round = place in the list)
public class EnemyAssets {
    private final List<EnemyAssetSet> enemies = new ArrayList<EnemyAssetSet>();
    private final Texture insecureSword;
    private final Sound enemyDefeatedSound;
    private Music battleMusic;

    //Special sprites that an enemy only shows while using one of its special moves
    private final Texture mathmindSprite;
    private final Texture mindBookSprite;
    private final Texture heavyNekSprite;
    private final Texture mindNekSprite;

    public EnemyAssets() {
        //One entry per enemy. Order = round number.
        //Sprites for the Enemies (4 states: Idle, Attack, Hurt, Dead)
        //Background music for the enemies (All the same until the final battle)
        //The last number is the enemy size as a fraction of the window height
        enemies.add(new EnemyAssetSet("backgrounds/garlic-background.png",
            poses("enemies/garlic_idle.png", "enemies/garlic_attack.png",
                "enemies/garlic_hurt.png", "enemies/garlic_dead.png"),
            "music/EnemyApproaching-TobyFox.mp3", "sounds/garlic-attack.mp3", 0.35f));

        enemies.add(new EnemyAssetSet("backgrounds/tung-background.png",
            poses("enemies/tung_tung-idle.png", "enemies/tung_tung-attack.png",
                "enemies/tung_tung-hurt.png", "enemies/tung_tung-dead.png"),
            "music/EnemyApproaching-TobyFox.mp3", "sounds/tung-attack.mp3", 0.43f));

        enemies.add(new EnemyAssetSet("backgrounds/calc-background.png",
            poses("enemies/calc-idle.png", "enemies/calc-attack.png",
                "enemies/calc-hurt.png", "enemies/calc-dead.png"),
            "music/EnemyApproaching-TobyFox.mp3", "sounds/calc-attack.mp3", 0.40f));

        enemies.add(new EnemyAssetSet("backgrounds/book-background.png",
            poses("enemies/book-idle.png", "enemies/book-attack.png",
                "enemies/book-hurt.png", "enemies/book-dead.png"),
            "music/EnemyApproaching-TobyFox.mp3", "sounds/book-attack.mp3", 0.42f));

        enemies.add(new EnemyAssetSet("backgrounds/nek-background.png",
            poses("enemies/nek-idle.png", "enemies/nek-attack.png",
                "enemies/nek-hurt.png", "enemies/nek-dead.png"),
            "music/nek-battlemusic.mp3", "sounds/nek-attack.mp3", 0.46f));

        insecureSword = new Texture("creatures/swordstand.png");
        insecureSword.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        mathmindSprite = loadSprite("enemies/Mathmind.png");
        // Special sprite used only when Angry Book activates its special move.
        mindBookSprite = loadSprite("enemies/MindBook.png");
        // Special sprite used when Nek activates Charge.
        heavyNekSprite = loadSprite("enemies/HeavyNek.png");
        // Special sprite used when Nek activates Math.
        mindNekSprite = loadSprite("enemies/MindNek.png");

        //Sound effect for when the enemy is defeated
        enemyDefeatedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemydefeated-1.mp3"));
    }

    private String[] poses(String idle, String attack, String hurt, String dead) {
        return new String[] {idle, attack, hurt, dead};
    }

    private Texture loadSprite(String path) {
        Texture texture = new Texture(path);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }

    //Finds the assets for a round (round 1 = first enemy in the list)
    private EnemyAssetSet getSet(int round) {
        if (round < 1 || round > enemies.size()) {
            throw new IllegalArgumentException("Unknown round: " + round);
        }
        return enemies.get(round - 1);
    }

    //Actually calling the background and implementing it using get
    public Texture getBackground(int round) {
        return getSet(round).getBackground();
    }

    public Texture getSword() { return insecureSword; }
    public Texture getIdle(int round) { return getSet(round).getSprite(0); }
    public Texture getAttack(int round) { return getSet(round).getSprite(1); }
    public Texture getHurt(int round) { return getSet(round).getSprite(2); }
    public Texture getDead(int round) { return getSet(round).getSprite(3); }

    public Texture getMathmind() { return mathmindSprite; }
    public Texture getMindBook() { return mindBookSprite; }
    public Texture getHeavyNek() { return heavyNekSprite; }
    public Texture getMindNek() { return mindNekSprite; }

    //Enemy size as a fraction of the window height (used by the BattleRenderer)
    public float getEnemyScale(int round) {
        return getSet(round).getScale();
    }

    //MUSIC SWITCHING: stop the last round's music before starting the next.
    public void playMusic(int round) {
        stopMusic();
        battleMusic = getSet(round).getMusic();
        battleMusic.play();
    }

    public void stopMusic() {
        if (battleMusic != null) battleMusic.stop();
    }

    //Play the signature attack sound for the active opponent.
    public void playEnemyAttack(int round) {
        getSet(round).playAttackSound();
    }

    public void playEnemyDefeated() {
        enemyDefeatedSound.play(0.6f);
    }

    //RESOURCE CLEANUP: dispose only what this class created.
    public void dispose() {
        stopMusic();
        for (EnemyAssetSet set : enemies) set.dispose();
        insecureSword.dispose();
        enemyDefeatedSound.dispose();
        mathmindSprite.dispose();
        mindBookSprite.dispose();
        heavyNekSprite.dispose();
        mindNekSprite.dispose();
    }
}
