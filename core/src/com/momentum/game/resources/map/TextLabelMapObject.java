package com.momentum.game.resources.map;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

public class TextLabelMapObject  extends MapObject {

    private Rectangle rectangle;
    private String text;

    /** @return rectangle shape */
    public Rectangle getRectangle () {
        return rectangle;
    }
    /** @return text as string */
    public String getText () {
        return text;
    }

    /** Creates a rectangle object which lower left corner is at (0, 0) with width=1 and height=1 */
    public TextLabelMapObject () {
        this("", 0.0f, 0.0f, 1.0f, 1.0f);
    }

    /** Creates a {@link Rectangle} object with the given X and Y coordinates along with a given width and height.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width of the {@link Rectangle} to be created.
     * @param height Height of the {@link Rectangle} to be created. */
    public TextLabelMapObject (String text, float x, float y, float width, float height) {
        super();
        rectangle = new Rectangle(x, y, width, height);
        this.text = text;
    }
}