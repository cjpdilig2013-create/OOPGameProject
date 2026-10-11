package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.ot_MoveSkillsSystem.DamageMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

// ENEMY FACTORY: enemy stats and attacks live here instead of BattleScreen.
// The numbers use the existing Enemy constructor: name, HP, attack, defense, speed.
public class EnemyFactory {
    public static Enemy createEnemy(int round) {
        if (round == 1) {
            //Create Garlic Man with HP, Attack, Defense and Speed
            return new Enemy("Garlic Man", 60, 12, 8, 12);
        }
        if (round == 2) {
            // Tung Tung Man has more HP and a stronger attack stat.
            return new Enemy("Tung Tung Man", 100, 18, 10, 14);
        }
        if (round == 3) {
            // CALCULATOR (Round 3): harder than Tung Tung, but still a prototype.
            // Change these four numbers later to balance the fight.
            return new Enemy("Calculator", 120, 21, 12, 15);
        }
        throw new IllegalArgumentException("Unknown round: " + round);
    }

    // Different move NAME and POWER, same Move interface and Battle logic.
    public static Move createEnemyMove(int round) {
        if (round == 1) {
            // Garlic Man's attack
            return new DamageMove("Garlic Punch", 10,
                "Garlic Man punches Insecure Sword!");
        }
        if (round == 2) {
            // A more powerful move for Tung Tung Man.
            return new DamageMove("Tung Tung Bong", 15,
                "Tung Tung Man used Tung Tung Bong!");
        }
        if (round == 3) {
            // Placeholder calculator move; rename when you design its actual skills.
            return new DamageMove("Calculate", 18,
                "Calculator used Calculate!");
        }
        throw new IllegalArgumentException("Unknown round: " + round);
    }

    // UI names come from here rather than being hard-coded in BattleScreen.
    public static String getEnemyName(int round) {
        switch (round) {
            case 1: return "Garlic Man";
            case 2: return "Tung Tung Man";
            case 3: return "Calculator";
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }

    // Name used inside the dialogue when an enemy attacks.
    public static String getMoveName(int round) {
        switch (round) {
            case 1: return "Garlic Punch";
            case 2: return "Tung Tung Bong";
            case 3: return "Calculate";
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }
}
