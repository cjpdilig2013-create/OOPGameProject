package com.badlogic.drop.cj_BattleSystem;

//Importing Creature Stats and Enemy Stats

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class Battle {

    //We store player and enemy so that Battle knows who is currently fighting
    private PlayerCreature player;
    private Enemy enemy;

    //We store turn and result so that Battle knows what is currently happening in the battle
    private TurnManager turnManager;
    private BattleResult battleResult;

    public Battle(PlayerCreature player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;

        //Battle will receive 2 fighters (Enemy and PlayerCreature)
        turnManager = new TurnManager();
        battleResult= new BattleResult();
    }

    //Battle Begins and Player will always start (Just like pokemon)
    public void startBattle() {
        turnManager.switchToPlayer();
    }

    //When the battle is over is the player or enemy dead?
    public boolean isBattleOver() {
        return !player.isAlive() || !enemy.isAlive();
    }

    public boolean isBattleInProgress() {
        return !isBattleOver();
    }

    //Returning who wins
    public String getWinner() {

        if (!enemy.isAlive()) {
            battleResult.playerWins();
            turnManager.finishBattle();
            return "Player";
        } else if (!player.isAlive()) {
            battleResult.enemyWins();
            turnManager.finishBattle();
            return "Enemy";
        } else {
            return "None";
        }
    }


//--------------------------------------------------------------------------------------------------------------------------------------
    //Move turn (Player) (We will use MoveSkillsSystem Import)
    //This is where the player attacks and if the enemy is still alive then switch to the enemy. If not then the battle is finished
    public void playerTurn(Move move) {

        //Prevents a move from being used if a battle is over
        if (isBattleOver()) {
            return;
        }

        if (!turnManager.isPlayerTurn()) {
            return;
        }

        useMove(player , enemy, move);

        if (enemy.isAlive()) {
            turnManager.switchToEnemy();
        } else {
            getWinner();
        }
    }


    //Move turn (Enemy)
    public void enemyTurn(Move move) {

        if (isBattleOver()) {
            return;
        }

        if (!turnManager.isEnemyTurn()) {
            return;
        }

        useMove(enemy, player, move);

        if (player.isAlive()) {
            turnManager.switchToPlayer();
        } else {
            getWinner();
        }
    }
//--------------------------------------------------------------------------------------------------------------------------------------



    //Makes sure that when we use our move it actually hits our target AKA the enemy
    public void useMove(Creature user, Creature target, Move move) {

        //Prevents game from crashing if a move isn't selected
        if (move == null) {
            return;
        }

        move.use(user, target);
    }

    //Now we add getters so that the UI is easier to read
    public PlayerCreature getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public BattleResult getBattleResult() {
        return battleResult;
    }




}
