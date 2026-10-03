package com.badlogic.drop.cj_BattleSystem;

public class BattleResult {

    //BattleResult tells us the result of the Battle
    //Is the Battle in Progress?
    //Did the Enemy or Player win?

    private String result;

    public BattleResult() {
        result = "In Progress";
    }

    public void playerWins() {
        result = "Player Wins!";
    }

    public void enemyWins() {
        result = "Enemy Wins!";
    }

    public String getResult() {
        return result;
    }
}


