
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;
import java.util.Random;

public class IntimidateMove extends Move {

    private final Random random = new Random();

    public IntimidateMove() {
        super("Intimidate", 0,
            "Reduces enemy Attack by 10-20% for the rest of battle.");
    }

    @Override
    public String use(Creature user, Creature target) {
        int originalAttack = target.getAttack();

        // Random reduction: 10%, 11%, ... 20%.
        int reductionPercent = 10 + random.nextInt(11);

        int reducedAttack = (int) Math.floor(
            originalAttack * (100 - reductionPercent) / 100.0
        );

        target.setAttack(reducedAttack);

        return user.getName() + " used Intimidate!"
            + "\n" + target.getName() + "'s Attack fell by "
            + reductionPercent + "%!"
            + "\nAttack: " + originalAttack
            + " -> " + reducedAttack;
    }
}
