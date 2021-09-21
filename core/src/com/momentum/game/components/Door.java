package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class Door implements Component, Pool.Poolable {

    public String name = null;

    @Override
    public void reset() {
        name = null;
    }

    public Door setName(String name) {
        this.name = name;
        return this;
    }

    public static final ComponentMapper<Door> mapper = ComponentMapper.getFor(Door.class);

}
