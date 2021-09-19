package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Player implements Component, Pool.Poolable {

    public Vector2 direction = new Vector2();
    public float speed;
    public Vector2 startDrag = new Vector2();
    public boolean dragging = false;

    @Override
    public void reset() {
        direction.setZero();
        speed = 0f;
        startDrag.setZero();
        dragging = false;
    }

    public Player setDirection(float x, float y) {
        direction.set(x, y);
        return this;
    }

    public Player setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public static final ComponentMapper<Player> mapper = ComponentMapper.getFor(Player.class);
}
