package com.momentum.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dongbat.jbump.World;
import com.momentum.game.components.Stage;
import com.momentum.game.resources.Resources;
import com.momentum.game.systems.*;

public class Game extends ApplicationAdapter {

    private static final float TILES = 32f;
    private static final float TILES_HEIGHT = 18f;
    private static final float TILE_SIZE = 16f;
    private static final float CAMERA_WIDTH = TILES * TILE_SIZE;
    private static final float CAMERA_HEIGHT = TILES_HEIGHT * TILE_SIZE;
    private FitViewport viewport;
    private PooledEngine engine;
    private World<Entity> world;

    @Override
    public void create() {
        Resources resources = new Resources();
        resources.finishLoading();
        Preferences preferences = Gdx.app.getPreferences("preferences");
        viewport = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT);
        engine = new PooledEngine();
        world = new World<>();

        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new MovableSystem(world));
        engine.addSystem(new PlayerSystem(viewport, world, resources));
        engine.addSystem(new SwitchSystem());
        engine.addSystem(new UiSystem(world, viewport));
        engine.addSystem(new StageSystem(resources, preferences));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new RenderSystem(viewport.getCamera()));
        engine.addSystem(new RenderDebugSystem(viewport.getCamera()));

        engine.addEntity(new Entity().add(engine.createComponent(Stage.class)));
    }

    @Override
    public void render() {
        engine.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        engine.removeAllEntities();
        engine.removeAllEntities();
        engine.clearPools();
        world.reset();
    }
}
