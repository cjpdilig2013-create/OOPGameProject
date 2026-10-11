
package com.badlogic.drop.ot_MoveSkillsSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

public class ChargeMove extends Move {

    private boolean charged = false;

    public ChargeMove(String name, int power, String description) {
        super(name, power, description);
    }

    @Override
    public String use(Creature user, Creature target) {

        // First turn: charge without dealing damage.
        if (!charged) {
            charged = true;

            return user.getName()
                + " is charging up " + getName() + "!\n"
                + "No damage this turn.";
        }

        // Next turn: calculate normal damage.
        int baseDamage = getPower()
            + user.getAttack()
            - target.getDefense();

        int randomBonus = (int) (Math.random() * 11) - 5;

        int normalDamage = Math.max(0, baseDamage + randomBonus);

        // Apply the user's existing attack buff.
        normalDamage = user.getBuffedDamage(normalDamage);

        // Charged attack deals 10 times the calculated damage.
        int damage = normalDamage * 10;

        target.takeDamage(damage);

        // Reset so the enemy must charge again.
        charged = false;

        return user.getName() + " used " + getName() + "!\n"
            + "CHARGED DAMAGE: " + damage + "\n"
            + target.getName() + " HP: "
            + target.getCurrentHp() + "/" + target.getMaxHp();
    }
    public boolean isCharged() {
        return charged;
    }
}
