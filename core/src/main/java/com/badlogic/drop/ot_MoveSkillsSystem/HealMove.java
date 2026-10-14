
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;
import java.util.Random;

public class HealMove extends Move {

    private final Random random = new Random();

    public HealMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {
        int healAmount = 20 + random.nextInt(21);

        int actualHeal = user.heal(healAmount);

        if (actualHeal <= 0) {
            return user.getName() + " used " + getName()
                + ", but recovered no HP!";
        }

        return user.getName() + " used " + getName()
            + " and recovered " + actualHeal + " HP!";
    }
}
