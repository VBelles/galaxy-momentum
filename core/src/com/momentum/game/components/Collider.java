package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.Item;

public class Collider implements Component, Pool.Poolable {

    public Item<Entity> item;
    public boolean isSensor = false;
    public Vector2 offset = new Vector2();
    public float width = 0;
    public float height = 0;

    @Override
    public void reset() {
        item = null;
        isSensor = false;
        offset.set(0f, 0f);
    }

    public Collider setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Collider setOffset(float offsetX, float offsetY) {
        this.offset.set(offsetX, offsetY);
        return this;
    }

    public Collider setSensor(boolean sensor) {
        isSensor = sensor;
        return this;
    }

    public static final ComponentMapper<Collider> mapper = ComponentMapper.getFor(Collider.class);
}
