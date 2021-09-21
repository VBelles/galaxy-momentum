package com.momentum.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.momentum.game.components.Renderable;
import com.momentum.game.components.Transform;

public class RenderSystem extends IteratingSystem {

    private final SpriteBatch batch = new SpriteBatch();
    private final Camera camera;

    public RenderSystem(Camera camera) {
        super(Family.all(Renderable.class, Transform.class).get());
        this.camera = camera;
    }


    @Override
    public void update(float deltaTime) {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Renderable renderable = Renderable.mapper.get(entity);
        Transform transform = Transform.mapper.get(entity);
        if (renderable.texture != null) {
            if (renderable.angle != 0f) {
                batch.draw(renderable.texture,
                        transform.position.x,
                        transform.position.y,
                        renderable.texture.getRegionWidth() / 2f,
                        renderable.texture.getRegionHeight() / 2f,
                        renderable.texture.getRegionWidth(),
                        renderable.texture.getRegionHeight(),
                        1f,
                        1f,
                        renderable.angle
                );
            } else {
                batch.draw(renderable.texture, transform.position.x, transform.position.y);
            }

        }
    }


}
