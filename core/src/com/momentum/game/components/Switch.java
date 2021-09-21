package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class Switch implements Component, Pool.Poolable {
    public boolean pressed = false;
    public boolean justPressed = false;
    public String name = null;

    @Override
    public void reset() {
        pressed = false;
        justPressed = false;
        name = null;
    }

    public Switch setName(String name) {
        this.name = name;
        return this;
    }

    public static final ComponentMapper<Switch> mapper = ComponentMapper.getFor(Switch.class);
}
