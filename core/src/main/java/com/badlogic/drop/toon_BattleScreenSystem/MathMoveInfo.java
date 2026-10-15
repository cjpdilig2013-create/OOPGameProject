package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.ot_MoveSkillsSystem.MathMove;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;

//Info for the Math move (it has a fixed name and effect, so there is nothing to set)
public class MathMoveInfo extends MoveInfo {

    public MathMoveInfo() {
        super("Math");
    }

    @Override
    public Move createMove() {
        return new MathMove();
    }
}
