package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class Goal implements Component, Pool.Poolable {

    public boolean achieved = false;

    @Override
    public void reset() {
        achieved = false;
    }

    public static final ComponentMapper<Goal> mapper = ComponentMapper.getFor(Goal.class);
}
