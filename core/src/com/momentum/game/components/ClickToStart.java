package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

public class ClickToStart implements Component, Pool.Poolable {

     @Override
    public void reset() {}

    public static final ComponentMapper<ClickToStart> mapper = ComponentMapper.getFor(ClickToStart.class);
}
