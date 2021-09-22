package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class Renderable implements Component, Pool.Poolable {

    public TextureRegion texture;
    public float width = -1;
    public float height = -1;
    public float angle = 0f;

    @Override
    public void reset() {
        texture = null;
        width = -1;
        height = -1;
        angle = 0f;
    }

    public Renderable setTexture(TextureRegion texture) {
        this.texture = texture;
        return this;
    }

    public Renderable setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Renderable setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    public static final ComponentMapper<Renderable> mapper = ComponentMapper.getFor(Renderable.class);
}
