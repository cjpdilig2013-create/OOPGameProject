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

    private boolean airSlashActive = false;
    private float airSlashTimer = 0f;
    private static final float AIR_SLASH_DURATION = 0.30f;
    private boolean showPlayerSword = true;
    private float swordHideTimer = 0f;
    private static final float SWORD_HIDE_DURATION = 0.35f;

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

    // Reuse the existing battle system
    private PlayerCreature player;
    private Enemy enemy;
    private Battle battle;
    private Move splashHit;
    private Move enemyMove;

    private Move defendMove;
    private Move healMove;
    private Move specialSkillMove;

    private Move[] enemyMoves;

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

    // Show the final boss defeat before the victory screen.
    private boolean victoryPending = false;
    private float victoryTimer = 0f;

    public BattleScreen(Game game) {
        this.game = game;
    }




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
        // Create renderer + assets only once when this battle screen opens.
        renderer = new BattleRenderer();
        assets = new EnemyAssets();
        assets.playMusic(round);
        currentSprite = assets.getIdle(round);

        // PLAYER STATS: create Insecure Sword only once so HP carries between all rounds (healing comes later).
        player = new PlayerCreature("Insecure Sword", 100, 20, 15, 18);
        // EnemyFactory centralizes enemy stats and moves.

        battle = new Battle(player, enemy);
        battle.startBattle();
        //1 move
        splashHit = new DamageMove("Slash Hit", 1000,
            "Insecure Sword attacks Garlic Man!");

        //2 move
        defendMove = new DefendMove(
            "Defend",
            9999,
            "Insecure Sword braces for an incoming attack!"
        );

        //3 move
        healMove = new HealMove(
            "Heal",
            50,
            "Insecure Sword restores 50 HP!"
        );
        //select sound
        buttonSound = Gdx.audio.newSound(
            Gdx.files.internal("effect/ButtonSoundEffect.mp3")
        );

        specialSkillMove = new SpecialSkillMove();
        // Create the enemy's two moves
        enemyMoves = EnemyFactory.createEnemyMoves(round);
        enemyMove = enemyMoves[0];
        battleMessage = battleStatus("A wild Garlic Man appeared!");

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
            renderer.render(
                assets, currentSprite, player, enemy, round,
                battleMessage, showPlayerSword, airSlashActive,
                1f, false, showShield
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
            if (waitingForNextRound && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
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
                || menuState == MenuState.FIGHT_MENU)
                && !waitingForNextRound
                && !allRoundsWon
                && !waitingToUseMove
                && !battle.isBattleOver()
                && player.isAlive()
                && (menuState != MenuState.FIGHT_MENU
                || battle.getTurnManager().isPlayerTurn())) {

                updateChoiceNavigation();
            }

            if (menuState == MenuState.MAIN_MENU) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    playButtonSound();

                    switch (selectedChoice) {
                        case 0:
                            menuState = MenuState.FIGHT_MENU;
                            selectedChoice = 0;
                            battleMessage = "Choose your move!";
                            break;

                        case 1:
                            menuState = MenuState.NOTE_CONFIRM;
                            selectedNoteChoice = 0;
                            battleMessage = "Use NOTE?";
                            break;

                        case 2:
                            menuState = MenuState.STATUS;
                            battleMessage =
                                "Player HP: " + player.getCurrentHp()
                                    + " | Enemy HP: " + enemy.getCurrentHp()
                                    + " | Press ESC to return.";
                            break;

                        case 3:
                            menuState = MenuState.SURRENDER_CONFIRM;
                            battleMessage =
                                "Surrender? Press Y to confirm or N to cancel.";
                            break;
                    }
                }

            } else if (menuState == MenuState.NOTE_CONFIRM) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)
                    || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    selectedNoteChoice = 1 - selectedNoteChoice;
                    playButtonSound();
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    playButtonSound();

                    if (selectedNoteChoice == 0) {
                        if (renderer.attachNoteToSword()) {
                            battleMessage = "You used NOTE!";
                        } else {
                            battleMessage = "You have no NOTE left!";
                        }
                    } else {
                        battleMessage = "You chose not to use NOTE.";
                    }


                    menuState = MenuState.MAIN_MENU;
                    selectedChoice = 1;
                }

            } else if (menuState == MenuState.STATUS) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    menuState = MenuState.MAIN_MENU;
                    selectedChoice = 2;
                    battleMessage = "Choose your action.";
                }
            } else if (menuState == MenuState.SURRENDER_CONFIRM) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
                    showLoseScreen();
                    return;

                } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                    menuState = MenuState.MAIN_MENU;
                    selectedChoice = 3;
                    battleMessage = "You chose to continue fighting.";
                }

            } else if (menuState == MenuState.FIGHT_MENU) {
                if (!waitingForNextRound
                    && !allRoundsWon
                    && !waitingToUseMove
                    && battle.getTurnManager().isPlayerTurn()
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





        } // End of player input handling.

        if (waitingToUseMove) {
            moveDelayTimer -= delta;

            if (moveDelayTimer <= 0f) {
                executeSelectedMove();
            }
        }


        if (animationTimer > 0f) {

            //Subtract the time that passed since the previous frame
            animationTimer -= delta;

            //Once time runs out, return to Idle
            if (animationTimer <= 0f) {
                currentSprite = assets.getIdle(round);
                animationTimer = 0f;
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
                if (!(enemyMove instanceof ChargeMove
                    && ((ChargeMove) enemyMove).isCharged())) {
                    enemyMove = enemyMoves[random.nextInt(enemyMoves.length)];
                }

                String result = battle.enemyTurn(enemyMove);
                boolean enemyIsCharging = result.contains("is charging up");

                if (enemyIsCharging) {
                    renderer.playEnemyBuffAnimation();
                }
                // A charging turn does not consume the shield.

                boolean playerWasDamaged =
                    player.getCurrentHp() < previousPlayerHp;

                if (!enemyIsCharging && shieldWasActive) {
                    // The shield blocks the incoming attack.
                    renderer.playBlockSound();
                    showShield = false;
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
                battleMessage = battleStatus(result);

                if (enemyIsCharging) {
                    // Play debuff sound when the enemy starts charging.
                    debuffSound.play(0.3f);

                    currentSprite = assets.getIdle(round);
                    animationTimer = 0f;
                } else {
                    // Play attack effects when the enemy releases the attack.
                    assets.playEnemyAttack(round);
                    currentSprite = assets.getAttack(round);
                    animationTimer = 0.75f;
                }

                if (!player.isAlive()) {
                    playerDefeated = true;
                    defeatTimer = DEFEAT_DELAY;
                    waitingToUseMove = false;
                    enemyTurnTimer = 0f;
                    battleMessage = battleStatus("Insecure Sword fainted!");
                }

                System.out.println(
                    "Sword HP: " + player.getCurrentHp()
                );

            }
        }
        if (airSlashActive) {
            airSlashTimer -= delta;

            if (airSlashTimer <= 0f) {
                airSlashActive = false;
                airSlashTimer = 0f;
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
        if (playerHitFlashTimer > 0f) {
            playerHitFlashTimer -= delta;

            if (playerHitFlashTimer < 0f) {
                playerHitFlashTimer = 0f;
            }
        }
        boolean playerHitFlash = playerHitFlashTimer > 0f;




        renderer.setCurrentMenu(menuState.name());
        renderer.setSelectedChoice(selectedChoice);
        // RENDERING: hand all graphics to the renderer, leaving BattleScreen
        // responsible only for inputs, turns, and round progression.
        renderer.render(
            assets,
            currentSprite,
            player,
            enemy,
            round,
            battleMessage,
            showPlayerSword, airSlashActive, enemyAlpha,
            playerHitFlash, showShield
        );


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
    }

    private void executeSelectedMove() {
        String result;

        switch (selectedMove) {
            case 1:
                result = battle.playerTurn(splashHit);

                airSlashActive = true;
                airSlashTimer = AIR_SLASH_DURATION;

                int index = random.nextInt(slashSounds.length);
                slashSounds[index].play();

                showPlayerSword = false;
                swordHideTimer = SWORD_HIDE_DURATION;

                if (enemy.isAlive()) {
                    currentSprite = assets.getHurt(round);
                    animationTimer = 0.4f;
                    enemyTurnTimer = 1.5f;
                    enemyFadeTimer = ENEMY_FADE_DURATION;
                } else {
                    currentSprite = assets.getDead(round);
                    assets.stopMusic();
                    assets.playEnemyDefeated();
                    animationTimer = 0f;
                    enemyTurnTimer = 0f;

                    if (round < 5) {
                        waitingForNextRound = true;
                        String nextName =
                            EnemyFactory.getEnemyName(round + 1);

                        battleMessage = battleStatus(
                            "YOU WIN! " + enemyDisplayName
                                + " defeated!\nNEXT: " + nextName
                                + " - Press ENTER"
                        );
                    } else {
                        allRoundsWon = true;
                        victoryPending = true;
                        victoryTimer = 2.0f;
                        battleMessage = battleStatus("Professor Nek defeated!");
                    }
                }

                if (enemy.isAlive()) {
                    battleMessage =
                        battleStatus("Insecure Sword used Slash Hit!");
                }
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
                renderer.playBuffAnimation();

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



    // ROUND TRANSITION
    // Enter moves to the NEXT opponent after a victory.
    // PlayerCreature is deliberately NOT recreated here, so any HP lost
    // against Garlic Man or Tung Tung Man remains lost against Calculator.
    // This also leaves room to add your healing mechanic later.
    private void startNextRound() {
        renderer.removeNoteFromSword();
        showShield = false;
        player.removeAttackBuff();
        round++; // 1 -> 2 -> 3
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
        String enemyHp = enemyDisplayName + " HP: "
            + enemy.getCurrentHp() + "/" + enemy.getMaxHp();

        String playerHp = "Sword HP: "
            + player.getCurrentHp() + "/" + player.getMaxHp();

        return action + "\n" + enemyHp + "    |    " + playerHp;
    }


    private void showLoseScreen() {
        assets.stopMusic();
        game.setScreen(new LoseScreen(game));
        dispose();
    }

}
