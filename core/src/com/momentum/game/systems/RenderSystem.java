package com.momentum.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.momentum.game.components.Renderable;
import com.momentum.game.components.Transform;

public class RenderSystem extends IteratingSystem {

    private final ComponentMapper<Renderable> renderableMapper = ComponentMapper.getFor(Renderable.class);
    private final ComponentMapper<Transform> transformMapper = ComponentMapper.getFor(Transform.class);
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
        Renderable renderable = renderableMapper.get(entity);
        Transform transform = transformMapper.get(entity);
        batch.draw(renderable.texture, transform.position.x, transform.position.y);
    }


}
