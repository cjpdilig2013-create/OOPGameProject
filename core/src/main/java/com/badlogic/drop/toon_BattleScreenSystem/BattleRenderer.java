package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;
import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;

// BATTLE RENDERER: only draws the current battle. It does NOT calculate damage
// or decide whose turn it is. This keeps graphics separate from game rules.
public class BattleRenderer {
    // These GPU resources belong to the renderer. Dispose them exactly once.
    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final BitmapFont font = new BitmapFont();
    private final BitmapFont dialogueFont = new BitmapFont();
    private DialogueBox dialogueBox;

    public BattleRenderer() {
        dialogueFont.setColor(Color.BLACK);
        updateDialoguePosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    // ONE rendering entry point shared by Garlic Man and Tung Tung Man.
    // The screen supplies current textures, HP, round number, and message.
    public void render(EnemyAssets assets, Texture currentSprite,
                       PlayerCreature player, Enemy enemy, int round,
                       String battleMessage) {
        Texture background = assets.getBackground(round);
        Texture insecureSword = assets.getSword();
        // RENDERING OVERVIEW
        // 1) Clear old pixels; 2) scale/draw the active background;
        // 3) draw both creatures; 4) draw both HP bars;
        // 5) draw the persistent top-left dialogue and bottom-right attack menu.
        // The order matters: later drawings appear on top of earlier drawings.
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

        // ENEMY SIZE AND POSITION
        // Use a larger square for Tung Tung without touching or stretching the source PNG.
        // Round 1 = 35%; Round 2 = 43%; Round 3 = 40% of window height.
        // Changing this one multiplier makes Tung Tung bigger/smaller.
        float enemyScale = (round == 2) ? 0.43f : (round == 3 ? 0.40f : 0.35f);
        float enemySize = screenHeight * enemyScale;

        // Keep each enemy in the top-right portion of the screen.
        // The X expression centers the sprite around 70% of the screen width.
        float enemyX = screenWidth * 0.70f - enemySize / 2f;
        // Same vertical base for both rounds; the larger Tung Tung grows upward.
        float enemyY = screenHeight * 0.48f;

        batch.begin();

        //Draw background first
        batch.draw(background, bgX, bgY, bgWidth, bgHeight);

        // ENEMY SPRITE: currentSprite changes between idle, attack, hurt and dead.
        // The bigger enemySize value makes Tung Tung visibly larger in round 2.
        batch.draw(currentSprite, enemyX, enemyY, enemySize, enemySize);

        // PLAYER SPRITE: the Insecure Sword remains the same in both rounds.
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

        // ENEMY HP BAR: position directly above whichever enemy is active.
        // Its width tracks enemySize, so the bar grows with Tung Tung.
        float hpBarWidth = enemySize;
        float hpBarHeight = Math.max(8f, screenHeight * 0.018f);

        float hpBarX = enemyX;
        float hpBarY = enemyY + enemySize + 10f;

// Draw Garlic Man's current HP
        drawHpBar(
            hpBarX,
            hpBarY,
            hpBarWidth,
            hpBarHeight,
            enemy.getCurrentHp(),
            enemy.getMaxHp()
        );
        // Display the player's remaining HP; enemyTurn() updates this through Battle.
        float playerHpX = playerX;
        float playerHpY = playerY + playerSize + 10f;
        drawHpBar(playerHpX, playerHpY, playerWidth, hpBarHeight,
            player.getCurrentHp(), player.getMaxHp());

        // Keep the latest message and both remaining HP values visible until the next action.
        dialogueBox.setText(battleMessage);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        dialogueBox.drawShapes(shapeRenderer);
        shapeRenderer.end();

        batch.begin();
        // DIALOGUE FONT: make the battle dialogue noticeably larger.
        // At 720p the font is 2.5x the default size; on larger windows it grows
        // slightly more (up to 3.0x). The dialogue box itself is unchanged.
        // The attack menu uses a separate font, so this will not resize the buttons.
        float dialogueScale = Math.max(2f, Math.min(2.5f, screenHeight / 720f * 2.5f));
        dialogueFont.getData().setScale(dialogueScale);
        dialogueBox.drawText(batch, dialogueFont);
        batch.end();

        drawAttackMenu();
    }

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


    //RESIZING: Keep drawing coordinates matched to the window.
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        updateDialoguePosition(width, height);
    }

    // DIALOGUE LOCATION: LibGDX coordinates start at bottom-left, so subtracting
    // the box height from screenHeight places the box at the TOP-left corner.
    private void updateDialoguePosition(int screenWidth, int screenHeight) {
        float width = screenWidth * 0.47f;
        float height = screenHeight * 0.19f;
        dialogueBox = new DialogueBox(20f, screenHeight - height - 20f, width, height);
    }

    // ATTACK MENU: draw four visible placeholders in a 2x2 grid.
    // For this prototype only key 1 triggers the actual player move;
    // the other keyboard numbers are still sprite testing shortcuts.
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

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        dialogueFont.dispose();
        batch.dispose();
    }
}
