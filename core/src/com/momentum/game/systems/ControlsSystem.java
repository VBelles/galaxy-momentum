package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;
import com.momentum.game.PhysicsUtils;
import com.momentum.game.components.Button;
import com.momentum.game.components.Stage;

import java.util.ArrayList;

public class ControlsSystem extends EntitySystem {

    private final World<Entity> world;
    private final Viewport viewport;
    private ImmutableArray<Entity> stageEntities;

    public ControlsSystem(World<Entity> world, Viewport viewport) {
        this.world = world;
        this.viewport = viewport;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        stageEntities = engine.getEntitiesFor(Family.all(Stage.class).get());
    }

    @Override
    public void update(float deltaTime) {
        Entity stageEntity = stageEntities.size() > 0 ? stageEntities.first() : null;
        if (stageEntity == null) {
            return;
        }
        Stage stage = Stage.mapper.get(stageEntity);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            onButtonPressed(Button.RESTART, stage);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            onButtonPressed(Button.NEXT, stage);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            onButtonPressed(Button.PREVIOUS, stage);
        }
        if (!Gdx.input.justTouched()) {
            return;
        }

        ArrayList<Item> items = PhysicsUtils.getTouchedItems(world, viewport);
        for (Item item : items) {
            Entity entity = (Entity) item.userData;
            Button button = Button.mapper.get(entity);
            if (button != null) {
                onButtonPressed(button.action, stage);
            }
        }
    }

    private void onButtonPressed(int button, Stage stage) {
        if (button == Button.RESTART) {
            stage.failure = true;
        } else if (button == Button.NEXT) {
            stage.next = true;
        } else if (button == Button.PREVIOUS) {
            stage.previous = true;
        }
    }


}
