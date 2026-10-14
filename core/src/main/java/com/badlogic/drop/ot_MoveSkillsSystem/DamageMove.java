package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;
import com.badlogic.drop.beach_CreatureStatsSystem.PlayerCreature;


public class DamageMove extends Move {

    private String lastResult;
    private int lastDamage;

    public DamageMove(String name, int power, String description) {
        super(name, power, description);
    }


    @Override
    public String use(Creature user, Creature target) {
        int baseDamage = getPower()
            + user.getAttack()
            - target.getDefense();

        // Random value from -5 to +5
        int randomBonus = (int) (Math.random() * 11) - 5;

        int damage = baseDamage + randomBonus;

        // Damage cannot be lower than 0
        damage = Math.max(0, damage);

        // Apply 50% attack buff if active
        // Apply the existing attack buff first
        damage = user.getBuffedDamage(damage);

        // Apply Fire NOTE: double damage
        if (user instanceof PlayerCreature
            && ((PlayerCreature) user).isFireBuffActive()) {
            damage *= 2;
        }

        int actualDamage = target.takeDamage(damage);
        lastDamage = actualDamage;

        lastResult = user.getName() + " used " + getName()
            + "!"
            + "\nDamage: " + actualDamage
            + "\n" + target.getName() + " HP: "
            + target.getCurrentHp() + "/" + target.getMaxHp();

        return lastResult;
    }

    public int getLastDamage() {
        return lastDamage;
    }
}
