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
public String playerTurn(Move move) {

    if (move == null) {
        return "Please select a move.";
    }

    if (isBattleOver()) {
        return "Battle is already over.";
    }

    if (!turnManager.isPlayerTurn()) {
        return "It is not the player's turn.";
    }

    String result = useMove(player, enemy, move);

    if (enemy.isAlive()) {
        turnManager.switchToEnemy();
    } else {
        getWinner();
    }

    return result;
}


    //Move turn (Enemy)
    public String enemyTurn(Move move) {

        if (move == null) {
            return "Enemy has no move selected.";
        }

        if (isBattleOver()) {
            return "Battle is already over.";
        }

        if (!turnManager.isEnemyTurn()) {
            return "It is not the enemy's turn.";
        }

        String result = useMove(enemy, player, move);

        if (player.isAlive()) {
            turnManager.switchToPlayer();
        } else {
            getWinner();
        }

        return result;
    }
//--------------------------------------------------------------------------------------------------------------------------------------



    //Makes sure that when we use our move it actually hits our target AKA the enemy
    public String useMove(Creature user, Creature target, Move move) {
        if (move == null) {
            return "No move selected.";
        }

        return move.use(user, target);
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
