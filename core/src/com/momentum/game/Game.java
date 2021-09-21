package com.momentum.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.dongbat.jbump.World;
import com.momentum.game.components.Stage;
import com.momentum.game.resources.Resources;
import com.momentum.game.systems.*;

public class Game extends ApplicationAdapter {

    private static final float CAMERA_WIDTH = 768F;
    private OrthographicCamera camera;
    private PooledEngine engine;
    private World<Entity> world;

    @Override
    public void create() {
        Resources resources = new Resources();
        resources.finishLoading();
        camera = new OrthographicCamera();
        engine = new PooledEngine();
        world = new World<>();

        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PlayerSystem(camera, world));
        engine.addSystem(new StageSystem(resources));
        engine.addSystem(new MovableSystem(world));
        engine.addSystem(new RenderSystem(camera));
        engine.addSystem(new RenderDebugSystem(camera));

        engine.addEntity(new Entity().add(engine.createComponent(Stage.class)));
    }

    @Override
    public void render() {
        engine.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, CAMERA_WIDTH, (CAMERA_WIDTH / width) * height);
    }

    @Override
    public void dispose() {
        engine.removeAllEntities();
        engine.removeAllEntities();
        engine.clearPools();
        world.reset();
    }
}
