package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.DefendMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.cj_BattleSystem.Battle;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;
import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import java.util.Random;
import com.badlogic.drop.ot_MoveSkillsSystem.HealMove;
import com.badlogic.drop.ot_MoveSkillsSystem.SpecialSkillMove;
import com.badlogic.drop.ot_MoveSkillsSystem.ChargeMove;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.drop.ot_MoveSkillsSystem.IntimidateMove;
import com.badlogic.drop.ot_MoveSkillsSystem.MathMove;
import com.badlogic.drop.ot_MoveSkillsSystem.MindLockMove;

// OOP Inheritance: BattleScreen extends ScreenAdapter.
// This class controls the game's flow, not how each image is drawn.
public class BattleScreen extends ScreenAdapter {
    // HELPER CLASSES: separate rendering, enemy creation, and file loading.
    private BattleRenderer renderer;
    private EnemyAssets assets;
    private Texture currentSprite;
    private int round = 1;
    private boolean waitingForNextRound = false;
    private boolean allRoundsWon = false;
    private String enemyDisplayName = "Garlic Man";
    private String battleMessage = "A wild Garlic Man appeared!";

    private float animationTimer = 0f;
    private boolean waitingForEnemyAnimation = false;

    private boolean airSlashActive = false;
    private float airSlashTimer = 0f;
    private static final float AIR_SLASH_DURATION = 0.30f;
    private boolean showPlayerSword = true;
    private float swordHideTimer = 0f;
    private static final float SWORD_HIDE_DURATION = 0.35f;

    private boolean doubleSlashActive = false;
    private boolean doubleSlashSecondHitPending = false;
    private float doubleSlashTimer = 0f;

    private boolean lifeStealActive = false;
    private boolean immortalActive = false;
    private int immortalTurnsRemaining = 0;
    private int immortalOriginalDefendReduction = 0;

    // Stun NOTE
    private boolean stunActive = false;
    private boolean enemyStunned = false;

    private float enemyTurnTimer = 0f;
    private float enemyFadeTimer = 0f;
    private static final float ENEMY_FADE_DURATION = 0.35f;

    private Sound[] slashSounds;
    private final Random random = new Random();

    private Sound debuffSound;

    private float playerHitFlashTimer = 0f;
    private static final float PLAYER_HIT_FLASH_DURATION = 0.5f;
    private boolean showShield = false;

    private float enemyBuffScale = 1.0f;
    private float enemyRedFlashTimer = 0f;

    // Intimidate: reduce enemy Attack
    private boolean intimidateActive = false;
    private int enemyOriginalAttack = 0;
    private int enemyReducedAttack = 0;

    // Player Sharpen animation
    private float playerBuffScale = 1.0f;
    private float playerRedFlashTimer = 0f;

    private static final float PLAYER_BUFF_DURATION = 0.6f;

    // Reuse the existing battle system
    private PlayerCreature player;
    private Enemy enemy;
    private Battle battle;
    private Move enemyMove;

    private Move splashHit;
    private Move defendMove;
    private Move healMove;
    private Move specialSkillMove;

    private Move[] enemyMoves;

    // Calculator's Math status
    private int mathTurnsRemaining = 0;
    private int calculatorOriginalAttack = 0;
    private int playerOriginalAttack = 0;
    private boolean mathEffectActive = false;
    private boolean mindLockActivatedThisTurn = false;

    // Calculator Math animation sequence
    private enum MathAnimationState {
        NONE,
        ENEMY_BUFF,
        PLAYER_DEBUFF
    }

    // Angry Book: Mind Lock
    private int noteLockTurnsRemaining = 0;

    private boolean showingMindBookSprite = false;
    private float mindBookSpriteTimer = 0f;
    private static final float MINDBOOK_SPRITE_DURATION = 0.75f;
    // Mind Lock debuff effect on player
    private float mindLockDebuffTimer = 0f;
    private static final float MIND_LOCK_DEBUFF_DURATION = 0.5f;

    private MathAnimationState mathAnimationState = MathAnimationState.NONE;

    private float mathAnimationTimer = 0f;

    private static final float MATH_ENEMY_EFFECT_DURATION = 0.45f;
    private static final float MATH_PLAYER_EFFECT_DURATION = 0.45f;

    // Mathmind sprite is shown only briefly when Math is used
    private float mathSpriteTimer = 0f;
    private static final float MATH_SPRITE_DURATION = 0.75f;
    private boolean showingMathSprite = false;


    private Sound buttonSound;

    private boolean waitingToUseMove = false;
    private float moveDelayTimer = 0f;
    private static final float MOVE_DELAY = 1f;

    // save player move
    private int selectedMove = 0;

    private final Game game;

    // Allow the final hit and fainting message to play before the loss screen.
    private boolean playerDefeated = false;
    private float defeatTimer = 0f;
    private static final float DEFEAT_DELAY = 2.0f;

    private float playerDeathFadeTimer = 0f;
    private static final float PLAYER_DEATH_FADE_DURATION = 1.5f;
    private boolean playerDying = false;

    // Show the final boss defeat before the victory screen.
    private boolean victoryPending = false;
    private float victoryTimer = 0f;

    private float enemyStunPauseTimer = 0f;
    private static final float ENEMY_STUN_PAUSE_DURATION = 2.5f;


    public BattleScreen(Game game) {
        this.game = game;
    }

    private float enemyDeathFadeTimer = 0f;
    private static final float ENEMY_DEATH_FADE_DURATION = 1.2f;
    private boolean enemyDying = false;
    private float menuHideTimer = 0f;
    private static final float MENU_HIDE_DURATION = 0.3f;

    private Sound enemyChargeSound;
    private Texture stunTexture;
    private Sound stunSound;


    private boolean enemySkippedTurn = false;
    private float stunEffectTimer = 0f;


    private static final float STUN_EFFECT_DURATION = 1.5f;

    private Texture heavyTungTexture;
    private boolean heavyTungAttackPending = false;
    private boolean heavyNekChargeActive = false;


    @Override
    public void show() {
        enemy = EnemyFactory.createEnemy(round);

        slashSounds = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/slashsound01.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/slashsound02.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/slashsound03.mp3"))
        };
        debuffSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/debuff01.mp3")
        );
        enemyChargeSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/buff01.mp3")
        );
        stunTexture = new Texture(
            Gdx.files.internal("effect/Stun.png")
        );

        stunSound = Gdx.audio.newSound(
            Gdx.files.internal("effect/ShockedSoundEffect.mp3")
        );
        // Create renderer + assets only once when this battle screen opens.
        renderer = new BattleRenderer();
        heavyTungTexture = new Texture(
            Gdx.files.internal("enemies/HeavyTung.png")
        );

        assets = new EnemyAssets();
        assets.playMusic(round);
        currentSprite = assets.getIdle(round);

        // PLAYER STATS: create Insecure Sword only once so HP carries between all rounds (healing comes later).
        player = new PlayerCreature("Insecure Sword", 200, 20, 15, 100);
        // EnemyFactory centralizes enemy stats and moves.

        battle = new Battle(player, enemy);
        battle.startBattle();
        //1 move
        splashHit = new DamageMove("Slash Hit", 20,
            "Insecure Sword attacks Garlic Man!");

        //2 move
        defendMove = new DefendMove(
            "Defend",
            50,
            "Insecure Sword braces for an incoming attack!"
        );

        //3 move
        // 3 move: Cure
        healMove = new HealMove(
            "Cure",
            1,
            "Restores 20-40 HP"
        );
        //select sound
        buttonSound = Gdx.audio.newSound(
            Gdx.files.internal("effect/ButtonSoundEffect.mp3")
        );

        specialSkillMove = new IntimidateMove();
        // Create the enemy's two moves
        enemyMoves = EnemyFactory.createEnemyMoves(round);
        enemyMove = enemyMoves[0];
        battleMessage = "A wild Garlic Man appeared!";



    }
    private void playButtonSound() {
        if (buttonSound != null) {
            buttonSound.play(0.2f);
        }
    }


    private enum MenuState {
        MAIN_MENU,
        FIGHT_MENU,
        NOTE,
        NOTE_CONFIRM,
        STATUS,
        SURRENDER_CONFIRM
    }

    private MenuState menuState = MenuState.MAIN_MENU;

    private int selectedChoice = 0;
    private int selectedNoteChoice = 0;


    private boolean typingNote = false;
    private StringBuilder noteInput = new StringBuilder();

    private boolean noteJustSubmitted = false;

    @Override
    public void render(float delta) {

        // Keep Professor Nek's defeated sprite visible for two seconds.
        if (victoryPending) {
            victoryTimer -= delta;
            if (victoryTimer <= 0f) {
                game.setScreen(new WinScreen(game));
                dispose();
                return;
            }


            renderer.setCurrentMenu(menuState.name());
            renderer.setSelectedChoice(selectedChoice);

            renderer.setShowNoteInput(typingNote);
            renderer.setNoteInputText(noteInput.toString());
            renderer.setNoteLocked(noteLockTurnsRemaining > 0);

            System.out.println(
                "Menu: " + menuState.name()
                    + " | Note locked: " + (noteLockTurnsRemaining > 0)
            );


            renderer.render(
                assets, currentSprite, player, enemy, round,
                battleMessage, showPlayerSword, airSlashActive,
                1f, false, showShield, 1f
            );
            return;
        }

        if (playerDefeated) {
            defeatTimer -= delta;
            if (defeatTimer <= 0f) {
                showLoseScreen();
                return;
            }
        }

        // Disable menu input while defeated, but continue rendering
        // the battle background, enemy animation and fainting message.
        if (!playerDefeated) {

            // Show the victory message until Enter is pressed.
            if (waitingForNextRound
                && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                startNextRound();
            }



            // Handle menu navigation
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                if (menuState == MenuState.SURRENDER_CONFIRM
                    || menuState == MenuState.NOTE
                    || menuState == MenuState.STATUS
                    || menuState == MenuState.NOTE_CONFIRM
                    || menuState == MenuState.FIGHT_MENU) {
                    menuState = MenuState.MAIN_MENU;
                    battleMessage = "Choose your action.";

                }
            }
            // Navigate the 2x2 menu with arrow keys.

            if ((menuState == MenuState.MAIN_MENU
                || menuState == MenuState.FIGHT_MENU
                || menuState == MenuState.SURRENDER_CONFIRM)
                && !waitingForNextRound
                && !allRoundsWon
                && !waitingToUseMove
                && !waitingForEnemyAnimation
                && animationTimer <= 0f
                && enemyTurnTimer <= 0f
                && !battle.isBattleOver()
                && player.isAlive()
                && (menuState != MenuState.FIGHT_MENU
                && !typingNote
                || battle.getTurnManager().isPlayerTurn())) {

                if (menuState == MenuState.SURRENDER_CONFIRM) {
                    updateSurrenderNavigation();
                } else {
                    updateChoiceNavigation();
                }
            }



            if (menuState == MenuState.MAIN_MENU
                && !typingNote
                && !noteJustSubmitted
                && !waitingForNextRound
                && !allRoundsWon
                && !battle.isBattleOver()
                && player.isAlive()) {
                if (!waitingForEnemyAnimation
                    && animationTimer <= 0f
                    && enemyTurnTimer <= 0f
                    && !waitingToUseMove
                    && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                    playButtonSound();

                    switch (selectedChoice) {
                        case 0:
                            menuState = MenuState.FIGHT_MENU;
                            selectedChoice = 0;
                            battleMessage = "Choose your move!";
                            break;


                        case 1:
                            if (noteLockTurnsRemaining > 0) {
                                battleMessage = "Mind Lock! NOTE is sealed for "
                                    + noteLockTurnsRemaining + " turns!";
                            } else if (renderer.getAttachedNoteCount() < round) {
                                startNoteTyping();
                            } else {
                                battleMessage = "No more NOTE available this round!";
                            }
                            break;



                        case 2:
                            menuState = MenuState.STATUS;
                            battleMessage = "Viewing battle status.";
                            break;


                        case 3:
                            menuState = MenuState.SURRENDER_CONFIRM;
                            selectedChoice = 0;
                            battleMessage = "Are you sure you want to surrender?";
                            break;
                    }
                }


            } else if (menuState == MenuState.NOTE_CONFIRM) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                    if (selectedNoteChoice == 0) {
                        if (noteLockTurnsRemaining > 0) {
                            battleMessage = "Mind Lock! NOTE is sealed for "
                                + noteLockTurnsRemaining + " turns!";
                            menuState = MenuState.MAIN_MENU;
                        } else if (renderer.getAttachedNoteCount() < round) {
                            menuState = MenuState.MAIN_MENU;
                            startNoteTyping();
                        } else {
                            battleMessage = "No more NOTE available this round!";
                            menuState = MenuState.MAIN_MENU;
                        }
                    }


                    selectedChoice = 1;
                }

        } else if (menuState == MenuState.STATUS) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    menuState = MenuState.MAIN_MENU;
                    selectedChoice = 2;
                    battleMessage = "Choose your action.";
                }

            } else if (menuState == MenuState.SURRENDER_CONFIRM) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    playButtonSound();

                    if (selectedChoice == 0) {
                        showLoseScreen();
                        return;
                    } else {
                        menuState = MenuState.MAIN_MENU;
                        selectedChoice = 3;
                        battleMessage = "You chose to continue fighting.";
                    }
                }
            }
            else
            if (menuState == MenuState.FIGHT_MENU) {
                if (!waitingForNextRound
                    && !allRoundsWon
                    && !waitingToUseMove
                    && !waitingForEnemyAnimation
                    && animationTimer <= 0f
                    && enemyTurnTimer <= 0f
                    && battle.getTurnManager().isPlayerTurn()
                    && !battle.isBattleOver()
                    && player.isAlive()
                    && !battle.isBattleOver()
                    && player.isAlive()

                    && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                    selectedMove = selectedChoice + 1;
                    playButtonSound();
                    waitingToUseMove = true;
                    moveDelayTimer = MOVE_DELAY;
                    battleMessage = "Preparing move...";
                }
            }
            //
            if (menuHideTimer > 0f) {
                menuHideTimer -= delta;
            }

        } // End of player input handling.

        if (waitingToUseMove) {
            moveDelayTimer -= delta;

            if (moveDelayTimer <= 0f) {
                executeSelectedMove();
            }
        }



        if (animationTimer > 0f) {
            animationTimer -= delta;

            if (animationTimer <= 0f) {
                currentSprite = assets.getIdle(round);
                heavyNekChargeActive = false;
                animationTimer = 0f;
                waitingForEnemyAnimation = false;
            }
        }

        if (enemyStunPauseTimer > 0f) {
            enemyStunPauseTimer -= delta;

            if (enemyStunPauseTimer <= 0f) {
                enemyStunPauseTimer = 0f;
                waitingForEnemyAnimation = false;
                currentSprite = assets.getIdle(round);
            }
        }


        // Wait before the current enemy performs its counterattack
        if (enemyTurnTimer > 0f) {


            // Count down using the time between frames
            enemyTurnTimer -= delta;

            // Only attack once the timer reaches zero
            if (enemyTurnTimer <= 0f) {


                enemyTurnTimer = 0f;


                // Check that the current enemy is alive and it is its turn

                int previousPlayerHp = player.getCurrentHp();
                boolean shieldWasActive = showShield;

                // Keep using the same ChargeMove object while charging.
                // Round 3: Calculator chooses its move.
                // Round 3: Calculator chooses its move.

                if (round == 3) {
                    // Calculator keeps its existing Math behavior.
                    if (mathEffectActive) {
                        enemyMove = new DamageMove(
                            "Calculate",
                            18,
                            "Calculator uses Calculate!"
                        );
                    } else {
                        enemyMove = random.nextBoolean()
                            ? new MathMove()
                            : new DamageMove(
                            "Calculate",
                            18,
                            "Calculator uses Calculate!"
                        );
                    }
                } else {
                    // Keep a charged move for its follow-up attack.
                    boolean continuingCharge =
                        enemyMove instanceof ChargeMove
                            && ((ChargeMove) enemyMove).isCharged();

                    if (!continuingCharge) {
                        do {
                            enemyMove = enemyMoves[random.nextInt(enemyMoves.length)];


                        } while (
                            ((round == 4 || round == 5)
                                && noteLockTurnsRemaining > 0
                                && enemyMove instanceof MindLockMove)
                                ||
                                (round == 5
                                    && enemyMove instanceof MathMove
                                    && ((MathMove) enemyMove).hasBeenUsed())
                        );


                    }
                }



                System.out.println("Turn before enemy action: "
                    + battle.getTurnManager().getCurrentTurn());



                boolean wasImmortal = immortalActive;
                boolean enemyWasStunned = enemyStunned;

// Remember whether Professor Nek is about to perform
// the attack following Charge, before ChargeMove resets its state.
                boolean nekChargedAttack =
                    round == 5
                        && !enemyWasStunned
                        && enemyMove instanceof ChargeMove
                        && ((ChargeMove) enemyMove).isCharged();

                String result;


                boolean usedMindLock = false;

                if (enemyWasStunned) {
                    enemySkippedTurn = true;
                    result = battle.skipEnemyTurn();
                    enemyStunned = false;
                } else {
                    enemySkippedTurn = false;
                    result = battle.enemyTurn(enemyMove);


                    if ((round == 4 || round == 5)
                        && enemyMove instanceof MindLockMove) {

                        usedMindLock = true;
                        noteLockTurnsRemaining = 3;

                        System.out.println(
                            "Mind Lock activated! Turns = " + noteLockTurnsRemaining
                        );

                        renderer.playMindLockDebuffAnimation();

                        // Book uses MindBook; Nek uses MindNek.
                        if (round == 4) {
                            currentSprite = assets.getMindBook();
                        } else {
                            currentSprite = assets.getMindNek();
                        }

                        showingMindBookSprite = true;
                        mindBookSpriteTimer = MINDBOOK_SPRITE_DURATION;
                        animationTimer = 0f;
                        waitingForEnemyAnimation = true;
                    }

                }

                boolean usedMath = enemyMove instanceof MathMove
                    && ((MathMove) enemyMove).hasBeenUsed();
                if (usedMath) {
                    activateMathEffect((MathMove) enemyMove);
                }



                System.out.println("DEBUG ENEMY RESULT: [" + result + "]");

                System.out.println("Enemy result: " + result);

                boolean enemyIsCharging = result.contains("is charging up");

// HeavyTung: จำสถานะ Charge ของศัตรูตัวที่ 2
                if (round == 2 && enemyIsCharging) {
                    heavyTungAttackPending = true;
                }

                boolean useHeavyTungAttack =
                    round == 2
                        && !enemyIsCharging
                        && !enemyWasStunned
                        && heavyTungAttackPending;
                boolean playerWasDamaged =
                    player.getCurrentHp() < previousPlayerHp;

// นับเทิร์น Immortal เมื่อศัตรูได้ลงมือจริง
                if (!enemyWasStunned && immortalActive) {
                    immortalTurnsRemaining--;

                    System.out.println(
                        "Immortal turns remaining: " + immortalTurnsRemaining
                    );

                    if (immortalTurnsRemaining <= 0) {
                        endImmortal();
                    }
                }
                if (playerWasDamaged) {
                    playerHitFlashTimer = PLAYER_HIT_FLASH_DURATION;
                }
                if (enemyRedFlashTimer > 0f) {
                    enemyRedFlashTimer -= delta;

                    if (enemyRedFlashTimer <= 0f) {
                        enemyRedFlashTimer = 0f;
                        enemyBuffScale = 1.0f;
                    }
                }

                System.out.println(result);

                // Detect whether this turn was spent charging.
                battleMessage = result;





                if (enemyWasStunned) {
                    // Enemy skips its turn and returns to idle.
                    currentSprite = assets.getIdle(round);
                    enemyStunPauseTimer = 0f;
                    enemyFadeTimer = 0f;
                    waitingForEnemyAnimation = false;
                    animationTimer = 0f;
                    enemyTurnTimer = 0f;
                } else if (usedMath) {
                } else if (usedMindLock) {
                    // Keep MindBook sprite visible for a short time.

                } else if (enemyIsCharging) {
                    renderer.playEnemyBuffAnimation();

                    if (enemyChargeSound != null) {
                        enemyChargeSound.play(0.5f);
                    }

                    // Keep Nek's normal sprite while charging.
                    currentSprite = assets.getIdle(round);
                    animationTimer = 0f;

                } else {
                    assets.playEnemyAttack(round);

                    if (nekChargedAttack) {
                        // Professor Nek's attack after charging.
                        currentSprite = assets.getHeavyNek();
                    } else if (useHeavyTungAttack) {
                        currentSprite = heavyTungTexture;
                        heavyTungAttackPending = false;
                    } else {
                        currentSprite = assets.getAttack(round);
                    }

                    animationTimer = 0.75f;
                    waitingForEnemyAnimation = true;
                }



                if (!player.isAlive()) {
                    playerDefeated = true;
                    playerDying = true;

                    // ค้างภาพตอนโดนโจมตี แล้วเริ่ม Fade
                    playerDeathFadeTimer = PLAYER_DEATH_FADE_DURATION;
                    defeatTimer = PLAYER_DEATH_FADE_DURATION;

                    waitingToUseMove = false;
                    enemyTurnTimer = 0f;
                    waitingForEnemyAnimation = false;
                    animationTimer = 0f;

                    // ใช้ภาพ Hurt ค้างไว้ ไม่กลับไป Idle
                    currentSprite = assets.getHurt(round);

                    battleMessage = battleStatus("Insecure Sword fainted!");
                }


                System.out.println(
                    "Sword HP: " + player.getCurrentHp()
                );
                tickMathDuration();
                tickNoteLockDuration();


            }
        }
        if (airSlashActive) {
            airSlashTimer -= delta;

            if (airSlashTimer <= 0f) {
                airSlashActive = false;
                airSlashTimer = 0f;
            }
        }
        if (mindLockDebuffTimer > 0f) {
            mindLockDebuffTimer -= delta;

            if (mindLockDebuffTimer < 0f) {
                mindLockDebuffTimer = 0f;
            }
        }
            // Wait for the first slash effect to finish before the second hit.
        if (doubleSlashSecondHitPending) {
            doubleSlashTimer -= delta;

            if (doubleSlashTimer <= 0f) {
                doubleSlashSecondHitPending = false;
                doubleSlashTimer = 0f;

                // Do not hit an enemy that has already been defeated.
                if (enemy.isAlive() && !battle.isBattleOver()) {
                    String secondHitResult = battle.useMove(player, enemy, splashHit);

                    // Show the second slash effect.
                    airSlashActive = true;
                    airSlashTimer = AIR_SLASH_DURATION;
                    renderer.playAirSlashAnimation(2);
                    applyLifeSteal();

                    // Play a slash sound.
                    int soundIndex = random.nextInt(slashSounds.length);
                    slashSounds[soundIndex].play();

                    battleMessage = secondHitResult;
                    if (enemy.isAlive() && tryStunEnemy()) {
                        battleMessage += "\n" + enemyDisplayName + " is stunned!";

                        // Keep the hurt sprite and transparency while stunned.
                        currentSprite = assets.getHurt(round);
                        enemyFadeTimer = ENEMY_STUN_PAUSE_DURATION;
                    }

                    if (!enemy.isAlive()) {
                        // The second hit defeated the enemy.
                        battle.getWinner();
                        currentSprite = assets.getDead(round);
                        enemyTurnTimer = 0f;
                        waitingForEnemyAnimation = false;
                        animationTimer = 0f;
                        enemyDying = true;
                        enemyDeathFadeTimer = ENEMY_DEATH_FADE_DURATION;
                        assets.stopMusic();
                        assets.playEnemyDefeated();

                        if (round < 5) {
                            waitingForNextRound = true;
                            battleMessage = battleStatus(
                                enemyDisplayName + " was defeated! Press SPACE for the next round."
                            );
                        } else {
                            allRoundsWon = true;
                            victoryPending = true;
                            victoryTimer = 2f;
                            battleMessage = "Professor Nek defeated!";
                        }
                    } else {
                        // Allow the enemy to counterattack only after both hits.
                        currentSprite = assets.getHurt(round);
                        animationTimer = 0.4f;
                        waitingForEnemyAnimation = true;
                        enemyTurnTimer = 1.5f;
                        enemyFadeTimer = ENEMY_FADE_DURATION;
                    }
                }
            }
        }
        if (!showPlayerSword) {
            swordHideTimer -= delta;

            if (swordHideTimer <= 0f) {
                showPlayerSword = true;
                swordHideTimer = 0f;
            }
        }

        if (enemyFadeTimer > 0f) {
            enemyFadeTimer -= delta;

            if (enemyFadeTimer < 0f) {
                enemyFadeTimer = 0f;
            }
        }
        float enemyAlpha = 1f;

        if (enemyFadeTimer > 0f) {
            enemyAlpha = 0.55f;
        }

        if (enemyDying) {
            enemyDeathFadeTimer -= delta;

            if (enemyDeathFadeTimer <= 0f) {
                enemyDeathFadeTimer = 0f;
            }

            enemyAlpha = enemyDeathFadeTimer / ENEMY_DEATH_FADE_DURATION;
        }
        if (enemyFadeTimer > 0f) {
            enemyAlpha = 0.55f;
        }
        if (playerHitFlashTimer > 0f) {
            playerHitFlashTimer -= delta;

            if (playerHitFlashTimer < 0f) {
                playerHitFlashTimer = 0f;
            }
        }

        // Update Calculator Math animation sequence.
        if (mathAnimationState != MathAnimationState.NONE) {
            mathAnimationTimer -= delta;

            if (mathAnimationTimer <= 0f) {
                if (mathAnimationState == MathAnimationState.ENEMY_BUFF) {
                    // Red Calculator effect finished.
                    // Now play the purple player debuff effect.
                    mathAnimationState = MathAnimationState.PLAYER_DEBUFF;
                    mathAnimationTimer = MATH_PLAYER_EFFECT_DURATION;

                    // Play the debuff sound once.
                    debuffSound.play(0.5f);
                } else {
                    // Both effects finished.
                    mathAnimationState = MathAnimationState.NONE;
                    mathAnimationTimer = 0f;
                }
            }
        }

        // Update Sharpen animation every frame.
        if (playerRedFlashTimer > 0f) {
            playerRedFlashTimer -= delta;

            if (playerRedFlashTimer <= 0f) {
                playerRedFlashTimer = 0f;
                playerBuffScale = 1.0f;
            }
        }
        boolean playerHitFlash = playerHitFlashTimer > 0f;




        renderer.setCurrentMenu(menuState.name());
        renderer.setSelectedChoice(selectedChoice);

        renderer.setShowNoteInput(typingNote);
        renderer.setNoteInputText(noteInput.toString());

        // RENDERING: hand all graphics to the renderer, leaving BattleScreen
        // responsible only for inputs, turns, and round progression.

        boolean canShowMenu =
            !typingNote
                && !waitingForEnemyAnimation
                && animationTimer <= 0f
                && enemyTurnTimer <= 0f
                && !waitingToUseMove
                && !waitingForNextRound
                && !allRoundsWon
                && !playerDefeated;

        renderer.setShowAttackMenu(canShowMenu);

        float playerDeathAlpha = 1f;

        if (playerDying) {
            playerDeathFadeTimer -= delta;

            playerDeathAlpha = Math.max(
                0f,
                playerDeathFadeTimer / PLAYER_DEATH_FADE_DURATION
            );
        }
        if (typingNote) {
            battleMessage = "Type a magic word and press Enter: "
                + noteInput;
        }

        if (stunEffectTimer > 0f) {
            stunEffectTimer -= delta;

            if (stunEffectTimer < 0f) {
                stunEffectTimer = 0f;
            }
        }
        if (showingMindBookSprite) {
            mindBookSpriteTimer -= delta;

            if (mindBookSpriteTimer <= 0f) {
                mindBookSpriteTimer = 0f;
                showingMindBookSprite = false;

                currentSprite = assets.getIdle(round);
                waitingForEnemyAnimation = false;
            }
        }


        if (showingMathSprite) {
            mathSpriteTimer -= delta;

            if (mathSpriteTimer <= 0f) {
                mathSpriteTimer = 0f;
                showingMathSprite = false;

                currentSprite = assets.getIdle(round);
                waitingForEnemyAnimation = false;
            }
        }



        renderer.setLifeStealActive(lifeStealActive);
        renderer.setImmortalActive(immortalActive);
        renderer.setStunActive(stunActive);
        renderer.drawStunEffect(stunEffectTimer);
        renderer.setMathAnimation(
            mathAnimationState.name(),
            mathAnimationTimer
        );


        renderer.render(
            assets,
            currentSprite,
            player,
            enemy,
            round,
            battleMessage,
            showPlayerSword,
            airSlashActive,
            enemyAlpha,
            playerHitFlash,
            showShield,
            playerDeathAlpha
        );
        noteJustSubmitted = false;


    }

    private void startNoteTyping() {
        typingNote = true;
        noteInput.setLength(0);

        renderer.setShowNoteInput(true);
        renderer.setNoteInputText("");


        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (!typingNote) {
                    return false;
                }

                if (character == '\n' || character == '\r') {
                    typingNote = false;
                    noteJustSubmitted = true;

                    Gdx.input.setInputProcessor(null);

                    playButtonSound(); // เสียงคลิกเมื่อกด Enter หลังพิมพ์ NOTE

                    useNoteWord(noteInput.toString());

                    return true;
                }

                if (character == '\b') {
                    int length = noteInput.length();
                    if (length > 0) {
                        noteInput.deleteCharAt(length - 1);
                    }
                    return true;
                }

                if (!Character.isISOControl(character)) {
                    noteInput.append(character);
                }

                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    typingNote = false;
                    Gdx.input.setInputProcessor(null);
                    battleMessage = "NOTE cancelled.";
                    return true;
                }
                return false;
            }
        });

        battleMessage = "Type a magic word and press Enter: ";
    }


    private void updateChoiceNavigation() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectedChoice % 2 == 1) {
                selectedChoice--;
                playButtonSound();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectedChoice % 2 == 0) {
                selectedChoice++;
                playButtonSound();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (selectedChoice >= 2) {
                selectedChoice -= 2;
                playButtonSound();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectedChoice < 2) {
                selectedChoice += 2;
                playButtonSound();
            }
        }
    }

    private void tickNoteLockDuration() {
        if (noteLockTurnsRemaining > 0) {
            noteLockTurnsRemaining--;

            if (noteLockTurnsRemaining == 0) {
                battleMessage += "\nMind Lock has ended! NOTE is available again.";
            }
        }
    }



    private void tickMathDuration() {
        if (!mathEffectActive) {
            return;
        }

        mathTurnsRemaining--;

        if (mathTurnsRemaining <= 0) {
            enemy.setAttack(calculatorOriginalAttack);
            player.setAttack(playerOriginalAttack);

            mathTurnsRemaining = 0;
            mathEffectActive = false;
        }
    }




    private void updateSurrenderNavigation() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)
            || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedChoice = (selectedChoice == 0) ? 1 : 0;
            playButtonSound();
        }
    }



    // RESIZE: tell renderer to adjust its projection + top-left dialogue position.
    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    // DISPOSE: each owner disposes only its own GPU/audio resources.
    @Override
    public void dispose() {
        if (slashSounds != null) {
            for (Sound sound : slashSounds) {
                sound.dispose();
            }
            slashSounds = null;
        }

        if (renderer != null) {
            renderer.dispose();
        }

        if (assets != null) {
            assets.dispose();
        }
        if (debuffSound != null) {
            debuffSound.dispose();
            debuffSound = null;
        }
        if (buttonSound != null) {
            buttonSound.dispose();
        }
        if (enemyChargeSound != null) {
            enemyChargeSound.dispose();
            enemyChargeSound = null;
        }
        if (stunTexture != null) {
            stunTexture.dispose();
            stunTexture = null;
        }

        if (stunSound != null) {
            stunSound.dispose();
            stunSound = null;
        }
        if (heavyTungTexture != null) {
            heavyTungTexture.dispose();
            heavyTungTexture = null;
        }
    }



    private void useNoteWord(String text) {
        // Check whether the entered keyword is valid.

        if (text == null) {
            battleMessage = "No such keyword! Choose a command.";
            menuState = MenuState.MAIN_MENU;
            selectedChoice = 0;
            return;
        }

        String keyword = text.trim();


        if (!"Sharpen".equalsIgnoreCase(keyword)
            && !"DoubleSlash".equalsIgnoreCase(keyword)
            && !"LifeSteal".equalsIgnoreCase(keyword)
            && !"Immortal".equalsIgnoreCase(keyword)
            && !"Stun".equalsIgnoreCase(keyword)) {
            battleMessage = "No such keyword! Choose a command.";
            menuState = MenuState.MAIN_MENU;
            selectedChoice = 0;
            return;
        }

        // Attach the NOTE only after the keyword is validated.
        if (!renderer.attachNoteToSword()) {
            battleMessage = "Cannot attach more notes!";
            menuState = MenuState.MAIN_MENU;
            selectedChoice = 0;
            return;
        }

        if ("Sharpen".equalsIgnoreCase(keyword)) {
            player.activateFireBuff();
            renderer.playSharpenAnimation();
            battleMessage = "Sharpen power activated! 100% Damage Increase!";
        } else if ("DoubleSlash".equalsIgnoreCase(keyword)) {
            doubleSlashActive = true;
            battleMessage = "DoubleSlash activated! Slash Hit will strike twice!";
            renderer.playDoubleSlashBuffAnimation();
        } else if ("LifeSteal".equalsIgnoreCase(keyword)) {
            lifeStealActive = true;
            battleMessage = "LifeSteal activated! Recover 25% of Slash damage!";
            renderer.playLifeStealBuffAnimation();
    } else if ("Immortal".equalsIgnoreCase(keyword)) {
        immortalOriginalDefendReduction = player.getDefendReduction();

        immortalActive = true;
        immortalTurnsRemaining = 3;
        player.setPermanentDefendReduction(true);
        player.setDefendReduction(9999);
            renderer.playImmortalBuffAnimation();

        battleMessage = "Immortal activated! 3 turns of invincibility!";
        } else if ("Stun".equalsIgnoreCase(keyword)) {
            stunActive = true;
            renderer.playStunBuffAnimation();
            battleMessage = "Stun activated! 15% chance to stun the enemy on every attack!";
            renderer.playStunBuffAnimation();
        }


    // NOTE consumes the player's turn only when valid.
        if (enemy.isAlive() && !battle.isBattleOver()) {
            enemyTurnTimer = 1.5f;
            waitingForEnemyAnimation = false;
            animationTimer = 0f;
            battle.endPlayerTurn();
        }
    }
    private void endImmortal() {
        immortalActive = false;
        immortalTurnsRemaining = 0;

        player.setPermanentDefendReduction(false);
        player.setDefendReduction(immortalOriginalDefendReduction);

        battleMessage += "\nImmortal has ended!";
    }


    private boolean tryStunEnemy() {

        if (!stunActive || !enemy.isAlive() || enemyStunned) {
            return false;
        }

        if (random.nextFloat() < 0.15f) {
            enemyStunned = true;
            stunEffectTimer = STUN_EFFECT_DURATION;

            if (stunSound != null) {
                stunSound.play();
            }

            return true;
        }

        return false;
    }




    private void applyLifeSteal() {
        if (!lifeStealActive) {
            return;
        }

        int damage = ((DamageMove) splashHit).getLastDamage();
        int healAmount = (int) (damage * 0.25);

        if (healAmount > 0) {
            int actualHeal = player.heal(healAmount);

            battleMessage += "\nLifeSteal restored "
                + actualHeal + " HP!";
        }
    }


    private void executeSelectedMove() {
        String result;

        switch (selectedMove) {

            case 1:
                result = battle.playerTurn(splashHit);

                // First slash uses airslash 01.
                airSlashActive = true;
                airSlashTimer = AIR_SLASH_DURATION;
                renderer.playAirSlashAnimation(1);

                int index = random.nextInt(slashSounds.length);
                slashSounds[index].play();

                showPlayerSword = false;
                swordHideTimer = SWORD_HIDE_DURATION;
                menuHideTimer = MENU_HIDE_DURATION;

                if (enemy.isAlive()) {
                    currentSprite = assets.getHurt(round);
                    animationTimer = 0.4f;
                    waitingForEnemyAnimation = true;
                    enemyFadeTimer = ENEMY_FADE_DURATION;

                    if (doubleSlashActive) {
                        // Delay the second hit. The enemy must wait.
                        doubleSlashSecondHitPending = true;
                        doubleSlashTimer = 0.35f;
                        enemyTurnTimer = 0f;
                    } else {
                        // Normal Slash: the enemy counters as usual.
                        doubleSlashSecondHitPending = false;
                        doubleSlashTimer = 0f;
                        enemyTurnTimer = 1.5f;
                    }
                } else {
                    // The first hit defeated the enemy; do not perform a second hit.
                    doubleSlashSecondHitPending = false;
                    doubleSlashTimer = 0f;

                    currentSprite = assets.getDead(round);
                    assets.stopMusic();
                    assets.playEnemyDefeated();
                    animationTimer = 0f;
                    enemyTurnTimer = 0f;
                    waitingForEnemyAnimation = false;

                    enemyDying = true;
                    enemyDeathFadeTimer = ENEMY_DEATH_FADE_DURATION;

                    if (round < EnemyFactory.getRoundCount()) {
                        waitingForNextRound = true;
                        String nextName = EnemyFactory.getEnemyName(round + 1);

                        battleMessage = battleStatus(
                            "YOU WIN! " + enemyDisplayName
                                + " defeated!\nNEXT: " + nextName
                                + " - Press SPACE"
                        );
                    } else {
                        allRoundsWon = true;
                        victoryPending = true;
                        victoryTimer = 2.0f;
                        battleMessage = battleStatus("Professor Nek defeated!");
                    }
                }

                if (enemy.isAlive()) {
                    battleMessage = battleStatus(
                        doubleSlashActive
                            ? "Insecure Sword used DoubleSlash!"
                            : "Insecure Sword used Slash Hit!"
                    );
                }

                if (enemy.isAlive() && tryStunEnemy()) {
                    battleMessage += "\n" + enemyDisplayName + " is stunned!";
                }

                applyLifeSteal();
                break;

            case 2:
                result = battle.playerTurn(defendMove);
                renderer.playShieldSound();
                showShield = true;

                battleMessage =
                    battleStatus("Insecure Sword used Defend!");

                if (enemy.isAlive() && !battle.isBattleOver()) {
                    enemyTurnTimer = 1.5f;
                }
                break;

            case 3:
                result = battle.playerTurn(healMove);
                renderer.playHealSound();
                renderer.playHealAnimation();

                battleMessage = battleStatus(result);

                if (enemy.isAlive() && !battle.isBattleOver()) {
                    enemyTurnTimer = 1.5f;
                }
                break;

            case 4:
                result = battle.playerTurn(specialSkillMove);

                if (specialSkillMove instanceof IntimidateMove
                    && debuffSound != null) {
                    debuffSound.play(0.5f);
                }

                if (specialSkillMove instanceof IntimidateMove) {
                    renderer.playIntimidateAnimation();
                }

                battleMessage = battleStatus(result);

                if (enemy.isAlive() && !battle.isBattleOver()) {
                    enemyTurnTimer = 1.5f;
                }
                break;

            default:
                return;
        }

        System.out.println(result);

        if (selectedMove == 1) {
            System.out.println(
                enemyDisplayName + " HP: " + enemy.getCurrentHp()
            );
        }

        selectedMove = 0;
        waitingToUseMove = false;
        moveDelayTimer = 0f;
        menuState = MenuState.MAIN_MENU;

    }

    private void activateMathEffect(MathMove move) {
        calculatorOriginalAttack = move.getOriginalEnemyAttack();
        playerOriginalAttack = move.getOriginalPlayerAttack();

        mathTurnsRemaining = 3;
        mathEffectActive = true;


        // Show the correct special sprite for the enemy using Math.
        if (round == 5) {
            currentSprite = assets.getMindNek();
        } else {
            currentSprite = assets.getMathmind();
        }

        mathSpriteTimer = MATH_SPRITE_DURATION;
        showingMathSprite = true;


        mathAnimationState = MathAnimationState.ENEMY_BUFF;
        mathAnimationTimer = MATH_ENEMY_EFFECT_DURATION;

        // Play the buff sound when Calculator starts the Math animation.
        if (enemyChargeSound != null) {
            enemyChargeSound.play(0.5f);
        }

        animationTimer = 0f;
        waitingForEnemyAnimation = true;
    }





    // ROUND TRANSITION
    // Enter moves to the NEXT opponent after a victory.
    // PlayerCreature is deliberately NOT recreated here, so any HP lost
    // against Garlic Man or Tung Tung Man remains lost against Calculator.
    // This also leaves room to add your healing mechanic later.
    private void startNextRound() {
        enemyDying = false;
        noteLockTurnsRemaining = 0;
        enemyDeathFadeTimer = 0f;
        enemyFadeTimer = 0f;

        doubleSlashActive = false;
        doubleSlashSecondHitPending = false;
        doubleSlashTimer = 0f;

        lifeStealActive = false;

        if (immortalActive) {
            player.setPermanentDefendReduction(false);
            player.setDefendReduction(immortalOriginalDefendReduction);
        }

        showShield = false;
        player.removeAttackBuff();
        player.removeFireBuff();

        renderer.setImmortalActive(false);
        renderer.setLifeStealActive(false);

        immortalActive = false;
        immortalTurnsRemaining = 0;

        stunActive = false;
        enemyStunned = false;
        enemySkippedTurn = false;
        stunEffectTimer = 0f;

        renderer.drawStunEffect(0f);

        heavyTungAttackPending = false;

        renderer.removeNoteFromSword();
        showShield = false;
        player.removeAttackBuff();
        player.removeFireBuff();
        round++; // 1 -> 2 ->
        waitingForNextRound = false; // Allow normal attack input again.
        enemyDisplayName = EnemyFactory.getEnemyName(round);

        // Fresh enemy + fresh Battle/TurnManager; same player instance.
        enemy = EnemyFactory.createEnemy(round);
        battle = new Battle(player, enemy);
        battle.startBattle();

        // Get the correct opponent's idle image; reset animation delays.
        currentSprite = assets.getIdle(round);
        animationTimer = 0f;
        enemyTurnTimer = 0f;

        // PLAYER MOVE: same placeholder Slash Hit for now.
        // ENEMY MOVE: chosen per opponent by EnemyFactory.


        enemyMoves = EnemyFactory.createEnemyMoves(round);
        enemyMove = enemyMoves[0];

        // Change the music and show the new round's opening message.
        assets.playMusic(round);
        battleMessage = battleStatus("A wild " + enemyDisplayName + " appeared!");
    }


    // DIALOGUE TEXT: show the last action and both HP totals.
    // The message stays visible until the next battle action.

    private String battleStatus(String action) {
        return action;
    }



    private void showLoseScreen() {
        assets.stopMusic();
        game.setScreen(new LoseScreen(game));
        dispose();
    }

}
