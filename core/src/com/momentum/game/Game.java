package com.momentum.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.momentum.game.components.Player;
import com.momentum.game.components.Renderable;
import com.momentum.game.components.Transform;
import com.momentum.game.resources.Resources;
import com.momentum.game.systems.PlayerSystem;
import com.momentum.game.systems.RenderSystem;

public class Game extends ApplicationAdapter {

    private static final float CAMERA_WIDTH = 800F;
    OrthographicCamera camera;
    PooledEngine engine;

    @Override
    public void create() {
        Resources resources = new Resources();
        resources.finishLoading();
        camera = new OrthographicCamera();
        engine = new PooledEngine();

        engine.addSystem(new PlayerSystem());
        engine.addSystem(new RenderSystem(camera));

        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(0, 0)
                        .setAngle(0)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(new TextureRegion(resources.get(resources.player)))
                )
                .add(engine.createComponent(Player.class)
                        .setSpeed(50)
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

    }
}
