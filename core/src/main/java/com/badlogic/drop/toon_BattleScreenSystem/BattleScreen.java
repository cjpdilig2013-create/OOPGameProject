package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.cj_BattleSystem.Battle;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;
import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;

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
    private float enemyTurnTimer = 0f;

    // Reuse CJ's existing battle system for damage, HP, and turns.
    private PlayerCreature player;
    private Enemy enemy;
    private Battle battle;
    // POLYMORPHISM: both attacks use the shared Move interface.
    private Move splashHit;
    private Move enemyMove;

    @Override
    public void show() {
        // Create renderer + assets only once when this battle screen opens.
        renderer = new BattleRenderer();
        assets = new EnemyAssets();
        assets.playMusic(round);
        currentSprite = assets.getIdle(round);

        // PLAYER STATS: create Insecure Sword only once so HP carries between all rounds (healing comes later).
        player = new PlayerCreature("Insecure Sword", 100, 20, 15, 18);
        // EnemyFactory centralizes enemy stats and moves.
        enemy = EnemyFactory.createEnemy(round);
        battle = new Battle(player, enemy);
        battle.startBattle();
        splashHit = new DamageMove("Splash Hit", 10,
            "Insecure Sword attacks Garlic Man!");
        enemyMove = EnemyFactory.createEnemyMove(round);
        battleMessage = battleStatus("A wild Garlic Man appeared!");
    }

    @Override
    public void render(float delta) {

        // Show the victory message until Enter is pressed.
        if (waitingForNextRound && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startNextRound();
        }

        // KEYBOARD CONTROLS: 1 uses Splash Hit. Keys 2-4 preview enemy sprites.
        // These extra keys are temporary debugging shortcuts, not real moves.
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {

            // Only attack if it is the player's turn
            if (!waitingForNextRound && !allRoundsWon
                && battle.getTurnManager().isPlayerTurn()
                && !battle.isBattleOver()) {

                // Execute the attack using CJ's Battle System
                String result = battle.playerTurn(splashHit);

                // Show the attack result in IntelliJ's console
                System.out.println(result);
                battleMessage = battleStatus("Insecure Sword used Splash Hit!");

                // Display the current enemy's remaining HP
                System.out.println(
                    enemyDisplayName + " HP: " + enemy.getCurrentHp()
                );

                // Change the sprite depending on whether the current enemy survived

                // If the enemy survives, play the Hurt animation
                if (enemy.isAlive()) {
                    currentSprite = assets.getHurt(round);
                    animationTimer = 0.4f;

                    // Schedule the enemy counterattack
                    enemyTurnTimer = 1.5f; // Wait 1.5 seconds before the enemy counters

                } else {
                    // The enemy is defeated, so no counterattack occurs
                    currentSprite = assets.getDead(round);
                    assets.stopMusic();
                    assets.playEnemyDefeated();
                    animationTimer = 0f;
                    enemyTurnTimer = 0f;
                    // ROUND PROGRESSION: let the player read the victory message.
                    // We do NOT heal the sword; its current HP carries forward.
                    if (round < 3) {
                        waitingForNextRound = true;
                        String nextName = EnemyFactory.getEnemyName(round + 1);
                        battleMessage = battleStatus("YOU WIN! " + enemyDisplayName
                            + " defeated!\nNEXT: " + nextName + " - Press ENTER");
                    } else {
                        // For this prototype, Calculator is the final opponent.
                        allRoundsWon = true;
                        battleMessage = battleStatus(
                            "YOU WIN! Calculator defeated!\nPrototype complete!");
                    }
                }

            }
        }

        if (!waitingForNextRound && !allRoundsWon && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentSprite = assets.getAttack(round);
            animationTimer = 0.4f;
        }

        if (!waitingForNextRound && !allRoundsWon && Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            currentSprite = assets.getHurt(round);
            animationTimer = 0.4f;
        }

        if (!waitingForNextRound && !allRoundsWon && Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            currentSprite = assets.getDead(round);
            animationTimer = 0.0f;
        }

        //Count down only when an animation is active
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
                if (enemy.isAlive()
                    && battle.getTurnManager().isEnemyTurn()
                    && !battle.isBattleOver()) {

                    // Execute this enemy's move using CJ's battle system
                    String result = battle.enemyTurn(enemyMove);

                    System.out.println(result);
                    assets.playEnemyAttack(round);
                    // The move name now comes from EnemyFactory, so we do not
                    // need to add a new condition every time a round is added.
                    battleMessage = battleStatus(enemyDisplayName + " used "
                        + EnemyFactory.getMoveName(round) + "!");
                    if (!player.isAlive()) {
                        battleMessage = battleStatus("Insecure Sword fainted! Game over.");
                        assets.stopMusic();
                    }

                    // Display Insecure Sword's remaining HP
                    System.out.println(
                        "Sword HP: " + player.getCurrentHp()
                    );

                    // Play the current enemy's attack animation
                    currentSprite = assets.getAttack(round);
                    animationTimer = 0.75f; // Let the attack animation linger
                }
            }
        }



        // RENDERING: hand all graphics to the renderer, leaving BattleScreen
        // responsible only for inputs, turns, and round progression.
        renderer.render(assets, currentSprite, player, enemy, round, battleMessage);
    }

    // RESIZE: tell renderer to adjust its projection + top-left dialogue position.
    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    // DISPOSE: each owner disposes only its own GPU/audio resources.
    @Override
    public void dispose() {
        renderer.dispose();
        assets.dispose();
    }

    // ROUND TRANSITION
    // Enter moves to the NEXT opponent after a victory.
    // PlayerCreature is deliberately NOT recreated here, so any HP lost
    // against Garlic Man or Tung Tung Man remains lost against Calculator.
    // This also leaves room to add your healing mechanic later.
    private void startNextRound() {
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

        // PLAYER MOVE: same placeholder Splash Hit for now.
        // ENEMY MOVE: chosen per opponent by EnemyFactory.
        splashHit = new DamageMove("Splash Hit", 10,
            "Insecure Sword attacks " + enemyDisplayName + "!");
        enemyMove = EnemyFactory.createEnemyMove(round);

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

}
