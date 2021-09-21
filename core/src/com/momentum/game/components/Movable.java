package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;

public class Movable implements Component, Pool.Poolable {

    public ArrayList<Vector2> path = new ArrayList<>();
    public boolean cyclic = true;
    public float speed = 50;
    public int nextPoint = 0;
    public int increment = 1;
    public Vector2 dir = new Vector2();

    @Override
    public void reset() {
        path.clear();
        cyclic = true;
        speed = 50f;
        nextPoint = 0;
        increment = 1;
        dir.setZero();
    }

    public static final ComponentMapper<Movable> mapper = ComponentMapper.getFor(Movable.class);
}
