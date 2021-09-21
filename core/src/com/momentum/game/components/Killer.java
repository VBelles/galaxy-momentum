package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class Killer implements Component, Pool.Poolable {

    @Override
    public void reset() {
    }

    public static final ComponentMapper<Killer> mapper = ComponentMapper.getFor(Killer.class);
}
