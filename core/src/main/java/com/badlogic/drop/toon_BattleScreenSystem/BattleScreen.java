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

    private float playerDeathFadeTimer = 0f;
    private static final float PLAYER_DEATH_FADE_DURATION = 1.5f;
    private boolean playerDying = false;

    // Show the final boss defeat before the victory screen.
    private boolean victoryPending = false;
    private float victoryTimer = 0f;


    public BattleScreen(Game game) {
        this.game = game;
    }

    private float enemyDeathFadeTimer = 0f;
    private static final float ENEMY_DEATH_FADE_DURATION = 1.2f;
    private boolean enemyDying = false;
    private float menuHideTimer = 0f;
    private static final float MENU_HIDE_DURATION = 0.3f;




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
        splashHit = new DamageMove("Slash Hit", 10,
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
                || battle.getTurnManager().isPlayerTurn())) {

                if (menuState == MenuState.SURRENDER_CONFIRM) {
                    updateSurrenderNavigation();
                } else {
                    updateChoiceNavigation();
                }
            }



            if (menuState == MenuState.MAIN_MENU
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
                            menuState = MenuState.NOTE_CONFIRM;
                            selectedNoteChoice = 0;
                            battleMessage = "Use NOTE?";
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
                        if (renderer.getAttachedNoteCount() < round) {

                            // Open the text input dialog.
                            menuState = MenuState.MAIN_MENU;

                            startNoteTyping();

                        } else {
                            battleMessage = "No more NOTE available this round!";
                            menuState = MenuState.MAIN_MENU;
                        }
                    } else {
                        battleMessage = "You chose not to use NOTE.";
                        menuState = MenuState.MAIN_MENU;
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
                animationTimer = 0f;
                waitingForEnemyAnimation = false;
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


                System.out.println("Turn before enemy action: "
                    + battle.getTurnManager().getCurrentTurn());

                String result = battle.enemyTurn(enemyMove);

                System.out.println("DEBUG ENEMY RESULT: [" + result + "]");

                System.out.println("Enemy result: " + result);
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
                battleMessage = result;

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
        boolean playerHitFlash = playerHitFlashTimer > 0f;




        renderer.setCurrentMenu(menuState.name());
        renderer.setSelectedChoice(selectedChoice);
        // RENDERING: hand all graphics to the renderer, leaving BattleScreen
        // responsible only for inputs, turns, and round progression.

        boolean canShowMenu =
            !waitingForEnemyAnimation
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


    }

    private void startNoteTyping() {
        typingNote = true;
        noteInput.setLength(0);

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (!typingNote) {
                    return false;
                }

                if (character == '\n' || character == '\r') {
                    typingNote = false;
                    Gdx.input.setInputProcessor(null);
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
    }


    private void useNoteWord(String text) {
        if (!renderer.attachNoteToSword()) {
            battleMessage = "No more NOTE available!";
            return;
        }

        if (text != null && "Fire".equalsIgnoreCase(text.trim())) {
            player.activateFireBuff();
            battleMessage = "The NOTE awakened! Fire power activated!";
        } else {
            battleMessage = "The NOTE accepted: " + text;
        }

        // NOTE consumes the player's turn.
        if (enemy.isAlive() && !battle.isBattleOver()) {
            enemyTurnTimer = 1.5f;
            waitingForEnemyAnimation = false;
            animationTimer = 0f;
            battle.endPlayerTurn();
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
                    waitingForEnemyAnimation = true;
                    menuHideTimer = MENU_HIDE_DURATION;
                    enemyTurnTimer = 1.5f;
                    enemyFadeTimer = ENEMY_FADE_DURATION;
                }
                else {
                    currentSprite = assets.getDead(round);
                    assets.stopMusic();
                    assets.playEnemyDefeated();
                    animationTimer = 0f;
                    enemyTurnTimer = 0f;

                    enemyDying = true;
                    enemyDeathFadeTimer = ENEMY_DEATH_FADE_DURATION;
                    // Gain 1 NOTE when the enemy is defeated.

                    if (round < 5) {
                        waitingForNextRound = true;
                        String nextName =
                            EnemyFactory.getEnemyName(round + 1);

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
        enemyDying = false;
        enemyDeathFadeTimer = 0f;
        enemyFadeTimer = 0f;

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
