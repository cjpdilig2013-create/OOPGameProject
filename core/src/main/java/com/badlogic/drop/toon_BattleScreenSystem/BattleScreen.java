
package com.badlogic.drop.toon_BattleScreenSystem;

//Garlic Man's Attack
import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;

//Drawing HP Bar
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.drop.cj_BattleSystem.Battle;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;
import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;

//Keyboard Controls
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
//Provides structure for game screen
import com.badlogic.gdx.ScreenAdapter;

//Holds an image into graphics memory
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

//OOP Inheritance where we are extending from ScreenAdapter
public class BattleScreen extends ScreenAdapter {

    //SpriteBatch allows as the draw the picture onto the game window
    private SpriteBatch batch;

    private BitmapFont font;
    private Texture background;
    //Stores a reference to the loaded Garlic Man image
    private Texture garlicIdle;
    private Texture garlicAttack;
    private Texture garlicHurt;
    private Texture garlicDead;
    private Texture insecureSword;
    //Store which sprite is currently displayed
    private Texture currentSprite;

    // Draws rectangles for our HP bars
    private ShapeRenderer shapeRenderer;

    //Tracks how many seconds remain before returning to Idle
    private float animationTimer = 0f;

    //Storing Liquid Cat, Garlic Man, and the battle system
    private PlayerCreature player;
    private Enemy enemy;
    private Battle battle;

    //POLYMORPHISM
    private Move splashHit;

    // Garlic Man's basic attack
    private Move garlicPunch;

    // Timer that delays the enemy's turn
    private float enemyTurnTimer = 0f;

    //Loading the images and it will then prepare the screen of garlic_idle (Our 1st enemy)
    @Override
    public void show() {

        batch = new SpriteBatch();
        // Create the shape drawing tool
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        //Loading the background
        background = new Texture("backgrounds/battle-background.png");

        insecureSword = new Texture("characters/swordstand.png");

        // Load Garlic Man's four sprites
        garlicIdle = new Texture("enemies/garlic_idle.png");
        garlicAttack = new Texture("enemies/garlic_attack.png");
        garlicHurt = new Texture("enemies/garlic_hurt.png");
        garlicDead = new Texture("enemies/garlic_dead.png");

        // Keep all sprites sharp when scaled
        Texture[] sprites = {
            garlicIdle, garlicAttack, garlicHurt, garlicDead
        };

        //For every texture stored in the sprites array, run the code inside the loop
        for (Texture sprite : sprites) {
            sprite.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );
        }

        //Garlic Man starts in the Idle state
        currentSprite = garlicIdle;


        //Create Liquid Cat with HP, Attack, Defense and Speed
        player = new PlayerCreature(
            "Liquid Cat",
            100,
            20,
            15,
            18
        );

    //Create Garlic Man with HP, Attack, Defense and Speed
        enemy = new Enemy(
            "Garlic Man",
            60,
            12,
            8,
            12
        );

    //Connect both creatures to CJ's battle system
        battle = new Battle(player, enemy);

    //Start the battle with the player's turn
        battle.startBattle();

        // Create Liquid Cat's basic attack
        splashHit = new DamageMove(
            "Splash Hit",
            10,
            "Liquid Cat splashes Garlic Man!"
        );

        // Garlic Man's attack
        garlicPunch = new DamageMove(
            "Garlic Punch",
            10,
            "Garlic Man punches Liquid Cat!"
        );

    }
    //Keeps drawing garlic man while the screen is active
    //We draw repeatedly because

    @Override
    public void render(float delta) {

        //Keyboard controls for testing Garlic Man's four states
        // Press 1 to make Liquid Cat attack Garlic Man
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {

            // Only attack if it is the player's turn
            if (battle.getTurnManager().isPlayerTurn()
                && !battle.isBattleOver()) {

                // Execute the attack using CJ's Battle System
                String result = battle.playerTurn(splashHit);

                // Show the attack result in IntelliJ's console
                System.out.println(result);

                // Display Garlic Man's remaining HP
                System.out.println(
                    "Garlic Man HP: " + enemy.getCurrentHp()
                );

                // Change the sprite depending on whether Garlic Man survived

                // If Garlic Man survives, play the Hurt animation
                if (enemy.isAlive()) {
                    currentSprite = garlicHurt;
                    animationTimer = 0.4f;

                    // Schedule Garlic Man's counterattack
                    enemyTurnTimer = 0.8f;

                } else {
                    // Garlic Man is defeated, so he cannot attack back
                    currentSprite = garlicDead;
                    animationTimer = 0f;
                    enemyTurnTimer = 0f;
                }

            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentSprite = garlicAttack;
            animationTimer = 0.4f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            currentSprite = garlicHurt;
            animationTimer = 0.4f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            currentSprite = garlicDead;
            animationTimer = 0.0f;
        }

        //Count down only when an animation is active
        if (animationTimer > 0f) {

            //Subtract the time that passed since the previous frame
            animationTimer -= delta;

            //Once time runs out, return to Idle
            if (animationTimer <= 0f) {
                currentSprite = garlicIdle;
                animationTimer = 0f;
            }
        }


// Wait before Garlic Man performs his counterattack
        if (enemyTurnTimer > 0f) {

            // Count down using the time between frames
            enemyTurnTimer -= delta;

            // Only attack once the timer reaches zero
            if (enemyTurnTimer <= 0f) {

                enemyTurnTimer = 0f;

                // Check that Garlic Man is alive and it is his turn
                if (enemy.isAlive()
                    && battle.getTurnManager().isEnemyTurn()
                    && !battle.isBattleOver()) {

                    // Execute Garlic Man's move using CJ's battle system
                    String result = battle.enemyTurn(garlicPunch);

                    System.out.println(result);

                    // Display Liquid Cat's remaining HP
                    System.out.println(
                        "Liquid Cat HP: " + player.getCurrentHp()
                    );

                    // Play Garlic Man's attack animation
                    currentSprite = garlicAttack;
                    animationTimer = 0.4f;
                }
            }
        }



        //Clear the previous frame
        ScreenUtils.clear(0.85f, 0.92f, 0.80f, 1f);

        //Get the current window dimensions once
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        //Scale background without changing its aspect ratio
        float scale = Math.max(
            screenWidth / background.getWidth(),
            screenHeight / background.getHeight()
        );

        float bgWidth = background.getWidth() * scale;
        float bgHeight = background.getHeight() * scale;

        //Center background; some edges may be cropped
        float bgX = (screenWidth - bgWidth) / 2;
        float bgY = (screenHeight - bgHeight) / 2;

        //Scale Garlic Man relative to the window height
        float garlicSize = screenHeight * 0.35f;

        //Keep Garlic Man toward the right side
        float garlicX = screenWidth * 0.70f - garlicSize / 2;
        float garlicY = screenHeight * 0.48f;

        batch.begin();

        //Draw background first
        batch.draw(background, bgX, bgY, bgWidth, bgHeight);

        //Draw Garlic Man on top



        //draw sword
        batch.draw(currentSprite, garlicX, garlicY,
            garlicSize, garlicSize);
        float playerSize = screenHeight * 0.55f;

        float playerWidth = playerSize
            * insecureSword.getWidth()
            / insecureSword.getHeight();

        float playerX = screenWidth * 0.10f;
        float playerY = (screenHeight - playerSize) / 6.5f;

        batch.draw(
            insecureSword,
            playerX,
            playerY,
            playerWidth,
            playerSize
        );
        batch.end();

        // Match ShapeRenderer's coordinates to SpriteBatch
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

// Position HP bar above Garlic Man
        float hpBarWidth = garlicSize;
        float hpBarHeight = Math.max(8f, screenHeight * 0.018f);

        float hpBarX = garlicX;
        float hpBarY = garlicY + garlicSize + 10f;

// Draw Garlic Man's current HP
        drawHpBar(
            hpBarX,
            hpBarY,
            hpBarWidth,
            hpBarHeight,
            enemy.getCurrentHp(),
            enemy.getMaxHp()
        );
        drawAttackMenu();
    }
    //123


    private void drawHpBar(float x, float y,
                           float width, float height,
                           int currentHp, int maxHp) {

        // Calculate how full the HP bar should be
        float hpPercent = Math.max(0f, Math.min(1f,
            (float) currentHp / maxHp
        ));

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the dark background of the HP bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, width, height);

        // Change the HP bar color depending on remaining HP
        if (hpPercent > 0.5f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (hpPercent > 0.2f) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        // Draw the remaining HP as a filled rectangle
        shapeRenderer.rect(x, y, width * hpPercent, height);

        shapeRenderer.end();
    }

    //Resizing it so that it isnt weird when we fullscreen
    @Override
    public void resize(int width, int height) {

        //Update our drawing coordinates when the window is resized.
        batch.getProjectionMatrix().setToOrtho2D(
            0, 0, width, height
        );
    }

    //Disposes of the graphics when we are done
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        background.dispose();

        garlicIdle.dispose();
        garlicAttack.dispose();
        garlicHurt.dispose();
        garlicDead.dispose();
        insecureSword.dispose();
        font.dispose();
        batch.dispose();
    }
        private void drawAttackMenu() {
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            float fontScale = screenHeight / 720f* 2.5f;
            fontScale = Math.max(0.8f, Math.min(fontScale, 1000.0f));
            font.setColor(Color.BLACK);
            font.getData().setScale(fontScale);

            float menuWidth = screenWidth * 0.55f;
            float menuHeight = screenHeight * 0.22f;
            float menuX = screenWidth - menuWidth - 20f;
            float menuY = 20f;

            float gap = 10f;
            float cellWidth = (menuWidth - gap * 3f) / 2f;
            float cellHeight = (menuHeight - gap * 3f) / 2f;

            // Draw translucent white background
            Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA
            );

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 1f, 1f, 0.35f);
            shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);

            shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);

            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    float x = menuX + gap + col * (cellWidth + gap);
                    float y = menuY + gap + row * (cellHeight + gap);

                    shapeRenderer.rect(x, y, cellWidth, cellHeight);
                }
            }

            shapeRenderer.end();
            Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

            // วาดข้อความในแต่ละช่อง
            batch.begin();

            font.setColor(Color.BLACK);
            font.getData().setScale(fontScale);


            font.draw(batch, "1. Splash Hit",
                menuX + gap + 8f,
                menuY + gap + cellHeight + gap + cellHeight / 2f + 5f);

            font.draw(batch, "2. Attack 2",
                menuX + gap + cellWidth + gap + 8f,
                menuY + gap + cellHeight + gap + cellHeight / 2f + 5f);

            font.draw(batch, "3. Attack 3",
                menuX + gap + 8f,
                menuY + gap + cellHeight / 2f + 5f);

            font.draw(batch, "4. Attack 4",
                menuX + gap + cellWidth + gap + 8f,
                menuY + gap + cellHeight / 2f + 5f);

            batch.end();
        }

    }

