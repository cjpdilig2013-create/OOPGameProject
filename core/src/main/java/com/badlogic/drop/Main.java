
package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.drop.toon_BattleScreenSystem.BattleScreen;
import com.badlogic.drop.toon_BattleScreenSystem.MenuScreen;

// Main is responsible for starting the game
// and deciding which screen to display.
public class Main extends Game {

    @Override
    public void create() {

        // Start the game by opening the Menu screen.
        setScreen(new MenuScreen(this));
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
