package com.momentum.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.momentum.game.components.Animated;
import com.momentum.game.components.Renderable;

public class AnimationSystem extends IteratingSystem {


    public AnimationSystem() {
        super(Family.all(Animated.class, Renderable.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Animated animated = Animated.mapper.get(entity);
        Renderable renderable = Renderable.mapper.get(entity);
        Animation<TextureRegion> animation = animated.animations.get(animated.currentAnimation);
        if (animation != null) {
            renderable.texture = animation.getKeyFrame(animated.stateTime, true);
            animated.stateTime += deltaTime;
        }
    }

}
