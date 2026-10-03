package com.badlogic.drop.cj_BattleSystem;

public class TurnManager {

    //TurnManager is where we determine who's turn it is.
    //TurnManager is responsible for the logic of the current battle state (The complicated code will be in battle.java)

    //Logic works like this Battle Starts -> TurnManger says Player turn -> Player uses a move -> TurnManger says Enemy turn -> Enemy uses a move -> and so on (loop until 0HP)

    private String currentTurn;

    //Battle starts with "Player"
    public TurnManager() {
        currentTurn = "Player";
    }

    public boolean isPlayerTurn() {
        return currentTurn.equals("Player");
    }

    public boolean isEnemyTurn() {
        return currentTurn.equals("Enemy");
    }

    //After enemy is done attacking switch to player
    public void switchToPlayer() {
        currentTurn = "Player";
    }

    //After player is done attacking switch to enemy
    public void switchToEnemy() {
        currentTurn = "Enemy";
    }

    //If Player or Enemy reaches 0HP the battle should end AKA finished
    public void finishBattle() {
        currentTurn = "Finished";
    }

    //Can later access in the UI or Battle
    public String getCurrentTurn() {
        return currentTurn;
    }
}
