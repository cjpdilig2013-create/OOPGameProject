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
import com.badlogic.gdx.audio.Sound;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
    private Texture playerHitTexture;
    private float swordFloatTimer = 0f;
    private Texture shieldTexture;
    private com.badlogic.gdx.audio.Sound shieldSound;

    private static final float FLOAT_RADIUS = 12f;
    private static final float FLOAT_SPEED = 2f;

    private float swordAnimationTimer = 0f;
    private static final float SWORD_FLOAT_HEIGHT = 8f;
    private static final float SWORD_FLOAT_SPEED = 2.5f;
    private static final float SHADOW_FLOAT_SPEED = 2.0f;
    private static final float SHADOW_FLOAT_HEIGHT = 12f;

    // Sword buff pulse effect
    private float buffAnimationTimer = 0f;
    private static final float BUFF_ANIMATION_DURATION = 0.3f;
    private static final float BUFF_MAX_SCALE = 1.2f;
    // Red flash effect when using Buff
    private float redFlashTimer = 0f;
    private static final float RED_FLASH_DURATION = 0.25f;

    // Shadow sword effect
    private static final float SHADOW_ALPHA = 0.22f;
    private static final float SHADOW_OFFSET_X = -5f;
    private static final float SHADOW_OFFSET_Y = -4f;

    // NOTE attached to the sword
    private Texture[] noteTextures = new Texture[5];
    private int attachedNoteCount = 0;


    // Shield / Block
    private final Sound[] blockSounds = new Sound[3];
    private final Random random = new Random();

    // Heal
    private final Sound[] healSounds = new Sound[4];

    //heal animetion
    private Texture healTexture;
    private Animation<TextureRegion> healAnimation;
    private float healAnimationTime = 0f;
    private boolean playingHealAnimation = false;

    private Sound buffSound;
    private float enemyBuffScale = 1.0f;
    private float enemyRedFlashTimer = 0f;
    private static final float ENEMY_BUFF_DURATION = 0.5f;
    //choicce screen
    private String currentMenu = "MAIN_MENU";
    private int selectedChoice = 0;


    private Texture shadowSword;

    public BattleRenderer() {
        dialogueFont.setColor(Color.BLACK);

        airSlashTexture = new Texture(
            Gdx.files.internal("effect/airslash 01.png")
        );
        shieldTexture = new Texture(
            Gdx.files.internal("effect/shield8bit.png")
        );
        shieldSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/block02.mp3")
        );
        blockSounds[0] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/block01.mp3")
        );
        blockSounds[1] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/block03.mp3")
        );
        blockSounds[2] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/block04.mp3")
        );

        healSounds[0] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/heal01.mp3")
        );
        healSounds[1] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/heal02.mp3")
        );
        healSounds[2] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/heal03.mp3")
        );
        healSounds[3] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/heal04.mp3")
        );
        healTexture = new Texture(
            Gdx.files.internal("effect/heal_spritesheet.png")
        );
        buffSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/buff01.mp3")
        );
        playerHitTexture = new Texture(
            Gdx.files.internal("creatures/swordgethit.png")
        );

        for (int i = 0; i < noteTextures.length; i++) {
            String fileName = String.format(
                "effect/OrangeNote%02d.png",
                i + 1
            );

            noteTextures[i] = new Texture(
                Gdx.files.internal(fileName)
            );
        }


        TextureRegion[][] frames = TextureRegion.split(
            healTexture,
            200,
            200
        );

        TextureRegion[] healFrames = new TextureRegion[8];

        for (int i = 0; i < 8; i++) {
            healFrames[i] = frames[0][i];
        }

        healAnimation = new Animation<>(0.05f, healFrames);


        updateDialoguePosition(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
    }

    public boolean attachNoteToSword() {
        if (attachedNoteCount >= noteTextures.length) {
            return false;
        }

        attachedNoteCount++;
        return true;
    }

    public void removeNoteFromSword() {
        attachedNoteCount = 0;
    }


    public void playBuffAnimation() {
        buffAnimationTimer = BUFF_ANIMATION_DURATION;
        redFlashTimer = RED_FLASH_DURATION;
        // เล่นเสียง Buff
        if (buffSound != null) {
            buffSound.play(0.4f);
        }
    }


    public void playHealSound() {
        int index = random.nextInt(healSounds.length);

        if (healSounds[index] != null) {
            healSounds[index].play(0.8f);
        }
    }

    public void playBlockSound() {
        int index = random.nextInt(blockSounds.length);
        blockSounds[index].play(0.8f);
    }

    public void playShieldSound() {
        if (shieldSound != null) {
            shieldSound.play(0.8f);
        }
    }

    public void playHealAnimation() {
        healAnimationTime = 0f;
        playingHealAnimation = true;
    }
    public int getAttachedNoteCount() {
        return attachedNoteCount;
    }

    public void playEnemyBuffAnimation() {
        enemyRedFlashTimer = ENEMY_BUFF_DURATION;
    }

    public void setCurrentMenu(String menu) {
        this.currentMenu = menu;
    }

    public void setSelectedChoice(int selectedChoice) {
        this.selectedChoice = selectedChoice;
    }


    // ONE rendering entry point shared by Garlic Man and Tung Tung Man.
    // The screen supplies current textures, HP, round number, and message.
    public void render(EnemyAssets assets, Texture currentSprite,
                       PlayerCreature player, Enemy enemy, int round,
                       String battleMessage,
                       boolean showPlayer, boolean showAirSlash,
                       float enemyAlpha, boolean playerHitFlash,
                       boolean showShield) {
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
        // Set sprite sizes for all five enemies
        float enemyScale;

        switch (round) {
            case 1:
                enemyScale = 0.35f;
                break;
            case 2:
                enemyScale = 0.43f;
                break;
            case 3:
                enemyScale = 0.40f;
                break;
            case 4:
                enemyScale = 0.42f;
                break;
            case 5:
                enemyScale = 0.46f;
                break;
            default:
                enemyScale = 0.35f;
        }
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

        // ENEMY SPRITE: Buff makes the enemy grow and turn red.
        float enemyDelta = Gdx.graphics.getDeltaTime();

        enemyRedFlashTimer = Math.max(
            0f,
            enemyRedFlashTimer - enemyDelta
        );

        // Smoothly enlarge and shrink the enemy
        float enemyBuffScale = 1.0f;

        if (enemyRedFlashTimer > 0f) {
            float progress =
                1f - enemyRedFlashTimer / ENEMY_BUFF_DURATION;

            enemyBuffScale = 1.0f + 0.3f
                * (float) Math.sin(progress * Math.PI);
        }

// Keep the enemy centered while its size changes
        float buffedEnemySize = enemySize * enemyBuffScale;
        float buffedEnemyX =
            enemyX - (buffedEnemySize - enemySize) / 2f;
        float buffedEnemyY =
            enemyY - (buffedEnemySize - enemySize) / 2f;

// Preserve transparency during enemy fade effects
        if (enemyRedFlashTimer > 0f) {
            batch.setColor(1f, 0.1f, 1f, enemyAlpha);
        } else {
            batch.setColor(1f, 1f, 1f, enemyAlpha);
        }

        batch.draw(
            currentSprite,
            buffedEnemyX,
            buffedEnemyY,
            buffedEnemySize,
            buffedEnemySize
        );

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
        float delta = Gdx.graphics.getDeltaTime();
        swordAnimationTimer += delta;
        // Update red flash timer
        redFlashTimer = Math.max(0f, redFlashTimer - delta);

        // Update buff animation
        buffAnimationTimer = Math.max(
            0f,
            buffAnimationTimer - delta
        );

        // Smoothly enlarge and shrink the sword
        float buffProgress = 0f;

        if (buffAnimationTimer > 0f) {
            float progress =
                1f - buffAnimationTimer / BUFF_ANIMATION_DURATION;

            buffProgress = (float) Math.sin(progress * Math.PI);
        }

        float buffScale =
            1f + (BUFF_MAX_SCALE - 1f) * buffProgress;

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


        float hitSpriteScale = 0.9f; // 80% ของขนาดเดิม
        float hitWidth = playerWidth * hitSpriteScale;
        float hitHeight = playerSize * hitSpriteScale;

        if (showPlayer) {
            float swordY = playerY + swordOffsetY;

            if (playerHitFlash) {
                // when hit
                batch.setColor(1f, 1f, 1f, 0.75f);
                float hitRotation = +12f; // เอียง 12 องศา
                batch.setColor(1f, 1f, 1f, 0.8f);

                batch.draw(
                    playerHitTexture,
                    playerX + (playerWidth - hitWidth) / 2f,
                    swordY + (playerSize - hitHeight) / 2f,
                    hitWidth / 2f,
                    hitHeight / 2f,
                    hitWidth,
                    hitHeight,
                    1f,
                    1f,
                    hitRotation,
                    0,
                    0,
                    playerHitTexture.getWidth(),
                    playerHitTexture.getHeight(),
                    false,
                    false
                );


                // รีเซ็ตสีหลังวาด
                batch.setColor(Color.WHITE);
            } else {
                // ไม่ได้โดนตี: แสดงเงาตามปกติ

                batch.setColor(0f, 0f, 0f, 0.08f);
                batch.draw(
                    insecureSword,
                    playerX - 80f,
                    swordY - 14f + shadowOffset4,
                    playerWidth,
                    playerSize
                );

                batch.setColor(0f, 0f, 0f, 0.12f);
                batch.draw(
                    insecureSword,
                    playerX - 60f,
                    swordY - 9f + shadowOffset3,
                    playerWidth,
                    playerSize
                );

                batch.setColor(0f, 0f, 0f, 0.17f);
                batch.draw(
                    insecureSword,
                    playerX - 40f,
                    swordY - 5f + shadowOffset2,
                    playerWidth,
                    playerSize
                );

                batch.setColor(0f, 0f, 0f, 0.22f);
                batch.draw(
                    insecureSword,
                    playerX - 20f,
                    swordY - 2f + shadowOffset1,
                    playerWidth,
                    playerSize
                );

                // ดาบปกติ พร้อมเอฟเฟกต์ Buff
                if (redFlashTimer > 0f) {
                    batch.setColor(1f, 0f, 0f, 1f);
                } else {
                    batch.setColor(Color.WHITE);
                }

                batch.draw(
                    insecureSword,
                    playerX - (playerWidth * buffScale - playerWidth) / 2f,
                    swordY - (playerSize * buffScale - playerSize) / 2f,
                    playerWidth * buffScale,
                    playerSize * buffScale
                );
                // Draw NOTE attached to the sword
                // Draw all collected notes attached to the sword
                float noteSize = playerSize * 0.12f;

                float[][] notePositions = {
                    {0.60f, 0.58f},
                    {0.35f, 0.68f},
                    {0.72f, 0.42f},
                    {0.30f, 0.38f},
                    {0.52f, 0.28f}
                };

                for (int i = 0; i < attachedNoteCount; i++) {
                    float noteX = playerX
                        + playerWidth * notePositions[i][0];

                    float noteY = swordY
                        + playerSize * notePositions[i][1];

                    batch.draw(
                        noteTextures[i],
                        noteX,
                        noteY,
                        noteSize,
                        noteSize
                    );
                }

                // รีเซ็ตสี ป้องกันสีตกค้างไปยังรูปอื่น
                batch.setColor(Color.WHITE);
            }
        }

        if (playingHealAnimation) {
            healAnimationTime += Gdx.graphics.getDeltaTime();

            if (healAnimation.isAnimationFinished(healAnimationTime)) {
                playingHealAnimation = false;
            } else {
                TextureRegion frame =
                    healAnimation.getKeyFrame(healAnimationTime);

                float effectSize = 350f;

                // ตำแหน่งกึ่งกลางของตัวละคร
                float effectX = playerX + playerWidth / 2f - effectSize / 2f;
                float effectY = playerY + playerSize  -400f;

                batch.draw(
                    frame,
                    effectX,
                    effectY,
                    effectSize,
                    effectSize
                );
            }
        }


    // จบการวาด Sprite ทั้งหมดก่อนใช้ ShapeRenderer
            batch.end();

    // ตั้งค่าพิกัดของ ShapeRenderer ให้ตรงกับ SpriteBatch
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        if (showShield && showPlayer) {
            float shieldSwordOffsetY =
                (float) Math.sin(swordAnimationTimer * SWORD_FLOAT_SPEED)
                    * SWORD_FLOAT_HEIGHT;

            float shieldSwordY = playerY + shieldSwordOffsetY;

            float shieldSize = playerSize * 0.30f;
            float shieldX = playerX + playerSize * 0.55f;
            float shieldY = shieldSwordY + playerSize * 0.40f;

            batch.begin();
            batch.setColor(Color.WHITE);

            batch.draw(
                shieldTexture,
                shieldX,
                shieldY,
                shieldSize,
                shieldSize
            );

            batch.end();
        }

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
        float width = screenWidth * 0.50f;
        float height = screenHeight * 0.30f;
        dialogueBox = new DialogueBox(20f, screenHeight - height - 20f, width, height);
    }

    // ATTACK MENU: draw four visible placeholders in a 2x2 grid.
    // For this prototype only key 1 triggers the actual player move;
    // the other keyboard numbers are still sprite testing shortcuts.

    private void drawAttackMenu() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float fontScale = screenHeight / 720f * 2.5f;
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

        // Enable transparency
        Gdx.gl.glEnable(
            com.badlogic.gdx.graphics.GL20.GL_BLEND
        );
        Gdx.gl.glBlendFunc(
            com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
            com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        boolean canHighlight =
            currentMenu.equals("MAIN_MENU")
                || currentMenu.equals("FIGHT_MENU");

        // 1. Draw the translucent menu background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 0.35f);
        shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);

        // 2. Draw the gold background inside the selected cell
        if (canHighlight && selectedChoice >= 0 && selectedChoice < 4) {
            int selectedRow = 1 - (selectedChoice / 2);
            int selectedCol = selectedChoice % 2;

            float selectedX = menuX + gap
                + selectedCol * (cellWidth + gap);
            float selectedY = menuY + gap
                + selectedRow * (cellHeight + gap);

            shapeRenderer.setColor(1f, 0.75f, 0.1f, 0.35f);
            shapeRenderer.rect(
                selectedX, selectedY, cellWidth, cellHeight
            );
        }

        shapeRenderer.end();

        // 3. Draw the menu borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                float x = menuX + gap + col * (cellWidth + gap);
                float y = menuY + gap + row * (cellHeight + gap);

                int choiceIndex = (1 - row) * 2 + col;

                if (canHighlight && choiceIndex == selectedChoice) {
                    shapeRenderer.setColor(Color.GOLD);
                } else {
                    shapeRenderer.setColor(Color.WHITE);
                }

                shapeRenderer.rect(x, y, cellWidth, cellHeight);
            }
        }

        shapeRenderer.end();

        Gdx.gl.glDisable(
            com.badlogic.gdx.graphics.GL20.GL_BLEND
        );

        // 4. Draw menu text
        String[] options;

        switch (currentMenu) {
            case "FIGHT_MENU":
                options = new String[]{
                    "Slash",
                    "Shield",
                    "Cure",
                    "Sharpen"
                };
                break;

            case "NOTE":
                options = new String[]{
                    "Use NOTE",
                    "Not Use",
                    "",
                    ""
                };
                break;

            case "STATUS":
                options = new String[]{
                    "STATUS",
                    "View battle status",
                    "",
                    ""
                };
                break;

            case "SURRENDER_CONFIRM":
                options = new String[]{
                    "Surrender",
                    "Continue",
                    "",
                    ""
                };
                break;

            default:
                options = new String[]{
                    "Fight",
                    "NOTE",
                    "Status",
                    "Surrender"
                };
                break;
        }

        float[] textX = {
            menuX + gap + 8f,
            menuX + gap + cellWidth + gap + 8f,
            menuX + gap + 8f,
            menuX + gap + cellWidth + gap + 8f
        };

        float[] textY = {
            menuY + gap + cellHeight + gap + cellHeight / 2f + 5f,
            menuY + gap + cellHeight + gap + cellHeight / 2f + 5f,
            menuY + gap + cellHeight / 2f + 5f,
            menuY + gap + cellHeight / 2f + 5f
        };

        batch.begin();

        font.setColor(Color.BLACK);
        font.getData().setScale(fontScale);

        for (int i = 0; i < options.length; i++) {
            if (!options[i].isEmpty()) {
                font.draw(batch, options[i], textX[i], textY[i]);
            }
        }

        batch.end();
    }



    public void dispose() {
        if (shieldTexture != null) {
            shieldTexture.dispose();
        }
        if (shieldSound != null) {
            shieldSound.dispose();
        }
        for (Sound sound : blockSounds) {
            if (sound != null) {
                sound.dispose();
            }
        }
        shapeRenderer.dispose();
        font.dispose();
        dialogueFont.dispose();
        batch.dispose();
        airSlashTexture.dispose();

        for (Sound sound : healSounds) {
            if (sound != null) {
                sound.dispose();
            }
        }
        if (buffSound != null) {
            buffSound.dispose();
        }
        if (playerHitTexture != null) {
            playerHitTexture.dispose();
        }
        for (Texture texture : noteTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }

    }
}
