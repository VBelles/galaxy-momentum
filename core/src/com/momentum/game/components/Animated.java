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
    public Scale scale = null;
    public Rotate rotate = null;

    public static class Scale {
        public final float from;
        public final float to;
        public float speed;

        public Scale(float from, float to, float speed) {
            this.from = from;
            this.to = to;
            this.speed = speed;
        }
    }

    public static class Rotate {
        public final float from;
        public final float to;
        public float speed;

        public Rotate(float from, float to, float speed) {
            this.from = from;
            this.to = to;
            this.speed = speed;
        }
    }

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

    public Animated setRotate(Rotate rotate) {
        this.rotate = rotate;
        return this;
    }

    public Animated setScale(Scale scale) {
        this.scale = scale;
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
