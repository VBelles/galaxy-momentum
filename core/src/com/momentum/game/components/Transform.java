package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Transform implements Component, Pool.Poolable {

    public Vector2 position = new Vector2();
    public float angle = 0f;

    @Override
    public void reset() {
        position.setZero();
        angle = 0f;
    }

    public Transform setPosition(float x, float y) {
        position.set(x, y);
        return this;
    }

    public Transform setAngle(float angle) {
        this.angle = angle;
        return this;
    }
}
