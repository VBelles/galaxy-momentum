package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class Renderable implements Component, Pool.Poolable {

    public TextureRegion texture;

    @Override
    public void reset() {
        texture = null;
    }

    public Renderable setTexture(TextureRegion texture) {
        this.texture = texture;
        return this;
    }

    public static final ComponentMapper<Renderable> mapper = ComponentMapper.getFor(Renderable.class);
}
