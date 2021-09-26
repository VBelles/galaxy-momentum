package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class Button implements Component, Pool.Poolable {

    public static final int RESTART = 1;
    public static final int NEXT = 2;
    public static final int PREVIOUS = 3;
    public int action;

    public Button setAction(int action) {
        this.action = action;
        return this;
    }

    @Override
    public void reset() {
        action = -1;
    }

    public static final ComponentMapper<Button> mapper = ComponentMapper.getFor(Button.class);
}
