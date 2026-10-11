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
// Sword idle animation



// BATTLE RENDERER: only draws the current battle. It does NOT calculate damage
// or decide whose turn it is. This keeps graphics separate from game rules.
public class BattleRenderer {
    // These GPU resources belong to the renderer. Dispose them exactly once.
    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final BitmapFont font = new BitmapFont();
    private final BitmapFont dialogueFont = new BitmapFont();
    private DialogueBox dialogueBox;
    private final Texture airSlashTexture;
    private float swordFloatTimer = 0f;

    private static final float FLOAT_RADIUS = 12f;
    private static final float FLOAT_SPEED = 2f;

    private float swordAnimationTimer = 0f;
    private static final float SWORD_FLOAT_HEIGHT = 8f;
    private static final float SWORD_FLOAT_SPEED = 2.5f;
    private static final float SHADOW_FLOAT_SPEED = 2.0f;
    private static final float SHADOW_FLOAT_HEIGHT = 12f;

    // Shadow sword effect
    private static final float SHADOW_ALPHA = 0.22f;
    private static final float SHADOW_OFFSET_X = -5f;
    private static final float SHADOW_OFFSET_Y = -4f;

    private Texture shadowSword;

    public BattleRenderer() {
        dialogueFont.setColor(Color.BLACK);

        airSlashTexture = new Texture(
            Gdx.files.internal("effect/airslash 01.png")
        );


        updateDialoguePosition(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
    }

    // ONE rendering entry point shared by Garlic Man and Tung Tung Man.
    // The screen supplies current textures, HP, round number, and message.
    public void render(EnemyAssets assets, Texture currentSprite,
                       PlayerCreature player, Enemy enemy, int round,
                       String battleMessage,
                       boolean showPlayer, boolean showAirSlash,
                       float enemyAlpha) {
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
        // Enemy HP bar position
        float hpBarWidth = enemySize;
        float hpBarHeight = Math.max(8f, screenHeight * 0.018f);
        float hpBarX = enemyX;
        float hpBarY = enemyY + enemySize + 10f;
        swordFloatTimer += Gdx.graphics.getDeltaTime();

        float floatX = (float) Math.cos(swordFloatTimer * FLOAT_SPEED)
            * FLOAT_RADIUS;

        float floatY = (float) Math.sin(swordFloatTimer * FLOAT_SPEED)
            * FLOAT_RADIUS;
        batch.begin();

        //Draw background first
        batch.draw(background, bgX, bgY, bgWidth, bgHeight);

        // ENEMY SPRITE: currentSprite changes between idle, attack, hurt and dead.
        // The bigger enemySize value makes Tung Tung visibly larger in round 2.
        // Draw the enemy with temporary transparency.
        batch.setColor(1f, 1f, 1f, enemyAlpha);
        batch.draw(currentSprite, enemyX, enemyY, enemySize, enemySize);
        batch.setColor(Color.WHITE);

// Draw Air Slash over the enemy during the player's attack.
        if (showAirSlash) {
            float slashHeight = enemySize * 1.15f;
            float slashWidth = slashHeight
                * airSlashTexture.getWidth()
                / airSlashTexture.getHeight();

            float slashX = enemyX + (enemySize - slashWidth) / 2f;
            float slashY = enemyY + (enemySize - slashHeight) / 2f;

            batch.draw(
                airSlashTexture,
                slashX,
                slashY,
                slashWidth,
                slashHeight
            );
        }

// PLAYER SPRITE: hide the player while Air Slash is active.
        float playerSize = screenHeight * 0.55f;

        float playerWidth = playerSize
            * insecureSword.getWidth()
            / insecureSword.getHeight();

        float playerX = screenWidth * 0.10f;
        float playerY = (screenHeight - playerSize) / 6.5f;


        //sword
        swordAnimationTimer += Gdx.graphics.getDeltaTime();

        float swordOffsetY =
            (float) Math.sin(swordAnimationTimer * SWORD_FLOAT_SPEED)
                * SWORD_FLOAT_HEIGHT;
        // เงาแต่ละชั้นเคลื่อนไหวเหลื่อมจังหวะกัน
        float shadowOffset1 =
            (float) Math.sin(swordAnimationTimer * SHADOW_FLOAT_SPEED + 0.4f)
                * SHADOW_FLOAT_HEIGHT * 0.25f;

        float shadowOffset2 =
            (float) Math.sin(swordAnimationTimer * SHADOW_FLOAT_SPEED + 0.8f)
                * SHADOW_FLOAT_HEIGHT * 0.40f;

        float shadowOffset3 =
            (float) Math.sin(swordAnimationTimer * SHADOW_FLOAT_SPEED + 1.2f)
                * SHADOW_FLOAT_HEIGHT * 0.55f;

        float shadowOffset4 =
            (float) Math.sin(swordAnimationTimer * SHADOW_FLOAT_SPEED + 1.6f)
                * SHADOW_FLOAT_HEIGHT * 0.70f;



            if (showPlayer) {
                float swordY = playerY + swordOffsetY;

                // เงาชั้นที่ 4: จางที่สุดและอยู่ห่างที่สุด
                batch.setColor(0f, 0f, 0f, 0.08f);
                batch.draw(
                    insecureSword,
                    playerX - 80f,
                    swordY - 14f + shadowOffset4,
                    playerWidth,
                    playerSize
                );

                // เงาชั้นที่ 3
                batch.setColor(0f, 0f, 0f, 0.12f);
                batch.draw(
                    insecureSword,
                    playerX - 60f,
                    swordY - 9f + shadowOffset3,
                    playerWidth,
                    playerSize
                );

                // เงาชั้นที่ 2
                batch.setColor(0f, 0f, 0f, 0.17f);
                batch.draw(
                    insecureSword,
                    playerX - 40f,
                    swordY - 5f + shadowOffset2,
                    playerWidth,
                    playerSize
                );

                // เงาชั้นที่ 1: ใกล้ดาบหลักที่สุด
                batch.setColor(0f, 0f, 0f, 0.22f);
                batch.draw(
                    insecureSword,
                    playerX - 20f,
                    swordY - 2f + shadowOffset1,
                    playerWidth,
                    playerSize
                );

                // ดาบหลัก
                batch.setColor(Color.WHITE);
                batch.draw(
                    insecureSword,
                    playerX,
                    swordY,
                    playerWidth,
                    playerSize
                );
            }




// จบการวาด Sprite ทั้งหมดก่อนใช้ ShapeRenderer
        batch.end();

// ตั้งค่าพิกัดของ ShapeRenderer ให้ตรงกับ SpriteBatch
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

// วาด Enemy HP bar
        drawHpBar(
            hpBarX,
            hpBarY,
            hpBarWidth,
            hpBarHeight,
            enemy.getCurrentHp(),
            enemy.getMaxHp()
        );

// วาด Player HP bar
        float playerHpX = playerX;
        float playerHpY = playerY + playerSize + 10f;

        drawHpBar(
            playerHpX,
            playerHpY,
            playerWidth,
            hpBarHeight,
            player.getCurrentHp(),
            player.getMaxHp()
        );

        dialogueBox.setText(battleMessage);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        dialogueBox.drawShapes(shapeRenderer);
        shapeRenderer.end();

        // Draw dialogue text
        batch.begin();

        float dialogueScale = screenHeight / 720f * 2.0f;
        dialogueScale = Math.max(1.0f, Math.min(dialogueScale, 3.0f));

        dialogueFont.getData().setScale(dialogueScale);
        dialogueBox.drawText(batch, dialogueFont);

        batch.end();

        // Draw HP bars

        // Draw attack menu last
        drawAttackMenu();


    }


    private void drawHpBar(float x, float y,
                           float width, float height,
                           int currentHp, int maxHp) {

        if (maxHp <= 0 || width <= 0 || height <= 0) {
            return;
        }

        // Clamp HP to a valid range
        int hp = Math.max(0, Math.min(currentHp, maxHp));

        // Each slot represents up to 10 HP
        int slotCount = (maxHp + 9) / 10;
        float gap = 0f;
        float slotWidth =
            (width - gap * (slotCount - 1)) / slotCount;

        float hpPercent = (float) hp / maxHp;

        // Choose the color based on remaining HP
        Color hpColor;
        if (hpPercent > 0.5f) {
            hpColor = Color.GREEN;
        } else if (hpPercent > 0.2f) {
            hpColor = Color.YELLOW;
        } else {
            hpColor = Color.RED;
        }

        // Draw the background and the filled HP inside each slot
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < slotCount; i++) {
            float slotX = x + i * (slotWidth + gap);

            // Dark background for an empty slot
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(slotX, y, slotWidth, height);

            // HP remaining in this slot
            int slotHp = Math.max(
                0,
                Math.min(10, hp - i * 10)
            );

            // The final slot may represent fewer than 10 HP
            int slotCapacity = Math.min(10, maxHp - i * 10);

            if (slotHp > 0 && slotCapacity > 0) {
                float inset = Math.min(1.5f, height * 0.08f);
                float fillWidth =
                    (slotWidth - inset * 2f)
                        * slotHp / slotCapacity;

                shapeRenderer.setColor(hpColor);
                shapeRenderer.rect(
                    slotX + inset,
                    y + inset,
                    fillWidth,
                    height - inset * 2f
                );
            }
        }

        shapeRenderer.end();

        // Draw a black outline around every slot
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        for (int i = 0; i < slotCount; i++) {
            float slotX = x + i * (slotWidth + gap);
            shapeRenderer.rect(slotX, y, slotWidth, height);
        }

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
        airSlashTexture.dispose();
    }
}
