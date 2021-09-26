package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class Button implements Component, Pool.Poolable {

    public static final int RESTART = 1;
    public int id;

    public Button setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public void reset() {
        id = -1;
    }

    public static final ComponentMapper<Button> mapper = ComponentMapper.getFor(Button.class);
}
