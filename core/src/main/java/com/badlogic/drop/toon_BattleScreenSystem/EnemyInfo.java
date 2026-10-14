package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Enemy;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

//Info for one enemy (Stats + Moves). EnemyFactory keeps a list of these, one for each round
public class EnemyInfo {
    private final String name;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int speed;
    private final MoveInfo[] moves;

    public EnemyInfo(String name, int maxHp, int attack, int defense, int speed, MoveInfo[] moves) {
        this.name = name;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.moves = moves;
    }

    public String getName() {
        return name;
    }

    //The name of the first move (the normal attack)
    public String getFirstMoveName() {
        return moves[0].getName();
    }

    //Creates a new Enemy with the stats from this info
    public Enemy createEnemy() {
        return new Enemy(name, maxHp, attack, defense, speed);
    }

    //Creates new Moves (DamageMove and ChargeMove are both Moves, so they fit in one array)
    public Move[] createMoves() {
        Move[] result = new Move[moves.length];
        for (int i = 0; i < moves.length; i++) {
            result[i] = moves[i].createMove();
        }
        return result;
    }
}
