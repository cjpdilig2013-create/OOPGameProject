package com.badlogic.drop.toon_BattleScreenSystem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

//DialogueBox = the text box at the bottom-left of the battle screen
//Example: "What will Liquid Cat do?"
public class DialogueBox {

    private final float x, y, width, height;
    private String text = "";

    public DialogueBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setText(String text) {
        this.text = text;
    }

    //Draw the box (call between shape.begin() and shape.end())
    public void drawShapes(ShapeRenderer shape) {
        shape.setColor(Color.DARK_GRAY);                 // border
        shape.rect(x, y, width, height);
        shape.setColor(Color.WHITE);                     // inside
        shape.rect(x + 4, y + 4, width - 8, height - 8);
    }

    //Draw the text (call between batch.begin() and batch.end())
    public void drawText(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.BLACK);
        font.draw(batch, text, x + 14, y + height - 14, width - 28, Align.left, true);
    }
}
