package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.Item;

public class Collider implements Component, Pool.Poolable {

    public Item<Entity> item;
    public float width, height;

    @Override
    public void reset() {
        item = null;
    }

    public Collider setWidth(float width) {
        this.width = width;
        return this;
    }

    public Collider setHeight(float height) {
        this.height = height;
        return this;
    }


    public static final ComponentMapper<Collider> mapper = ComponentMapper.getFor(Collider.class);
}
