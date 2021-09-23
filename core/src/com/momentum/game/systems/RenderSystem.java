package com.momentum.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.momentum.game.components.Renderable;
import com.momentum.game.components.Transform;

public class RenderSystem extends IteratingSystem {

    private final SpriteBatch batch = new SpriteBatch();
    private final Camera camera;
    private final Color backgroundColor = new Color(29 / 256f, 33 / 256f, 45 / 256f, 1);

    public RenderSystem(Camera camera) {
        super(Family.all(Renderable.class, Transform.class).get());
        this.camera = camera;
    }


    @Override
    public void update(float deltaTime) {
        ScreenUtils.clear(backgroundColor);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Renderable renderable = Renderable.mapper.get(entity);
        Transform transform = Transform.mapper.get(entity);

        float width = renderable.width >= 0 ? renderable.width : renderable.texture.getRegionWidth();
        float height = renderable.height >= 0 ? renderable.height : renderable.texture.getRegionHeight();

        // Fit
        if (width != renderable.texture.getRegionWidth() || height != renderable.texture.getRegionHeight()) {
            float defaultAspectRatio = (float) renderable.texture.getRegionWidth() / renderable.texture.getRegionHeight();
            float aspectRatio = width / height;
            if (aspectRatio > defaultAspectRatio) {
                height = width / defaultAspectRatio;
            } else if (aspectRatio < defaultAspectRatio) {
                width = height * defaultAspectRatio;
            }
        }

        float halfWidth = width / 2f;
        float halfHeight = height / 2f;

        if (renderable.texture != null) {
            batch.setColor(renderable.color);
            batch.draw(renderable.texture,
                    transform.position.x - halfWidth,
                    transform.position.y - halfHeight,
                    halfWidth,
                    halfHeight,
                    width,
                    height,
                    renderable.scale,
                    renderable.scale,
                    renderable.angle
            );
            batch.setColor(Color.WHITE);
        }
    }


}
