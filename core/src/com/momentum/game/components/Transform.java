package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Transform implements Component, Pool.Poolable {

    public Vector2 position = new Vector2();

    @Override
    public void reset() {
        position.setZero();
    }

    public Transform setPosition(float x, float y) {
        position.set(x, y);
        return this;
    }

    public static final ComponentMapper<Transform> mapper = ComponentMapper.getFor(Transform.class);
}
