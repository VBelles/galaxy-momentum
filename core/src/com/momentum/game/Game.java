package com.momentum.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.dongbat.jbump.World;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Player;
import com.momentum.game.components.Renderable;
import com.momentum.game.components.Transform;
import com.momentum.game.resources.Resources;
import com.momentum.game.systems.PhysicsSystem;
import com.momentum.game.systems.PlayerSystem;
import com.momentum.game.systems.RenderSystem;

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

        engine.addSystem(new PlayerSystem(camera, world));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new RenderSystem(camera));


        TiledMap map = resources.get(resources.map);
        MapLoader.load(map, engine);

        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(50, 50)
                        .setAngle(0)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(new TextureRegion(resources.get(resources.player)))
                )
                .add(engine.createComponent(Player.class)
                        .setSpeed(100)
                )
                .add(engine.createComponent(Collider.class)
                        .setWidth(resources.get(resources.player).getWidth())
                        .setHeight(resources.get(resources.player).getHeight())
                )
        );
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
