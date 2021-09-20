package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.momentum.game.StageLoader;
import com.momentum.game.components.Stage;
import com.momentum.game.components.Tag;
import com.momentum.game.resources.Resources;

public class StageSystem extends IteratingSystem {

    private final Resources resources;

    private Iterable<Entity> tagEntities;

    public StageSystem(Resources resources) {
        super(Family.all(Stage.class).get());
        this.resources = resources;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        tagEntities = engine.getEntitiesFor(Family.all(Tag.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Stage stage = Stage.mapper.get(entity);
        if (stage.level == -1 || Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            stage.level++;
            loadStage(stage.level - 1, stage.level);
        }

    }

    private void loadStage(int previousLevel, int level) {
        // Clear entities from previous level
        for (Entity tagEntity : tagEntities) {
            Tag tag = Tag.mapper.get(tagEntity);
            if (tag.hasTag(previousLevel)) {
                getEngine().removeEntity(tagEntity);
            }
        }
        // Load new level
        StageLoader.load(getEngine(), resources, level);
    }
}
