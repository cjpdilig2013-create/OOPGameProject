
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class MathMove extends Move {

    private int originalEnemyAttack;
    private int originalPlayerAttack;
    private boolean used = false;

    public MathMove() {
        super(
            "Math",
            0,
            "Doubles Calculator's Attack and halves the player's Attack."
        );
    }

    @Override
    public String use(Creature user, Creature target) {
        if (used) {
            return user.getName() + " has already used Math!";
        }

        originalEnemyAttack = user.getAttack();
        originalPlayerAttack = target.getAttack();

        user.setAttack(originalEnemyAttack * 2);
        target.setAttack(Math.max(1, originalPlayerAttack / 2));

        used = true;

        return user.getName()
            + " used Math!\n"
            + user.getName() + "'s Attack doubled!\n"
            + target.getName() + "'s Attack was halved!";
    }

    public int getOriginalEnemyAttack() {
        return originalEnemyAttack;
    }

    public int getOriginalPlayerAttack() {
        return originalPlayerAttack;
    }

    public boolean hasBeenUsed() {
        return used;
    }
}
