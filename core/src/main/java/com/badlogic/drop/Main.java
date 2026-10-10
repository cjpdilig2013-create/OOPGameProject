
package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.drop.toon_BattleScreenSystem.BattleScreen;

// Main is responsible for starting the game
// and deciding which screen to display.
public class Main extends Game {

    @Override
    public void create() {

        // Start the game by opening the battle screen.
        setScreen(new BattleScreen());
    }

    @Override
    public void dispose() {

        // Release the battle screen's resources.
        if (getScreen() != null) {
            getScreen().dispose();
        }

        super.dispose();
    }
}
