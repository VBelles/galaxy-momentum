package com.momentum.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
        if (animated.rotate != null) {
            renderable.angle += deltaTime * animated.rotate.speed;
            if (renderable.angle >= animated.rotate.to || renderable.angle <= animated.rotate.from) {
                renderable.angle = MathUtils.clamp(renderable.angle,
                        Math.min(animated.rotate.from, animated.rotate.to),
                        Math.max(animated.rotate.from, animated.rotate.to)
                );
            }
        }

        if (animated.scale != null) {
            renderable.scale += deltaTime * animated.scale.speed;
            if (renderable.scale >= animated.scale.to || renderable.scale <= animated.scale.from) {
                renderable.scale = MathUtils.clamp(renderable.scale,
                        Math.min(animated.scale.from, animated.scale.to),
                        Math.max(animated.scale.from, animated.scale.to)
                );
                animated.scale.speed = -animated.scale.speed;
            }
        }
    }

}
