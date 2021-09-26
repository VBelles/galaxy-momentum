package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class Stage implements Component, Pool.Poolable {

    public int level = -1;
    public boolean failure = false;
    public boolean next = false;
    public boolean previous = false;

    public Stage setLevel(int level) {
        this.level = level;
        return this;
    }

    @Override
    public void reset() {
        level = -1;
        failure = false;
        next = false;
        previous = false;
    }

    public static final ComponentMapper<Stage> mapper = ComponentMapper.getFor(Stage.class);
}
