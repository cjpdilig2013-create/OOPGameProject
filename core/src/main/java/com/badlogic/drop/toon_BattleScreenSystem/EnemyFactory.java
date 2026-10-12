package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.drop.ot_MoveSkillsSystem.ChargeMove;
import com.badlogic.drop.ot_MoveSkillsSystem.EnemyBuffMove;

//Enemy Factory is where we create the Spirtes for the Enemies
//This includes there Stats (HP, Attack, etc.) and also there Move names and the damage it does
public class EnemyFactory {

    //Enemy Constructor (Creating stats for the enemies)
    public static Enemy createEnemy(int round) {
        switch (round) {
            case 1: return new Enemy("Garlic Man", 60, 12, 8, 12);
            case 2: return new Enemy("Tung Tung Man", 100, 18, 10, 14);
            case 3: return new Enemy("Calculator", 120, 21, 12, 15);
            case 4: return new Enemy("Angry Book", 140, 24, 14, 16);
            case 5: return new Enemy("Professor Boss", 170, 28, 16, 18);
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }


    //OOP POLYMORPHISM (We inherit the moves and the enemy and use it)
    public static Move createEnemyMove(int round) {
        switch (round) {
            case 1: return new ChargeMove("Garlic Punch", 10, //Garlic Man
                "Garlic Man charges a powerful punch!");
            case 2: return new ChargeMove("Tung Tung Bong", 15, //Tung Tung Man
                "Tung Tung Man charges a powerful attack!");
            case 3:
                return new ChargeMove(
                    "Ultimate Calculation",
                    23,
                    "Calculator charges a powerful attack!"
                );
            case 4: return new ChargeMove("Paper Cut", 21,
                "Angry Book charges a devastating paper cut!");
            case 5: return new ChargeMove("Final Exam", 25,
                "Professor Boss charges the final exam attack!");
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }

    //Displaying the name in the BattleScreen
    public static String getEnemyName(int round) {
        switch (round) {
            case 1: return "Garlic Man";
            case 2: return "Tung Tung Man";
            case 3: return "Calculator";
            case 4: return "Angry Book";
            case 5: return "Professor Boss";
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }

    //The name that will display when the enemy attacks (Ex: Garlic Punch!)
    public static String getMoveName(int round) {
        switch (round) {
            case 1: return "Garlic Punch";
            case 2: return "Tung Tung Bong";
            case 3: return "Calculate";
            case 4: return "Paper Cut";
            case 5: return "Final Exam";
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }

    public static Move[] createEnemyMoves(int round) {
        switch (round) {
            case 1:
                return new Move[] {
                    new DamageMove("Garlic Punch", 10,
                        "Garlic Man uses Garlic Punch!"),
                    new ChargeMove("Mega Garlic Smash", 15,
                        "Garlic Man charges a powerful attack!")
                };

            case 2:
                return new Move[] {
                    new DamageMove("Tung Tung Bong", 15,
                        "Tung Tung Man uses Tung Tung Bong!"),
                    new ChargeMove("Mega Tung Tung Bong", 20,
                        "Tung Tung Man charges a powerful attack!")
                };

            case 3:
                return new Move[] {
                    new DamageMove("Calculate", 18,
                        "Calculator uses Calculate!"),
                    new ChargeMove("Ultimate Calculation", 23,
                        "Calculator charges a powerful attack!")
                };

            case 4:
                return new Move[] {
                    new DamageMove("Paper Cut", 21,
                        "Angry Book uses Paper Cut!"),
                    new ChargeMove("Paper Storm", 26,
                        "Angry Book charges a powerful attack!")
                };

            case 5:
                return new Move[] {
                    new DamageMove("Final Exam", 25,
                        "Professor Boss uses Final Exam!"),
                    new ChargeMove("Ultimate Final Exam", 30,
                        "Professor Boss charges a powerful attack!")
                };

            default:
                throw new IllegalArgumentException(
                    "Unknown round: " + round
                );
        }
    }

}
