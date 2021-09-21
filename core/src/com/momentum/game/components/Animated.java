package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;

public class Animated implements Component, Pool.Poolable {

    public IntMap<Animation<TextureRegion>> animations = new IntMap<>();
    public int currentAnimation = -1;
    public float stateTime = 0;

    @Override
    public void reset() {
        animations.clear();
        currentAnimation = -1;
        stateTime = 0;
    }

    public Animated setCurrentAnimation(int currentAnimation) {
        if (currentAnimation != this.currentAnimation) {
            this.currentAnimation = currentAnimation;
            stateTime = 0;
        }
        return this;
    }

    public boolean isCurrentAnimationFinished() {
        Animation<? extends TextureRegion> animation = animations.get(currentAnimation);
        if (animation != null) {
            return animation.isAnimationFinished(stateTime);
        }
        return false;
    }

    public Animated addAnimation(int key, Animation<TextureRegion> animation) {
        animations.put(key, animation);
        return this;
    }

    public static final ComponentMapper<Animated> mapper = ComponentMapper.getFor(Animated.class);

}
