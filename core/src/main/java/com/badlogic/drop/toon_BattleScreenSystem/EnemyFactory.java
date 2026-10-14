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
        new EnemyInfo("Garlic Man", 60, 12, 8, 12, new MoveInfo[] {
            damage("Garlic Punch", 10, "Garlic Man uses Garlic Punch!"),
            charge("Mega Garlic Smash", 15, "Garlic Man charges a powerful attack!")
        }),

        new EnemyInfo("Tung Tung Man", 100, 18, 10, 14, new MoveInfo[] {
            damage("Tung Tung Bong", 15, "Tung Tung Man uses Tung Tung Bong!"),
            charge("Mega Tung Tung Bong", 20, "Tung Tung Man charges a powerful attack!")
        }),

        new EnemyInfo("Calculator", 120, 21, 12, 15, new MoveInfo[] {
            damage("Calculate", 18, "Calculator uses Calculate!"),
            charge("Ultimate Calculation", 23, "Calculator charges a powerful attack!")
        }),

        new EnemyInfo("Angry Book", 140, 24, 14, 16, new MoveInfo[] {
            damage("Paper Cut", 21, "Angry Book uses Paper Cut!"),
            charge("Paper Storm", 26, "Angry Book charges a powerful attack!")
        }),

        new EnemyInfo("Professor Boss", 170, 28, 16, 18, new MoveInfo[] {
            damage("Final Exam", 25, "Professor Boss uses Final Exam!"),
            charge("Ultimate Final Exam", 30, "Professor Boss charges a powerful attack!")
        })
    );

    //Short names so the list above is easy to read
    private static MoveInfo damage(String name, int power, String description) {
        return new MoveInfo(name, power, description, false);
    }

    private static MoveInfo charge(String name, int power, String description) {
        return new MoveInfo(name, power, description, true);
    }

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

    //OOP POLYMORPHISM (DamageMove and ChargeMove are both Moves, so they go in the same array)
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
