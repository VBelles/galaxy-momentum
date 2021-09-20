package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;

public class Tag implements Component, Pool.Poolable {

    private List<Integer> tags = new ArrayList<>();

    public boolean hasTag(int tag) {
        return tags.contains(tag);
    }

    public Tag addTag(int tag) {
        tags.add(tag);
        return this;
    }

    public Tag removeTag(int tag) {
        tags.remove(tag);
        return this;
    }

    @Override
    public void reset() {
        tags = new ArrayList<>();
    }

    public static final ComponentMapper<Tag> mapper = ComponentMapper.getFor(Tag.class);
}
