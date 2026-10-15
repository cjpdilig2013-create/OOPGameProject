package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

//Everything one enemy needs to look and sound right: background, 4 sprites, music, attack sound and size
//EnemyAssets keeps a list of these, one for each round
public class EnemyAssetSet {
    private final Texture background;
    private final Texture[] sprites;
    private final Music music;
    private final Sound attackSound;
    private final float scale;

    public EnemyAssetSet(String backgroundPath, String[] spritePaths, String musicPath,
                         String attackSoundPath, float scale) {
        background = new Texture(backgroundPath);

        sprites = new Texture[spritePaths.length];
        for (int i = 0; i < spritePaths.length; i++) {
            sprites[i] = new Texture(spritePaths[i]);
            //Nearest increases the scale for the enemies so that the states aren't blurred
            sprites[i].setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        music = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        music.setLooping(true);
        music.setVolume(0.35f);

        attackSound = Gdx.audio.newSound(Gdx.files.internal(attackSoundPath));
        this.scale = scale;
    }

    public Texture getBackground() {
        return background;
    }

    //pose: 0 = Idle, 1 = Attack, 2 = Hurt, 3 = Dead
    public Texture getSprite(int pose) {
        return sprites[pose];
    }

    public Music getMusic() {
        return music;
    }

    public void playAttackSound() {
        attackSound.play(0.7f);
    }

    //Enemy size as a fraction of the window height
    public float getScale() {
        return scale;
    }

    public void dispose() {
        background.dispose();
        for (Texture t : sprites) t.dispose();
        attackSound.dispose();
        music.dispose();
    }
}
