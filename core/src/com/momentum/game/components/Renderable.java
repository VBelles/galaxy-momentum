package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class Renderable implements Component, Pool.Poolable {

    public TextureRegion texture;
    public float width = -1;
    public float height = -1;
    public float angle = 0f;
    public float scale = 1f;
    public Color color = Color.WHITE;

    @Override
    public void reset() {
        texture = null;
        width = -1;
        height = -1;
        angle = 0f;
        scale = 1f;
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

    public Renderable setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public Renderable setColor(Color color) {
        this.color = color;
        return this;
    }

    public static final ComponentMapper<Renderable> mapper = ComponentMapper.getFor(Renderable.class);
}
