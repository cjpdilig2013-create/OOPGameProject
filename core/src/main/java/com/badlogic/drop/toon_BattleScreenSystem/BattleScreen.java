package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.drop.beach_CreatureStatsSystem.Creature;
import com.badlogic.drop.cj_BattleSystem.Battle;
import com.badlogic.drop.ot_MoveSkillsSystem.Move;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//BattleScreen (simple demo version, no images)
//Layout:   Enemy name + HP (top left)          Player name + HP (right)
//          Dialogue box (bottom left)          Buttons FIGHT / BAG / PARTY / RUN (bottom right)
//
//It only calls CJ's Battle class, all the battle logic stays in Battle.
//
//Example (in Main, which must "extends Game"):
//    Battle battle = new Battle(playerCreature, enemy);
//    setScreen(new BattleScreen(battle, playerMoves, enemyMoves));
public class BattleScreen extends ScreenAdapter implements BattleMenu.Listener {

    private final Battle battle;
    private final List<Move> playerMoves;   // moves shown after pressing FIGHT
    private final List<Move> enemyMoves;    // moves the enemy picks from (random)

    private final OrthographicCamera camera = new OrthographicCamera();
    private final FitViewport viewport = new FitViewport(800, 480, camera);
    private final ShapeRenderer shape = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final Random random = new Random();

    private final DialogueBox dialogue = new DialogueBox(10, 10, 460, 130);
    private final BattleMenu menu = new BattleMenu(480, 10, 310, 130, this);

    private boolean inMoveMenu = false;   // false = FIGHT/BAG/PARTY/RUN, true = list of moves

    public BattleScreen(Battle battle, List<Move> playerMoves, List<Move> enemyMoves) {
        this.battle = battle;
        this.playerMoves = playerMoves;
        this.enemyMoves = enemyMoves;
        font.getData().setScale(1.2f);

        battle.startBattle();
        showMainMenu("Battle start!");
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    //These 4 small methods are the ONLY place that touches Beach's and OT's code.
    //When their classes are finished, check these names. If one is different, fix it here only.
    private String creatureName(Creature c) { return c.getName(); }     // Beach: Creature.getName()
    private int hpOf(Creature c)            { return c.getHp(); }       // Beach: Creature.getHp()
    private int maxHpOf(Creature c)         { return c.getMaxHp(); }    // Beach: Creature.getMaxHp()
    private String moveName(Move m)         { return m.getName(); }     // OT: Move.getName()
//--------------------------------------------------------------------------------------------------------------------------------------

    //---------------- Menus ----------------

    private void showMainMenu(String message) {
        inMoveMenu = false;
        String question = "What will " + creatureName(battle.getPlayer()) + " do?";
        dialogue.setText(message.isEmpty() ? question : message + "\n" + question);

        List<String> labels = new ArrayList<>();
        labels.add("FIGHT");
        labels.add("BAG");
        labels.add("PARTY");
        labels.add("RUN");
        menu.setButtons(labels);
    }

    private void showMoveMenu() {
        inMoveMenu = true;
        dialogue.setText("Choose a move!");

        List<String> labels = new ArrayList<>();
        for (Move move : playerMoves) {
            labels.add(moveName(move));
        }
        labels.add("BACK");
        menu.setButtons(labels);
    }

    //A button was clicked (index = position of the button)
    @Override
    public void onButtonClicked(int index) {
        if (!inMoveMenu) {
            if (index == 0) {
                showMoveMenu();                                  // FIGHT
            } else if (index == 1) {
                dialogue.setText("Bag: Coming Soon");            // BAG
            } else if (index == 2) {
                dialogue.setText("Party: Coming Soon");          // PARTY
            } else {
                Gdx.app.exit();                                  // RUN
            }
        } else {
            if (index == playerMoves.size()) {
                showMainMenu("");                                // BACK
            } else {
                useMove(playerMoves.get(index));                 // a move
            }
        }
    }

    //---------------- Battle ----------------

    //Player uses a move, then the enemy uses a random move
    private void useMove(Move playerMove) {
        String message = creatureName(battle.getPlayer()) + " used " + moveName(playerMove) + "!";
        battle.playerTurn(playerMove);

        if (!battle.isBattleOver()) {
            Move enemyMove = enemyMoves.get(random.nextInt(enemyMoves.size()));
            message += "\n" + creatureName(battle.getEnemy()) + " used " + moveName(enemyMove) + "!";
            battle.enemyTurn(enemyMove);
        }

        if (battle.isBattleOver()) {
            dialogue.setText(message + "\n" + battle.getWinner() + " wins!");
            menu.hide();
        } else {
            showMainMenu(message);
        }
    }

    //---------------- Drawing ----------------

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            Vector2 click = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            menu.handleClick(click.x, click.y);
        }

        Creature player = battle.getPlayer();
        Creature enemy = battle.getEnemy();

        ScreenUtils.clear(0.8f, 0.92f, 0.8f, 1f);
        viewport.apply();
        shape.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        //Boxes
        shape.begin(ShapeRenderer.ShapeType.Filled);
        drawInfoBox(40, 390, 300, 70);      // enemy
        drawInfoBox(460, 170, 320, 70);     // player
        dialogue.drawShapes(shape);
        menu.drawShapes(shape);
        shape.end();

        //Text
        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, creatureName(enemy) + "\nHP " + hpOf(enemy) + " / " + maxHpOf(enemy), 54, 448);
        font.draw(batch, creatureName(player) + "\nHP " + hpOf(player) + " / " + maxHpOf(player), 474, 228);
        dialogue.drawText(batch, font);
        menu.drawText(batch, font);
        batch.end();
    }

    private void drawInfoBox(float x, float y, float w, float h) {
        shape.setColor(Color.DARK_GRAY);
        shape.rect(x, y, w, h);
        shape.setColor(Color.WHITE);
        shape.rect(x + 3, y + 3, w - 6, h - 6);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
    }
}
