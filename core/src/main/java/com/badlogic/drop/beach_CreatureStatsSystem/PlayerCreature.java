import com.badlogic.drop.beach_CreatureStatsSystem.Creature;

packagepackage com.badlogic.drop.beach_CreatureStatsSystem;

/**
 * A creature controlled by the player (e.g. Liquid Cat, Insecure Sword).
 */
public class PlayerCreature extends Creature {
    private int exp;

    public PlayerCreature(String name, int level, int maxHp, int attack, int defense, int speed) {
        super(name, level, maxHp, attack, defense, speed);
        this.exp = 0;
    }

    public int getExp() { return exp; }

    /** Level up every 100 exp; stats grow a little each level. */
    public void gainExp(int amount) {
        if (amount <= 0) return;
        exp += amount;
        while (exp >= 100) {
            exp -= 100;
            levelUp();
        }
    }

    private void levelUp() {
        setLevel(getLevel() + 1);
        stats.setMaxHp(stats.getMaxHp() + 10);
        stats.setAttack(stats.getAttack() + 2);
        stats.setDefense(stats.getDefense() + 2);
        stats.setSpeed(stats.getSpeed() + 1);
        if (isAlive()) {
            heal(10); // fill the extra max HP, but never revive a fainted creature
        }
    }
}
