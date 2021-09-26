package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;
import com.momentum.game.PhysicsUtils;
import com.momentum.game.components.Button;
import com.momentum.game.components.Stage;

import java.util.ArrayList;

public class UiSystem extends EntitySystem {

    private final World<Entity> world;
    private final Viewport viewport;
    private ImmutableArray<Entity> stageEntities;

    public UiSystem(World<Entity> world, Viewport viewport) {
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
        if (!Gdx.input.justTouched()) {
            return;
        }
        Entity stageEntity = stageEntities.size() > 0 ? stageEntities.first() : null;
        if (stageEntity == null) {
            return;
        }
        ArrayList<Item> items = PhysicsUtils.getTouchedItems(world, viewport);
        for (Item item : items) {
            Entity entity = (Entity) item.userData;
            Button button = Button.mapper.get(entity);
            if (button != null) {
                onButtonPressed(button, Stage.mapper.get(stageEntity));
            }
        }
    }

    private void onButtonPressed(Button button, Stage stage) {
        if (button.id == Button.RESTART) {
            stage.failure = true;
        }
    }


}
