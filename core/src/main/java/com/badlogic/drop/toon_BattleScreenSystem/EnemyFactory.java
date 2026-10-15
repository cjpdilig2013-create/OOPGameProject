package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

import java.util.Arrays;
import java.util.List;

//Enemy Factory is where we create the Enemies for each round
//This includes their Stats (HP, Attack, etc.) and also their Move names and the damage they do
//To add a new enemy, just add one more EnemyInfo to the ENEMIES list below (round = place in the list)
public class EnemyFactory {

    //Round 1 = first enemy, round 2 = second enemy, and so on
    private static final List<EnemyInfo> ENEMIES = Arrays.asList(
        new EnemyInfo("Garlic Man", 60, 12, 5, 10, new MoveInfo[] {
            new DamageMoveInfo("Garlic Punch", 10, "Garlic Man uses Garlic Punch!")
        }),

        new EnemyInfo("Tung Tung Man", 100, 18, 6, 10, new MoveInfo[] {
            new DamageMoveInfo("Tung Tung Bong", 15, "Tung Tung Man uses Tung Tung Bong!"),
            new ChargeMoveInfo("Mega Tung Tung Bong", 20, "Tung Tung Man charges a powerful attack!")
        }),

        new EnemyInfo("Calculator", 120, 20, 10, 10, new MoveInfo[] {
            new DamageMoveInfo("Calculate", 18, "Calculator uses Calculate!"),
            new MathMoveInfo()
        }),

        new EnemyInfo("Angry Book", 150, 22, 12, 10, new MoveInfo[] {
            new DamageMoveInfo("Paper Cut", 21, "Angry Book uses Paper Cut!"),
            new MindLockMoveInfo("Mind Lock", 0, "Angry Book uses Mind Lock!")
        }),

        new EnemyInfo("Professor Boss", 200, 25, 15, 10, new MoveInfo[] {
            new DamageMoveInfo("Final Exam", 25, "Professor Boss uses Final Exam!"),
            new ChargeMoveInfo("Ultimate Final Exam", 30, "Professor Boss charges the final exam attack!"),
            new MathMoveInfo(),
            new MindLockMoveInfo("Mind Lock", 0, "Professor Nek uses Mind Lock!")
        })
    );

    //Finds the enemy for a round
    private static EnemyInfo getInfo(int round) {
        if (round < 1 || round > ENEMIES.size()) {
            throw new IllegalArgumentException("Unknown round: " + round);
        }
        return ENEMIES.get(round - 1);
    }

    //How many rounds the game has
    public static int getRoundCount() {
        return ENEMIES.size();
    }

    //Enemy Constructor (Creating stats for the enemies)
    public static Enemy createEnemy(int round) {
        return getInfo(round).createEnemy();
    }

    //OOP POLYMORPHISM (DamageMove, ChargeMove, MathMove... are all Moves, so they go in the same array)
    public static Move[] createEnemyMoves(int round) {
        return getInfo(round).createMoves();
    }

    //Displaying the name in the BattleScreen
    public static String getEnemyName(int round) {
        return getInfo(round).getName();
    }

    //The name that will display when the enemy attacks (Ex: Garlic Punch!)
    public static String getMoveName(int round) {
        return getInfo(round).getFirstMoveName();
    }

}
