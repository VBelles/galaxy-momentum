package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class GravityField implements Component, Pool.Poolable {

    public boolean constantField = false;// true is always pulling

    // only when not constant, debug variable for testing which option is better
    // in the final game all of them should be true or false
    public boolean toggleable = false;

    public float minPull = 200;// lerped from min to max depending on distance
    public float maxPull = 500;

    private boolean active = false;// set by clicking on it unless constant field

    public GravityField setConstantField(boolean constantField) {
        this.constantField = constantField;
        return this;
    }

    public GravityField setToggleable(boolean toggleable) {
        this.toggleable = toggleable;
        return this;
    }

    public GravityField setMaxPull(float maxPull) {
        this.maxPull = maxPull;
        return this;
    }

    @Override
    public void reset() {
        constantField = false;
        toggleable = false;
        active = false;
        maxPull = 500;
    }

    public float getPull(){
        return maxPull;
    }

    public static final ComponentMapper<GravityField> mapper = ComponentMapper.getFor(GravityField.class);
}
