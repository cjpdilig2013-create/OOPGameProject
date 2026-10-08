package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class DamageMove extends Move {

    private String lastResult;

    public DamageMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {
        int baseDamage = getPower() + user.getAttack() - target.getDefense();

// Random value from -5 to +5
        int randomBonus = (int) (Math.random() * 11) - 5;

        int damage = baseDamage + randomBonus;

// Damage cannot be lower than 1
        damage = Math.max(1, damage);

        target.takeDamage(damage);

        lastResult = user.getName() + " used " + getName()
            + "!"
            + "\nDamage: " + damage
            + "\n" + target.getName() + " HP: "
            + target.getCurrentHp() + "/" + target.getMaxHp();

        return lastResult;
    }

    public String getLastResult() {
        return lastResult;
    }
}
