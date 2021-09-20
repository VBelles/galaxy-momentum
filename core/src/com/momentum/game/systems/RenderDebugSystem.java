package com.momentum.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Transform;

public class RenderDebugSystem extends IteratingSystem {

    private final ShapeRenderer renderer = new ShapeRenderer();
    private final Camera camera;
    private boolean enabled = false;

    public RenderDebugSystem(Camera camera) {
        super(Family.all(Collider.class).get());
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            enabled = !enabled;
        }
        if (enabled) {
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            super.update(deltaTime);
            renderer.end();
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = Collider.mapper.get(entity);
        Transform transform = Transform.mapper.get(entity);
        renderer.rect(transform.position.x, transform.position.y, collider.width, collider.height);
    }


}
