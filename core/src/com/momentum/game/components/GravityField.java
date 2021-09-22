package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class GravityField implements Component, Pool.Poolable {

    public boolean constantField = false;// true is always pulling

    public float minPull = 200;// lerped from min to max depending on distance
    public float maxPull = 500;

    public boolean active = false;// set by clicking on it unless constant field

    public GravityField setConstantField(boolean constantField) {
        this.constantField = constantField;
        return this;
    }

    public GravityField setMinPull(float minPull) {
        this.minPull = minPull;
        return this;
    }

    public GravityField setMaxPull(float maxPull) {
        this.maxPull = maxPull;
        return this;
    }

    @Override
    public void reset() {
        constantField = false;
        active = false;
        maxPull = 500;
    }

    public static final ComponentMapper<GravityField> mapper = ComponentMapper.getFor(GravityField.class);
}
