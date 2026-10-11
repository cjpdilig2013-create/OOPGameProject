package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.drop.ot_MoveSkillsSystem.ChargeMove;
import com.badlogic.drop.ot_MoveSkillsSystem.EnemyBuffMove;

// ENEMY FACTORY: creates enemy stats, signature attacks and display names.
// BattleScreen asks this class for the current round's opponent.
public class EnemyFactory {

    // Enemy constructor: name, HP, attack, defense, speed.
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

    // POLYMORPHISM: all signature attacks use the shared Move interface.
    // DamageMove constructor: move name, power, description.

    public static Move createEnemyMove(int round) {
        switch (round) {
            case 1: return new ChargeMove("Garlic Punch", 10,
                "Garlic Man charges a powerful punch!");
            case 2: return new ChargeMove("Tung Tung Bong", 15,
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

    // Name displayed in the dialogue box and victory screen.
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

    // Name displayed when the current enemy attacks.
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
